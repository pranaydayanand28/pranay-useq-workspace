package com.smallboard.multiassetcollection;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.MultiAssetResource;
import com.smallcase.resource.RequestSpec;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertEquals;

public class GetAssetListById extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GetAssetListById.class);

    @Test(priority = 1,testName = "Get Asset List by ID", description = "To validate fetching asset list by ID")
    @Parameters({"Flow"})
    public void getAssetListById(String Flow) {
        Response createdAssetResponse = (Response) DataToShare.getValue("createdAssetResponse");
        String assetListId = (String) DataToShare.getValue("assetListId");
        logger.info("Fetching Asset List by ID: " + assetListId);

        try {
            // Send GET request
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .when()
                    .get(MultiAssetResource.assetList + assetListId)
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

            // Validate response body matches the details from the created asset list
            response.then()
                    .assertThat()
                    .body("success", equalTo(true))
                    .body("errors.size()", equalTo(0))
                    .body("data._id", equalTo(assetListId))
                    .body("data.name", equalTo(createdAssetResponse.jsonPath().getString("data.name")))
                    .body("data.description", equalTo(createdAssetResponse.jsonPath().getString("data.description")))
                    .body("data.cid", equalTo(createdAssetResponse.jsonPath().getString("data.cid")))
                    .body("data.rank", equalTo(createdAssetResponse.jsonPath().getInt("data.rank")))
                    .body("data.type", equalTo(createdAssetResponse.jsonPath().getString("data.type")))
                    .body("data.assets.size()", equalTo(createdAssetResponse.jsonPath().getList("data.assets").size()));

            // Validate each asset in the list
            List<Map<String, Object>> createdAssets = createdAssetResponse.jsonPath().getList("data.assets");
            List<Map<String, Object>> fetchedAssets = response.jsonPath().getList("data.assets");

            for (int i = 0; i < createdAssets.size(); i++) {
                Map<String, Object> createdAsset = createdAssets.get(i);
                Map<String, Object> fetchedAsset = fetchedAssets.get(i);

                assertEquals(fetchedAsset.get("assetId"), createdAsset.get("assetId"));
                assertEquals(fetchedAsset.get("info.name"), createdAsset.get("info.name"));
                assertEquals(fetchedAsset.get("assetType"), createdAsset.get("assetType"));
            }

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while fetching the asset list: ", e);
            throw e;
        }
    }

    @Test(priority = 2,testName = "Get Asset List by Invalid ID", description = "To validate fetching asset list by an invalid ID")
    @Parameters({"Flow"})
    public void getAssetListByInvalidId(String Flow) {
        String invalidAssetListId = "123invalid"; // Example of an invalid ID
        logger.info("Fetching Asset List by Invalid ID: " + invalidAssetListId);

        try {
            // Send GET request with an invalid asset list ID
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .when()
                    .get(MultiAssetResource.assetList + invalidAssetListId)
                    .then()
                    .log().all()
                    .extract()
                    .response();

            logger.info("Response: " + response.prettyPrint());

            // Assert response details
            assertEquals(400, response.getStatusCode());

            // Validate the error response body
            response.then()
                    .assertThat()
                    .body("success", equalTo(false))
                    .body("errors.size()", greaterThan(0))
                    .body("errors[0]", equalTo("\"assetId\" must only contain hexadecimal characters")) // Expected error message for invalid asset ID
                    .body("data", equalTo(null)) // Data should be null in case of failure
                    .body("errorType", equalTo("InputException"));

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while fetching the asset list by invalid ID: ", e);
            throw e;
        }
    }
}