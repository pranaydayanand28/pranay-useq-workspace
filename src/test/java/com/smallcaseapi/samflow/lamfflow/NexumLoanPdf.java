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

public class NexumLoanPdf extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(NexumLoanPdf.class.getName());

    @Test(testName = "Check Nexum User Loan detail ", description = "Verify User Loan detail  pdf generation")
    @Parameters({"PhoneNo", "Flow"})
    public void loanPdf(String PhoneNo, String Flow) {

        /*** get loan pdf api link generation to get user loan detail in pdf link  ***/
        logger.info("get loan pdf api Started  " + PhoneNo);
        Response getLoanPdf = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.lamfFlowRequestSpec())
                .when()
                .get(SmallcaseResource.nexumUserloanpdf)
                .then()
                .extract().response();

        logger.info("get loan pdf api Ended  " + PhoneNo);

        /*** json schema response validation ***/
        ResponseAssert.assertThat(getLoanPdf).returns_200_OK().hasValidSchema(IConst.GET_NEXULOANPDF_SCHEMA);
        writeRequestAndResponseInReport(writer.toString(), getLoanPdf.prettyPrint(), Flow);
    }

}
