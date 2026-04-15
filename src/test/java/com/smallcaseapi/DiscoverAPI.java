package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.enums.PublisherType;
import com.smallcase.resource.enums.QueryParameters;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DiscoverAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DiscoverAPI.class.getName());
    static Response response;

    @Test(testName = "To validate schema for Discover API", description = "Discover API schema validation: to validate schema for discover API upon valid set of inputs")
    @Parameters({"broker"})
    public static void checkJSONSchema(String broker) throws IOException {

        logger.info("Checking schema for Discover - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.sortBy.name(), QueryParameters.getSort_by_value())
                .when().get(SmallcaseResource.discover);

        logger.info("Checking schema for Discover - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasValidSchema(IConst.DISCOVER_SCHEMA);

        logger.info("Test passed for " + broker);
    }

    @SneakyThrows
    @Test(testName = "This step fetches a scid which will be used to do transactions across current broker")
    @Parameters({"broker"})
    public static void fetchAndSetSCID(String broker) {

        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.Private.name().toLowerCase(), QueryParameters.isPrivate())
                .queryParam(QueryParameters.Public.name().toLowerCase(), QueryParameters.isPublic())
                .when().get(SmallcaseResource.discover);

        String scid = response.jsonPath().getMap("data[" + 0 + "]").get("scid").toString();
        String smallcaseName = response.jsonPath().get("data[" + 0 + "].info.name").toString();
        System.setProperty("smallcaseID", scid);
        System.setProperty("smallcaseName", smallcaseName);
        logger.info("---------------------"+response.prettyPrint());
    }

    @Test(testName = "To validate Discover API", description = "Discover API : to validate discover API upon valid set of inputs")
    @Parameters({"broker"})
    public void discover_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for discover API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.sortBy.name(), QueryParameters.getSort_by_value())
                .when().get(SmallcaseResource.discover);

        logger.info("Creating request for discover API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        int dataArrSize = JsonPathFinder.getJsPath(response).get("data.size()");
        for (int i = 0; i < dataArrSize; i++) {
            assertThat(response.jsonPath().getMap("data[" + i + "]")).containsKeys("info", "scid", "flags");
        }
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Discover API without login", description = "Discover API : to validate Discover API upon valid set of inputs when user has not logged in")
    @Parameters({"broker"})
    public void discover_WITHOUTLOGIN_shouldReturn200(String broker) {

        logger.info("Creating request for Discover API without login- start " + (broker));

        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .when()
                .get(SmallcaseResource.discover);

        logger.info("Creating request for Discover API without login- end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Discover API when search string is passed", description = "Discover API : to validate discover API upon passing search string")
    @Parameters({"broker"})
    public void discover_withSearchString_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for discover API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.sortBy.name(), QueryParameters.getSort_by_value())
                .queryParam(QueryParameters.performSearch.name(), QueryParameters.isPerformSearch())
                .queryParam(QueryParameters.searchString.name(), QueryParameters.getSearch_string_value())
                .when().get(SmallcaseResource.discover);

        logger.info("Creating request for discover API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        assertThat(response.jsonPath().getMap("data[" + 0 + "]")).extracting("scid").isEqualTo("SCAW_0001");

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Discover API when include blocked param is passed", description = "Discover API : to validate discover API upon passing include blocked param")
    @Parameters({"broker"})
    public void discover_showBlockedSmallcases_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for discover API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForSpecificPublisherNameAndType(broker, SmallcaseResource.publishername, PublisherType.research.name()))
                .queryParam(QueryParameters.sortBy.name(), QueryParameters.getSort_by_value())
                .queryParam(QueryParameters.includeBlocked.name(), QueryParameters.isBlockedIncluded())
                .when().get(SmallcaseResource.discover);

        logger.info("Creating request for discover API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        logger.info("Test passed for " + broker);
    }
}
