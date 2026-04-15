package com.smallcaseapi.orderFlow;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import com.smallcaseapi.BaseTest;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import commonutils.WaitMethod;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.reports.LogStatus;
import static io.restassured.RestAssured.given;

public class StatusBatchIDAPI extends BaseTest {

    private static Response response;
    private static int Count = 0;
    private static int counter_for_currentBatch = 0;
    private static String userId;
    private static final Logger logger = LoggerFactory.getLogger(StatusBatchIDAPI.class);

    @SneakyThrows
    public static void statusBatchAPI(String broker){

        String batchId = (String) DataToShare.getValue("batchId");
        String lockKey = (String) DataToShare.getValue("lockKey");

        DataToShare.putIfAbsent(batchId, 0);
        logger.info("Polling status API to check if order has been validated");

        response = given()
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam("batchId", batchId)
                .queryParam("lockKey", lockKey)
                .when().get(SmallcaseResource.statusBatch);

        LogStatus.info("---- Response for statusBatchAPI for batch --- " + batchId);
        formatAPIAndLogInReport(response.prettyPrint());
        userId = JsonPathFinder.getJsPath(response).get("data.batch.userId").toString();
        DataToShare.setValue("userId", userId);

        counter_for_currentBatch = (Integer) DataToShare.getValue(batchId);

        if (response.getStatusCode() == ApiConstants.GATEWAY_TIMEOUT && Count < 5) {
            statusBatchAPI(broker);
            Count++;
        }

        else if (counter_for_currentBatch>=10){
            DataToShare.setValue("lockedStatus", "ERROR_Locked");
            logger.error("SCID locked because the status API keeps polling true, check for errors in the API......");
        }

        else if (response.getStatusCode() == 200) {
            String lockedStatus = JsonPathFinder.getJsPath(response).get("data.locked").toString();
            if(lockedStatus.equals("true")) {
                DataToShare.setValue(batchId, counter_for_currentBatch+1);
            }

            logger.info("Locked status returned " + lockedStatus);
            DataToShare.setValue("lockedStatus", lockedStatus);
            logger.info("The polling for "+ batchId + " has happened "+ " -> " + DataToShare.getValue(batchId) + " times so far !!!!!");
            WaitMethod.wait(5); //Adding this method because the api gets called very quickly and fails often because we have put a check to poll only 10 times...
        }
    }
}