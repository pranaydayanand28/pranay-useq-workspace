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

public class PendingAction extends BaseTest
{
        private static final Logger logger = LoggerFactory.getLogger(PendingAction.class.getName());

        @Test(testName = "Get Pending Action for user", description = "To validate pending action")
        @Parameters({"PhoneNo", "Flow"})
        public  void getPendingAction(String PhoneNo, String Flow)
        {
            /*** This API call is used to get list of user pending action ***/

            logger.info("List of Pending Actions" + PhoneNo);

            Response pendingAction = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .when()
                    .get(SmallcaseResource.scorePendingAction)
                    .then()
                    .extract().response();

            logger.info("Asserting pending action response");

            ResponseAssert.assertThat(pendingAction)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();

            writeRequestAndResponseInReport(writer.toString(), pendingAction.prettyPrint(), Flow);

        }


    }
