package com.smallcaseapi.orderFlow;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import com.smallcaseapi.BaseTest;
import com.smallcaseapi.payload.FixBatchPayload;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import resource.reports.LogStatus;

import java.io.IOException;
import static io.restassured.RestAssured.given;

public class RepairAction extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(RepairAction.class);
    private static String iscid;
    private static String batchId;
    private static String lockedStatus;
    private static String lockeKey;
    private static int count = 0;

    public static void fixBatch(String broker) throws IOException{

        logger.info("Trying to fix batch");
        Response response = given()
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .body(FixBatchPayload.fixBatchPayload())
                .post(SmallcaseResource.fixBatch);

        LogStatus.info("---- Response for fixBatch ---- "+ JsonPathFinder.getJsPath(response).get("data.batchId"));
        formatAPIAndLogInReport(response.prettyPrint());

        logger.info("Polling repair action API to check if the server has validated the order");
        logger.info("Polling repair api returned " + response.getStatusCode() + response.asString());

        if (response.getStatusCode() == ApiConstants.GATEWAY_TIMEOUT && count < 5) {
            fixBatch(broker);
            count++;
        }
        else if (response.getStatusCode()== ApiConstants.success) {
            iscid = JsonPathFinder.getJsPath(response).get("data.iscid").toString();
            batchId = JsonPathFinder.getJsPath(response).get("data.batchId").toString();
            lockeKey = JsonPathFinder.getJsPath(response).get("data.lockKey").toString();

            DataToShare.setValue("iscid", iscid);
            DataToShare.setValue("batchId", batchId);
            DataToShare.setValue("lockKey", lockeKey);

            StatusBatchIDAPI.statusBatchAPI(broker);
            lockedStatus = (String) DataToShare.getValue("lockedStatus");

            while (lockedStatus.equals("true")) {
                StatusBatchIDAPI.statusBatchAPI(broker);
                lockedStatus = (String) DataToShare.getValue("lockedStatus");
            }
             if(lockedStatus.equals("ERROR_Locked")){
                Assert.fail("The check status keeps returning true, scid locked / Check for errors.....");
            }
        }
    }
}
