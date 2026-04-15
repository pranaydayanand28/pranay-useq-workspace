package com.smallcaseapi.samflow.lamfflow;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class NexumHolding extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(NexumHolding.class.getName());

    @Test(testName = "Check Nexum pledgedHoldings check with valid data ", description = "Verify pledgedHoldings for user")
    @Parameters({"PhoneNo", "Flow"})
    public void nexumHolding(String PhoneNo, String Flow) {

        /*** get userHolding API call to fetch pledgedHoldings  ***/
        logger.info("GetUser holding API started   " + PhoneNo);
        Response getUserholding = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.lamfFlowRequestSpec())
                .when()
                .get(SmallcaseResource.nexumUserHolding)
                .then()
                .extract().response();

        logger.info("GetUser holding API Ended  " + PhoneNo);
        /*** json schema response validation ***/
        ResponseAssert.assertThat(getUserholding).returns_200_OK().hasValidSchema(IConst.GET_NEXUMHOLDING_SCHEMA);
        writeRequestAndResponseInReport(writer.toString(), getUserholding.prettyPrint(), Flow);

    }
}