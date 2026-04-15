package com.smallboard.multiassetcollection;

import com.CommonBaseTest;
import com.smallcase.resource.MultiAssetResource;
import com.smallcase.resource.RequestSpec;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.Map;

public class GetAllAssetList extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GetAllAssetList.class);

    @Test(priority = 1, testName = "Get All Asset List", description = "To validate fetching all asset lists")
    @Parameters({"Flow"})
    public void getAllAssetList(String Flow) {
        String assetListId = (String) DataToShare.getValue("assetListId");
        logger.info("Fetching All Asset List");
        try {
            // Make GET request to fetch all asset lists
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .when()
                    .get(MultiAssetResource.assetList) // Use the appropriate endpoint for fetching all asset lists
                    .then()
                    .log().all()
                    .extract()
                    .response();

            // Validate the response status code
            int statusCode = response.getStatusCode();
            Assert.assertEquals(statusCode, 200, "Expected status code: 200, but found: " + statusCode);

            // Validate the response structure
            boolean success = response.jsonPath().getBoolean("success");
            Assert.assertTrue(success, "Response should indicate success.");

            // Get the asset data from the response
            List<Map<String, Object>> assetList = response.jsonPath().getList("data");

            // Check if the updated asset exists in the asset list
            boolean assetExists = assetList.stream()
                    .anyMatch(asset -> asset.get("_id").equals(assetListId)); // assetListId should hold the ID of the recently created asset list

            Assert.assertTrue(assetExists, "The updated asset list should exist in the response data.");

            logger.info("Successfully validated that the asset list exists.");

            // Write request and response in report
            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);
        } catch (Exception e) {
            logger.error("An error occurred while fetching all asset lists: ", e);
            throw e;
        }
    }

    @Test(priority = 2, testName = "Get All Asset List Unauthorized", description = "To validate fetching all asset lists without authorization")
    @Parameters({"Flow"})
    public void getAllAssetListUnauthorized(String Flow) {
        logger.info("Fetching All Asset List Unauthorized");

        try {
            // Make GET request to fetch all asset lists without authorization
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .header("Authorization", "") // Intentionally empty to simulate unauthorized access
                    .when()
                    .get(MultiAssetResource.assetList) // Use the appropriate endpoint for fetching all asset lists
                    .then()
                    .log().all()
                    .extract()
                    .response();

            // Log the raw response body
            logger.info("Response Body: " + response.asString());

            // Check the content type of the response
            String contentType = response.getContentType();
            logger.info("Content Type: " + contentType);

            if (contentType.contains("application/json")) {
                // If the response is JSON, proceed with JSON assertions
                response.then()
                        .assertThat()
                        .body("success", equalTo(false)) // Expecting success to be false
                        .body("errors.size()", equalTo(1)) // Expecting 1 error in the list
                        .body("errors[0]", equalTo("Authorization header missing")) // The expected error message
                        .body("data", equalTo(null)) // Data should be null in case of failure
                        .body("errorType", equalTo("BadRequest")); // Expecting errorType to be BadRequest
            } else {
                // If the response is not JSON, treat it as plain text (or HTML)
                String responseBody = response.asString();
                Assert.assertTrue(responseBody.contains("400 Bad Request"), "Expected 400 Bad Request message");
            }

            // Write request and response in report
            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while fetching all asset lists unauthorized: ", e);
            throw e;
        }
    }
}