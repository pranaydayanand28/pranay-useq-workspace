package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import commonutils.GetSmallcaseID;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GetWatchlistAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GetWatchlistAPI.class.getName());

    @Test(testName = "To validate Get watchlist API", description = "Get watchlist API : Should fetch all the watchlist smallcases")
    @Parameters({"broker"})
    public void getWatchlist_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for getWatchlist API - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getWatchlist);

        logger.info("Creating request for getWatchlist API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        if (response.jsonPath().getList("data").size() != 0) {
            assertThat(response.jsonPath().getList("data")).extracting("scid").contains(new GetSmallcaseID().getSCID());
        }
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate schema for Get watchlist API ", description = "Get watchlist API schema validation : Check schema for get watchlist API")
    @Parameters({"broker"})
    public void checkSchema(String broker) throws IOException {

        logger.info("Creating request for getWatchlist API schema validation - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getWatchlist);

        logger.info("Creating request for getWatchlist API schema validation - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasValidSchema(IConst.WATCHLIST_SCHEMA);

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Get watchlist API when no user token is passed", description = "Get watchlist API : To validate Get watchlist API when no user token is passed")
    @Parameters({"broker"})
    public void getWatchlist_shouldReturn401(String broker) throws IOException {

        logger.info("Creating request for getWatchlist API without token - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecificationForUnauthorized()).when().get(SmallcaseResource.getWatchlist);

        logger.info("Creating request for getWatchlist API without token - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .returnsValidErrorMessage(ApiConstants.unAuthorizedMessage);

        logger.info("Test passed for " + broker);
    }


    public static void verifyWatchlistSmallcases(String broker) throws IOException {

        Response response = given().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getWatchlist);
        ResponseAssert.assertThat(response)
                .returns_200_OK();

        if (response.jsonPath().getList("data").size() == 0) {
            logger.info("Test passed for " + broker);
        } else if (response.jsonPath().getList("data").size() != 0) {
            assertThat(response.jsonPath().getList("data")).extracting("scid").doesNotContain(new GetSmallcaseID().getSCID());
        }
    }
}
