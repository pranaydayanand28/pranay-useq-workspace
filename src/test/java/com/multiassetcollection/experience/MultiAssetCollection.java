package com.multiassetcollection.experience;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MultiAssetCollection extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MultiAssetCollection.class);

    @Test(testName = "Get Multi Asset Collection", description = "To validate fetching multi asset collections")
    @Parameters({"Flow"})
    public void fetchMultiAssetCollection(String Flow) {
        String assetListId = (String) DataToShare.getValue("assetListId");
        logger.info("Fetching Multi Asset Collection");
        logger.info("Parameters: Flow = {}", Flow);

        List<Map<String, Object>> mismatchedAssets = new ArrayList<>();

        try {
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.requestSpecificationWithoutBrokerNameInHeaderAndLoggedOutState())
                    .queryParam("type", "static")
                    .when()
                    .get(MultiAssetResource.multiAssetCollection)
                    .then()
                    .extract()
                    .response();

            logger.info("Response: {}", response.prettyPrint());

            // Validate response
            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();

            // Validate main response fields
            response.then()
                    .assertThat()
                    .body("success", equalTo(true))
                    .body("errors.size()", equalTo(0))
                    .body("data.size()", greaterThan(0));

            // Detailed validations for each data item
            List<Map<String, Object>> assetList = response.jsonPath().getList("data");
            for (Map<String, Object> asset : assetList) {
                try {
                    response.then()
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.assetType[0]", anyOf(equalTo("smallcase"), equalTo("stock"), equalTo("mutualFund")))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.name", not(isEmptyOrNullString()))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.slug", not(isEmptyOrNullString()))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.description", not(isEmptyOrNullString()))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.type", equalTo("static"))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.rank", allOf(greaterThan(0), lessThan(40)))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.active", equalTo(true))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.cid", not(isEmptyOrNullString()))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.assets.size()", greaterThanOrEqualTo(2))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.createdAt", not(isEmptyOrNullString()))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.updatedAt", not(isEmptyOrNullString()))
                            .body("data.find { it._id == '" + asset.get("_id") + "' }.__v", equalTo(0));

                    List<Map<String, Object>> assets = (List<Map<String, Object>>) asset.get("assets");
                    for (Map<String, Object> assetDetail : assets) {
                        response.then()
                                .body("data.find { it._id == '" + asset.get("_id") + "' }.assets.find { it.assetId == '" + assetDetail.get("assetId") + "' }.info.name", not(isEmptyOrNullString()))
                                .body("data.find { it._id == '" + asset.get("_id") + "' }.assets.find { it.assetId == '" + assetDetail.get("assetId") + "' }.assetType", anyOf(equalTo("smallcase"), equalTo("stock"), equalTo("mutualFund")));
                    }
                } catch (AssertionError e) {
                    mismatchedAssets.add(asset);  // Collect the mismatched asset
                }
            }

            boolean assetExists = assetList.stream()
                    .anyMatch(asset -> asset.get("_id").equals(assetListId));

            Assert.assertTrue(assetExists, "The asset list should exist in the response data.");

            if (!mismatchedAssets.isEmpty()) {
                String mismatchedAssetsReport = mismatchedAssets.toString();
                logger.error("Mismatched assets found: {}", mismatchedAssetsReport);
                writeRequestAndResponseInReport(writer.toString(), response.prettyPrint() + "\nMismatched Assets: " + mismatchedAssetsReport, Flow);
                Assert.fail("Some assets in the collection did not match expected criteria.");
            } else {
                writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);
            }

        } catch (Exception e) {
            logger.error("An error occurred while fetching multi asset collection: ", e);
            throw e;
        }
    }
}