package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.enums.QueryParameters;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class Search extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Search.class.getName());
    static Response response;

    @Test(testName = "To validate Search API", description = "Search API : to validate search API upon valid set of inputs")
    @Parameters({"broker"})
    public void search_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for search API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.text.name(), QueryParameters.getSearch_string_value())
                .when().get(SmallcaseResource.search);

        logger.info("Creating request for search API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        int dataArrSize = JsonPathFinder.getJsPath(response).get("data.smallcases.size()");
        for (int i = 0; i < dataArrSize; i++) {
            assertThat(response.jsonPath().getMap("data.smallcases[" + i + "]")).containsKeys("name", "scid", "slug", "description");
        }
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate schema for Search API", description = "Search API schema validation : to validate schema for search API upon valid set of inputs")
    @Parameters({"broker"})
    public void checkSchema(String broker) throws IOException {

        logger.info("Creating request for search API schema validation - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.text.name(), QueryParameters.getSearch_string_value())
                .when().get(SmallcaseResource.search);

        logger.info("Creating request for search API schema validation - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasValidSchema(IConst.SEARCH_SCHEMA);

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Search API for logged out user", description = "Search API : to validate search API upon valid set of inputs for logged out user")
    @Parameters({"broker"})
    public void search_WITHOUTLOGIN_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for search API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .queryParam(QueryParameters.text.name(), QueryParameters.getSearch_string_value())
                .when().get(SmallcaseResource.search);

        logger.info("Creating request for search API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        int dataArrSize = JsonPathFinder.getJsPath(response).get("data.smallcases.size()");
        for (int i = 0; i < dataArrSize; i++) {
            assertThat(response.jsonPath().getMap("data.smallcases[" + i + "]")).containsKeys("name", "scid", "slug", "description");
        }
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Search API with invalid search string key",
            description = "Search API : To validate Search API with invalid search string key")
    @Parameters({"broker"})
    public void searchWithInvalidSearchStringKey_shouldReturn500(String broker) throws IOException {

        logger.info("Creating request for Search API - start for broker: " + broker);

        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam("wrong", QueryParameters.getSearch_string_value())
                .when()
                .get(SmallcaseResource.search);

        logger.info("Response received. Writing request and response to Extent Report for broker: " + broker);
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response for broker: " + broker);

        String actualData = response.jsonPath().getString("data");

        Assert.assertEquals(actualData, "Please mail us at tech@smallcase.com. Mistake is on our side!",
                "The 'data' field content does not match the expected value!");

        Assert.assertEquals(response.getStatusCode(), 500, "Expected status code 500, but found: " + response.getStatusCode());

        logger.info("Test passed for broker: " + broker);
    }



    @Test(testName = "To validate Search API with valid search string key and invalid search string value", description = "Search API : To validate Search API with valid search string key and invalid search string value")
    @Parameters({"broker"})
    public void searchWithInvalidSearchStringValue_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for search API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.text.name(), "this search string does not exist")
                .when().get(SmallcaseResource.search);

        logger.info("Creating request for search API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                        .verifyResponseDataIsEmpty("data.smallcases")
                        .verifyResponseDataIsEmpty("data.collections")
                        .verifyResponseDataIsEmpty("data.stocks")
                        .verifyResponseDataIsEmpty("data.publishers");

        logger.info("Test passed for " + broker);
    }
}
