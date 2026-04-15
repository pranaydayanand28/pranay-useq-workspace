package com.smallcaseapi.orderFlow;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import com.smallcaseapi.payload.RebalancePayload;
import commonutils.AssertActions;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import commonutils.WaitMethod;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.smallcaseapi.GetUser.getActionItems;
import static io.restassured.RestAssured.given;

public class RebalanceAPI extends BaseTest {

    private static String lockedStatus;
    private static Response response;
    private static String iscid;
    private static String batchId;
    private static String lockkey;
    private static final Logger logger = LoggerFactory.getLogger(RebalanceAPI.class.getName());

    @Test(priority = 5, testName = "To validate Rebalance API", description = "Rebalance API : to validate Rebalance API upon valid set of inputs")
    @Parameters({"broker"})
    public static void rebalance(String broker) throws IOException, InterruptedException {

        List<HashMap<String, String>> rebalanceItems = getActionItems(broker, "rebalance");

        // Log & Report pending actions before placing order
        logger.info("Rebalance Items (Before Order): {}", rebalanceItems);
        writeRequestAndResponseInReport("Rebalance Items Before Order", rebalanceItems.toString(), broker);

        if (!rebalanceItems.isEmpty()) {

            logger.info("Pending action found for broker: {}", broker);
            logger.info("Creating request for Rebalance order API - start");

            response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.requestSpecification(broker))
                    .body(RebalancePayload.rebalance(broker))
                    .when()
                    .post(SmallcaseResource.placeOrder)
                    .then()
                    .extract().response();

            logger.info("Creating request for place order API with label REBALANCE - end");
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

            logger.info("BatchID generated => {}", batchId);
            logger.info("ISCID and batchID stored successfully");
            logger.info("Polling place order API to check if the server has validated the order");

            WaitMethod.wait(5);

            int retryCount = 0;
            int maxRetries = 10; // prevent infinite loop

            StatusBatchIDAPI.statusBatchAPI(broker);
            lockedStatus = String.valueOf(DataToShare.getValue("lockedStatus"));

            while ("true".equals(lockedStatus) && retryCount < maxRetries) {
                WaitMethod.wait(2);
                StatusBatchIDAPI.statusBatchAPI(broker);
                lockedStatus = String.valueOf(DataToShare.getValue("lockedStatus"));
                retryCount++;
            }

            if ("true".equals(lockedStatus)) {
                Assert.fail("Order status remains locked after max retries. Check for errors.");
            }
            if ("ERROR_Locked".equals(lockedStatus)) {
                Assert.fail("Order is locked due to an error state.");
            }

            logger.info("Polling successful, Order validated successfully with status => {}", lockedStatus);

            logger.info("Checking if the order requires repair action or any pending action");
            Assert.assertTrue(UserAction.userAction(broker), "Error during statusBatch API / RepairAction API");

            List<HashMap<String, String>> rebalanceCheck = getActionItems(broker, "rebalance");

            // Log & Report pending actions after repair action check
            logger.info("Rebalance Items (After Repair Check): {}", rebalanceCheck);
            writeRequestAndResponseInReport("Rebalance Items After Repair Check", rebalanceCheck.toString(), broker);

            Assert.assertTrue(rebalanceCheck.isEmpty(), "Pending action list should be empty but is not");

            if (rebalanceCheck.isEmpty()) {
                logger.info("Pending action deleted successfully");
            } else {
                logger.warn("Pending actions still present after repair action check");
            }

        } else {
            logger.info("No pending action found for broker: {}", broker);
        }
    }
}