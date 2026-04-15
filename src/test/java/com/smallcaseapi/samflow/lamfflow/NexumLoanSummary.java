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

public class NexumLoanSummary extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(NexumLoanSummary.class.getName());

    @Parameters({"PhoneNo", "Flow"})
    @Test(testName = "Check Nexum User Loan summary detail ", description = "Verify User Loan summary detail")
    public void loanSummary(String PhoneNo, String Flow) {

        /*** get loan summary API to fetch user loan summary  ***/
        logger.info("get loan summary  api Started  " + PhoneNo);
        Response getLoanSummary = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.lamfFlowRequestSpec())
                .when()
                .get(SmallcaseResource.loanSummary)
                .then()
                .extract().response();

        logger.info("get loan summary api Ended  " + PhoneNo);
        /*** json schema response validation ***/
        ResponseAssert.assertThat(getLoanSummary).returns_200_OK().hasValidSchema(IConst.GET_NEXUMLOANSUMMARY_SCHEMA);
        writeRequestAndResponseInReport(writer.toString(), getLoanSummary.prettyPrint(), Flow);
    }

}
