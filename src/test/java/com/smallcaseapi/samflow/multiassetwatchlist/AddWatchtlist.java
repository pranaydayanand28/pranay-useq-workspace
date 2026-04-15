package com.smallcaseapi.samflow.multiassetwatchlist;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import com.smallcaseapi.payload.AddToWatchlistV2;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class AddWatchtlist extends BaseTest
{

    private static final Logger logger = LoggerFactory.getLogger(AddWatchtlist.class.getName());
    @Test(testName = "Add to Watchllist v2", description = "Add Smallcase to Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public  void AddSmallcaseToWatchlsit(String PhoneNo, String Flow)
    {
        logger.info("Adding Smallcase to Watchlist" + PhoneNo);
        Response AddtoWatchlist = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(AddToWatchlistV2.AddSCIDToWatchlist())
                .when()
                .post(SmallcaseResource.AddWatchlistv2)
                .then()
                .extract().response();

        ResponseAssert.assertThat(AddtoWatchlist)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        writeRequestAndResponseInReport(writer.toString(), AddtoWatchlist.prettyPrint(), Flow);

    }

    @Test(testName = "Add invalid Smallcase to wathlist v2", description = "Add Invalid SCID to Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public  void AddInvalidSmallcaseToWatchlsit(String PhoneNo, String Flow)
    {
        logger.info("Adding Invalid Smallcase to Watchlist" + PhoneNo);
        Response AddtoWatchlist = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(AddToWatchlistV2.AddInvalidSCIDToWatchlist())
                .when()
                .post(SmallcaseResource.AddWatchlistv2)
                .then()
                .extract().response();

        ResponseAssert.assertThat(AddtoWatchlist)
                .returns_404_NOTFOUND()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        writeRequestAndResponseInReport(writer.toString(), AddtoWatchlist.prettyPrint(),Flow);
    }

    @Test(testName = "Add  Stocks to wathlist v2", description = "Add Stock to Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public  void AddStockToWatchlsit(String PhoneNo, String Flow)
    {
        logger.info("Adding Stock to Watchlist" + PhoneNo);
        Response AddtoWatchlist = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(AddToWatchlistV2.AddStockToWatchlist())
                .when()
                .post(SmallcaseResource.AddWatchlistv2)
                .then()
                .extract().response();

        ResponseAssert.assertThat(AddtoWatchlist)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        writeRequestAndResponseInReport(writer.toString(), AddtoWatchlist.prettyPrint(),Flow);
    }

    @Test(testName = "Add MF to watchlist v2", description = "Adding mutual fund to Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public void addMFToWatchlist(String PhoneNo, String Flow) {
        logger.info("Adding MF to Watchlist for Phone Number: " + PhoneNo);

        // Generate the request body and log it for debugging
        String requestBody = AddToWatchlistV2.AddMFToWatchlist();
        logger.info("Request Body: " + requestBody);

        Response addToWatchlistResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .body(requestBody)
                .when()
                .post(SmallcaseResource.AddWatchlistv2);

        logger.info("Response Status Code: " + addToWatchlistResponse.statusCode());
        logger.info("Response Body: " + addToWatchlistResponse.prettyPrint());

        // Assertions with error handling for unexpected status codes
        if (addToWatchlistResponse.statusCode() == 200) {
            ResponseAssert.assertThat(addToWatchlistResponse)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();
        } else {
            logger.error("Expected status code 200 but got " + addToWatchlistResponse.statusCode());
            Assert.fail("The API call failed with status code " + addToWatchlistResponse.statusCode() +
                    ". Check request body and endpoint.");
        }

        writeRequestAndResponseInReport(writer.toString(), addToWatchlistResponse.prettyPrint(), Flow);
    }
}
