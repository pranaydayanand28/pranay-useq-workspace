package com.smallcaseapi.samflow.investmentscore;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class Breakdown extends BaseTest
{

    private static final Logger logger = LoggerFactory.getLogger(Breakdown.class.getName());
    @Test(testName = "Breakdown of user investments", description = "To validate user investment breakdown")
    @Parameters({"PhoneNo", "Flow"})
    public  void getBreakdown(String PhoneNo, String Flow)
    {

        /*** THis API is used to generate user investment breakdown ***/

        logger.info("Generating Investment Breakdown" + PhoneNo);

        Response breakdown = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .log().all()
                .when()
                .get(SmallcaseResource.breakdown)
                .then()
                .extract().response();


        writeRequestAndResponseInReport(writer.toString(), breakdown.prettyPrint(),Flow);

        ResponseAssert.assertThat(breakdown)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .hasMandatoryObjectsPresent("stable")
                .hasMandatoryObjectsPresent("steady")
                .hasMandatoryObjectsPresent("growth");

        writeRequestAndResponseInReport(writer.toString(), breakdown.prettyPrint(),Flow);

    }

}
