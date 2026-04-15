package com.smallboard;

import com.CommonBaseTest;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.DataToShare;
import commonutils.GetSmallcaseID;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class PushRebalance extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PushRebalance.class);

    /**
     * Triggers the push rebalance API for a given userId, iscid, and scid.
     */
    @Test(testName = "Push Rebalance", description = "To push rebalance for a given userId, iscid, and scid")
    @Parameters({"Flow"})
    public void pushRebalance(String Flow) {
        String iscid = (String) DataToShare.getValue("iscid");
        String userId = (String) DataToShare.getValue("userId");
        String scid = new GetSmallcaseID().getSCID();

        logger.info("Triggering Push Rebalance API for userId: {}", userId);

        JSONObject requestBody = new JSONObject();
        requestBody.put("iscid", iscid);
        requestBody.put("userId", userId);
        requestBody.put("scid", scid);

        try {
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.pushRebalanceSpec())
                    .body(requestBody.toString())
                    .log().all()
                    .when()
                    .post(SmallcaseResource.pushRebalance)
                    .then()
                    .log().all()
                    .extract().response();

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

            boolean success = JsonPathFinder.getJsPath(response).getBoolean("success");
            List<String> errors = JsonPathFinder.getJsPath(response).getList("error");

            if (!success && errors != null && !errors.isEmpty()) {
                Assert.fail("Push Rebalance failed: " + errors);
            }

            if (success && (errors == null || errors.isEmpty())) {
                logger.info("Push Rebalance succeeded for userId: {}", userId);
            }

        } catch (Exception e) {
            logger.error("Exception while calling Push Rebalance API: {}", e.getMessage(), e);
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }
}