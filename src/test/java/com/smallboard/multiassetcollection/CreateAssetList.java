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

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateAssetList extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(CreateAssetList.class);
    private JSONObject requestBody;
    public String assetListId;

    // Helper method to generate random assets with at least 2 items
    public List<JSONObject> generateRandomAssets() {
        List<JSONObject> assets = new ArrayList<>();
        String[] assetTypes = {"smallcase", "stock", "mutualFund"};

        int assetCount = new Random().nextInt(6) + 2;
        for (int i = 0; i < assetCount; i++) {
            JSONObject asset = new JSONObject();
            asset.put("assetId", "SC" + RandomStringUtils.randomAlphanumeric(6));
            JSONObject info = new JSONObject();
            info.put("name", "Asset-" + RandomStringUtils.randomAlphabetic(5));
            asset.put("info", info);
            asset.put("assetType", assetTypes[new Random().nextInt(assetTypes.length)]);
            assets.add(asset);
        }
        return assets;
    }

    @Test(priority = 1, testName = "Create Asset List", description = "To validate creating an asset list")
    @Parameters({"Flow"})
    public void createAssetList(String Flow) {
        logger.info("Creating Asset List");

        try {
            // Create request body
            requestBody = new JSONObject();
            requestBody.put("name", "Asset-" + RandomStringUtils.randomAlphabetic(5));
            requestBody.put("description", RandomStringUtils.randomAlphabetic(20));
            requestBody.put("cid", UUID.randomUUID().toString());
            requestBody.put("rank", new Random().nextInt(39) + 1); // Random rank between 1 and 40
            requestBody.put("type", "static");
            requestBody.put("assets", generateRandomAssets()); // Ensure at least 2 assets

            // Send POST request
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .body(requestBody.toString())
                    .when()
                    .post(MultiAssetResource.assetList)
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

            // Store assetListId for future requests
            assetListId = response.jsonPath().getString("data._id");

            // Store the assetListId and response in DataToShare for other classes
            DataToShare.setValue("assetListId", assetListId);
            DataToShare.setValue("createdAssetResponse", response);  // Store the full response

            // Validate response body structure
            response.then()
                    .assertThat()
                    .body("success", equalTo(true))
                    .body("errors.size()", equalTo(0))
                    .body("data.name", equalTo((String) requestBody.get("name")))
                    .body("data.description", equalTo((String) requestBody.get("description")))
                    .body("data.cid", equalTo((String) requestBody.get("cid")))
                    .body("data.rank", equalTo(((Number) requestBody.get("rank")).intValue()))
                    .body("data.type", equalTo((String) requestBody.get("type")))
                    .body("data.assets.size()", greaterThanOrEqualTo(2));

            int assetsSize = response.jsonPath().getList("data.assets").size();
            for (int i = 0; i < assetsSize; i++) {
                response.then()
                        .body("data.assets[" + i + "].assetId", not(isEmptyOrNullString()))
                        .body("data.assets[" + i + "].info.name", not(isEmptyOrNullString()))
                        .body("data.assets[" + i + "].assetType", anyOf(equalTo("smallcase"), equalTo("stock"), equalTo("mutualFund")));
            }

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while creating the asset list: ", e);
            throw e;
        }
    }

    @Test(priority = 2, testName = "Create Asset List with Invalid Data", description = "To validate creating an asset list with invalid/missing data")
    @Parameters({"Flow"})
    public void createAssetListWithInvalidData(String Flow) {
        logger.info("Creating Asset List with Invalid Data");

        try {
            // Create request body with invalid/missing data
            requestBody = new JSONObject();
            requestBody.put("name", ""); // Missing name
            requestBody.put("description", RandomStringUtils.randomAlphabetic(20)); // Valid description
            requestBody.put("cid", "invalid-cid"); // Invalid cid format
            requestBody.put("rank", -1); // Invalid rank, should be between 1 and 40
            requestBody.put("type", "invalidType"); // Invalid type, should be 'static' or 'dynamic'
            requestBody.put("assets", Collections.emptyList()); // No assets provided

            // Send POST request with invalid data
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .body(requestBody.toString())
                    .when()
                    .post(MultiAssetResource.assetList)
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
            logger.error("An error occurred while creating an asset list with invalid data: ", e);
            throw e;
        }
    }
}