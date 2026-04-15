package com.smallcaseapi.orderFlow;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import com.smallcaseapi.payload.PartialExitPayload;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import commonutils.WaitMethod;
import commonutils.AssertActions;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import static io.restassured.RestAssured.given;

public class PartialExit extends BaseTest {

    private static String lockedStatus;
    private static Response response;
    private static String iscid;
    private static String batchId;
    private static String lockkey;
    private static final Logger logger = LoggerFactory.getLogger(PartialExit.class.getName());

	@Test(priority = 6, testName = "To validate Partial exit API", description = "Partial exit API : to validate Partial exit API upon valid set of inputs")
    @Parameters({"broker"})
    public static void partialExit(String broker) throws IOException, InterruptedException {

        logger.info("Creating request for Partial exit API - start");

        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .body(PartialExitPayload.partialPayload(broker))
                .when()
                .post(SmallcaseResource.placeOrder)
                .then()
                .extract().response();

        logger.info("Creating request for Partial exit API - end");
        logger.info("Writing Request and Response to Extent Report");

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response");
        AssertActions.verifyStatusCode(response);

        logger.info("Fetching required data & writing iscid/ batchId to the Data Map");

        iscid = JsonPathFinder.getJsPath(response).get("data.iscid").toString();
        batchId = JsonPathFinder.getJsPath(response).get("data.batchId").toString();
        lockkey = JsonPathFinder.getJsPath(response).get("data.lockKey").toString();
        DataToShare.setValue("iscid", iscid);
        DataToShare.setValue("batchId", batchId);
        DataToShare.setValue("lockKey", lockkey);

        logger.info("BatchID generated => " + batchId);
        logger.info("wrote ISCID and batchID successfully !!!!");
        logger.info("Polling place order API to check if the server has validated the order");

        WaitMethod.wait(5);
        StatusBatchIDAPI.statusBatchAPI(broker);
        lockedStatus = (String) DataToShare.getValue("lockedStatus");
        while (lockedStatus.equals("true")) {
            StatusBatchIDAPI.statusBatchAPI(broker);
            lockedStatus = (String) DataToShare.getValue("lockedStatus");
        } if (lockedStatus.equals("ERROR_Locked")) {
            Assert.fail("The check status keeps returning true, scid locked / Check for errors.....");
        }

        logger.info("Polling successful, Order validated successfully with status => " + lockedStatus);

        logger.info("Checking if the order requires repair action or any pending action");
        Assert.assertTrue(UserAction.userAction(broker), "Error during statusBatch API/ RepairAction API");
    }
}