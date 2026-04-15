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

public class GetScore extends BaseTest
{

    private static final Logger logger = LoggerFactory.getLogger(GetScore.class.getName());

    @Test(testName = "Get Investment Score", description = "To Validate fetched User Score ")
    @Parameters({"PhoneNo", "Flow"})
    public  void getInvScore(String PhoneNo, String Flow)
    {
        /*** This API call is to generate user investment score ***/

        logger.info("Generating Investment Score for : " + PhoneNo);

        Response getScore = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .when()
                .get(SmallcaseResource.invScore)
                .then()
                .extract().response();


        logger.info("asserting Investment Score response for user : " + PhoneNo);

        ResponseAssert.assertThat(getScore)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .hasMandatoryObjectsPresent("score")
                .hasMandatoryObjectsPresent("cardContent")
                .hasMandatoryObjectsPresent("pendingAction");

        writeRequestAndResponseInReport(writer.toString(), getScore.prettyPrint(), Flow);

    }
}

