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
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DeleteAssetList extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DeleteAssetList.class);

    @Test(priority = 1,testName = "Delete Asset List", description = "To validate deletion of an asset list")
    @Parameters({"Flow"})
    public void deleteAssetList(String Flow) {
        String assetListId = (String) DataToShare.getValue("assetListId");
        logger.info("Deleting Asset List: " + assetListId);

        try {
            // Send DELETE request
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .when()
                    .delete(MultiAssetResource.assetList + assetListId)
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

            // Validate successful deletion
            response.then()
                    .assertThat()
                    .body("success", equalTo(true))
                    .body("errors.size()", equalTo(0))
                    .body("data", equalTo(true));
//                    .body("data.deletedCount", equalTo(1));  // Validate successful deletion

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while deleting the asset list: ", e);
            throw e;
        }
    }

    @Test(priority = 2, testName = "Delete Asset List with Invalid ID", description = "To validate deletion of an asset list with an invalid or non-existent ID")
    @Parameters({"Flow"})
    public void deleteAssetListWithInvalidId(String Flow) {
        String invalidAssetListId = (String) DataToShare.getValue("assetListId"); // Using an invalid or non-existent asset list ID
        logger.info("Attempting to delete Asset List with invalid ID: " + invalidAssetListId);

        try {
            // Send DELETE request with invalid asset list ID
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.smallboardSpec())
                    .log().all()
                    .when()
                    .delete(MultiAssetResource.assetList + invalidAssetListId)
                    .then()
                    .log().all()
                    .extract()
                    .response();

            logger.info("Response: " + response.prettyPrint());

            // Assert response status code - expecting 400 or 404 Bad Request/Not Found
            int statusCode = response.getStatusCode();
            // Adjust the expected status code based on the actual API behavior (400 or 404)
            Assert.assertTrue(statusCode == 404, "Expected status code: 404, but found: " + statusCode);

            // Assert that the response indicates a failure
            response.then()
                    .assertThat()
                    .body("success", equalTo(false))  // Expecting success to be false
                    .body("errors.size()", equalTo(1)) // Expecting 1 error in the list
                    .body("errors[0]", equalTo("Resource Not Found"))  // Adjust this message based on actual API response
                    .body("data", equalTo(null)) // Data should be null in case of failure
                    .body("errorType",equalTo("InputException"));

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);

        } catch (Exception e) {
            logger.error("An error occurred while attempting to delete an asset list with an invalid ID: ", e);
            throw e;
        }
    }
}