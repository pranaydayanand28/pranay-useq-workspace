package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import com.smallcaseapi.payload.AddWatchlistPayload;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static io.restassured.RestAssured.given;

public class RemoveWatchlist extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(RemoveWatchlist.class.getName());

    @Test(testName = "To validate remove watchlist API", description = "Remove watchlist API : to validate remove watchlist API upon valid set of inputs")
    @Parameters({"broker"})
    public void removeWatchlist_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for removeWatchlist API - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
                .body(AddWatchlistPayload.addWatchlistPayload()).when().post(SmallcaseResource.removeWatchlist);

        logger.info("Creating request for removeWatchlist API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        //This method verifies if the watchlist has been removed or not
        GetWatchlistAPI.verifyWatchlistSmallcases(broker);
    }

    @Test(testName = "To validate remove watchlist API with invalid request body", description = "Remove watchlist API : to validate remove watchlist API upon invalid request")
    @Parameters({"broker"})
    public void removeWatchlist_shouldReturn400(String broker) throws IOException {

        logger.info("Creating request for removeWatchlist API with wrong body - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
                .body(AddWatchlistPayload.addWatchListInvalidPayload()).when().post(SmallcaseResource.removeWatchlist);

        logger.info("Creating request for removeWatchlist API with wrong body - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST();

        logger.info("Test passed for "+ broker);
    }

    @Test(testName = "To validate remove watchlist API when no user token is passed", description = "Remove watchlist API : to validate remove watchlist API when no user token is passed")
    @Parameters({"broker"})
    public void removeWatchlist_shouldReturn401(String broker) throws IOException {

        logger.info("Creating request for removeWatchlist API with no token - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecificationForUnauthorized())
                .body(AddWatchlistPayload.addWatchlistPayload()).when().post(SmallcaseResource.removeWatchlist);

        logger.info("Creating request for removeWatchlist API - end with no token " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .returnsValidErrorMessage(ApiConstants.unAuthorizedMessage);

        logger.info("Test passed for "+ broker);
    }

    @Test(testName = "To validate remove watchlist API for invalid SCID", description = "Remove watchlist API : to validate remove watchlist API for invalid SCID")
    @Parameters({"broker"})
    public void removeWatchlistForInvalidScid_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for removeWatchlist API with wrong scid - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
                .body(AddWatchlistPayload.addWatchListInvalidSCID()).when().post(SmallcaseResource.removeWatchlist);

        logger.info("Creating request for removeWatchlist API - end with wrong scid " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();


        logger.info("Test passed for "+ broker);
    }
}
