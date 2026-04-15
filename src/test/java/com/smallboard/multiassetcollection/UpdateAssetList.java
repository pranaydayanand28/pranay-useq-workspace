package com.smallboard.multiassetcollection;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.MultiAssetResource;
import com.smallcase.resource.RequestSpec;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UpdateAssetList extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(UpdateAssetList.class);

    @Test(testName = "Update Asset List", description = "To validate updating asset list")
    @Parameters({"Flow"})
    public void updateAssetList(String Flow) {
        String assetListId = (String) DataToShare.getValue("assetListId");
        logger.info("Updating Asset List: " + assetListId);

        try {
            // Create updated request body
            JSONObject updatedRequestBody = new JSONObject();
            updatedRequestBody.put("name", "UpdatedAsset-" + RandomStringUtils.randomAlphabetic(5));
            updatedRequestBody.put("description", RandomStringUtils.randomAlphabetic(25));
            updatedRequestBody.put("cid", UUID.randomUUID().toString());
            updatedRequestBody.put("rank", new Random().nextInt(39) + 1);  // Random rank between 1 and 40
            updatedRequestBody.put("type", "static");  // Change type for testing
            updatedRequestBody.put("assets", new CreateAssetList().generateRandomAssets());  // Random assets for update

            // Send POST request to update asset list
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .body(updatedRequestBody.toString())
                    .when()
                    .post(MultiAssetResource.assetList + assetListId)
                    .then()
                    .log().all()
                    .extract()
                    .response();

            logger.info("Response: " + response.prettyPrint());

            // Assert response details
            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();

            // Validate response for successful update
            response.then()
                    .assertThat()
                    .body("success", equalTo(true))
                    .body("errors.size()", equalTo(0))
                    .body("data", equalTo(true));
//                    .body("data.modifiedCount", equalTo(1))  // Check if the update was successful
//                    .body("data.matchedCount", equalTo(1));

            // Optionally, validate that the fields were updated correctly by fetching the asset again
            logger.info("Validating updated asset list...");
//            getAssetListById(Flow);  // Call the method to fetch and validate the updated asset list

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while updating the asset list: ", e);
            throw e;
        }
    }

    @Test(priority = 2, testName = "Update Asset List with Invalid Data", description = "To validate updating an asset list with invalid/missing data")
    @Parameters({"Flow"})
    public void updateAssetListWithInvalidData(String Flow) {
        String assetListId = (String) DataToShare.getValue("assetListId");
        logger.info("Updating Asset List with Invalid Data");

        try {
            // Update request body with invalid/missing data
            JSONObject updatedRequestBody = new JSONObject();
            updatedRequestBody.put("name", ""); // Missing name
            updatedRequestBody.put("description", RandomStringUtils.randomAlphabetic(20)); // Valid description
            updatedRequestBody.put("cid", "invalid-cid"); // Invalid cid format
            updatedRequestBody.put("rank", -1); // Invalid rank, should be between 1 and 40
            updatedRequestBody.put("type", "invalidType"); // Invalid type, should be 'static' or 'dynamic'
            updatedRequestBody.put("assets", Collections.emptyList()); // No assets provided

            // Send POST request with invalid data
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .body(updatedRequestBody.toString())
                    .when()
                    .post(MultiAssetResource.assetList + assetListId)
                    .then()
                    .log().all()
                    .extract()
                    .response();

            logger.info("Response: " + response.prettyPrint());

            // Assert response status code - expecting a 400 Bad Request
            int statusCode = response.getStatusCode();
            Assert.assertEquals(statusCode, 400, "Expected status code: 400, but found: " + statusCode);

            // Assert the response matches the expected structure
            response.then()
                    .assertThat()
                    .body("success", equalTo(false))
                    .body("errors.size()", greaterThan(0))
                    .body("errors[0]", not(isEmptyOrNullString())) // Expected error message for invalid rank
                    .body("data", equalTo(null)) // Data should be null in case of failure
                    .body("errorType", equalTo("InputException"));

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while updating an asset list with invalid data: ", e);
            throw e;
        }
    }
}