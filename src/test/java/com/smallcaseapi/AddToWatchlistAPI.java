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
import static io.restassured.RestAssured.given;

public class AddToWatchlistAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AddToWatchlistAPI.class.getName());

    @Test(testName = "To validate Add to watchlist API", description = "Add to Watchlist API : to validate add to watchlist API upon valid set of inputs")
    @Parameters({"broker"})
    public void addWatchlist_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for addWatchlist API - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
                .body(AddWatchlistPayload.addWatchlistPayload()).when().post(SmallcaseResource.addWatchlist);

        logger.info("Creating request for addWatchlist API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        logger.info("Test passed for "+ broker);
    }

    @Test(testName = "To validate Add to watchlist API for invalid request body", description = "Add to Watchlist API : to validate add to watchlist API upon invalid set of inputs")
    @Parameters({"broker"})
    public void addWatchlist_shouldReturn400(String broker) throws IOException {

        logger.info("Creating request for addWatchlist API with wrong body - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
                .body(AddWatchlistPayload.addWatchListInvalidPayload()).when().post(SmallcaseResource.addWatchlist);

        logger.info("Creating request for addWatchlist API with wrong body - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_400_BADREQUEST();

        logger.info("Test passed for "+ broker);
    }

    @Test(testName = "To validate Add to watchlist API when no user token is passed", description = "Add to Watchlist API : to validate add to watchlist API if no user token is passed")
    @Parameters({"broker"})
    public void addWatchlist_shouldReturn401(String broker){

        logger.info("Creating request for addWatchlist API without token - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecificationForUnauthorized())
                .body(AddWatchlistPayload.addWatchlistPayload()).when().post(SmallcaseResource.addWatchlist);

        logger.info("Creating request for addWatchlist API without token - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .returnsValidErrorMessage(ApiConstants.unAuthorizedMessage);

        logger.info("Test passed for "+ broker);
    }

    @Test(testName = "To validate Add to watchlist API for invalid SCID", description = "Add to Watchlist API : to validate add to watchlist API for invalid scid")
    @Parameters({"broker"})
    public void addWatchlist_shouldReturn404(String broker) throws IOException {

        logger.info("Creating request for addWatchlist API with wrong scid - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
                .body(AddWatchlistPayload.addWatchListInvalidSCID()).when().post(SmallcaseResource.addWatchlist);

        logger.info("Creating request for addWatchlist API - end with wrong scid " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_404_NOTFOUND();

        logger.info("Test passed for "+ broker);
    }
}
