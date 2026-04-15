package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import static io.restassured.RestAssured.given;

public class DashboardAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DashboardAPI.class.getName());
    static Response response;

    @Test(testName = "To validate schema for Dashboard API", description = "Dashboard API schema validation: to validate schema for dashboard API upon valid set of inputs")
    @Parameters({"broker"})
    public static void checkJSONSchema(String broker) throws IOException {

        logger.info("Checking schema for Dashboard - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.dashboard);

        logger.info("Checking schema for Dashboard - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.DASHBOARD_SCHEMA);

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Dashboard API", description = "Dashboard API : to validate dashboard API upon valid set of inputs")
    @Parameters({"broker"})
    public void dashboardAPI_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for Dashboard API - start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.dashboard);

        logger.info("Creating request for Dashboard API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Dashboard API without login", description = "Dashboard API : to validate dashboard API upon valid set of inputs when user has not logged in")
    @Parameters({"broker"})
    public void dashboardAPI_WITHOUTLOGIN_shouldReturn200(String broker) {

        logger.info("Creating request for Dashboard API without login- start " + (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .when()
                .get(SmallcaseResource.dashboard);

        logger.info("Creating request for Dashboard API without login- end " + (broker));
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
