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

public class NexumUserCredit extends BaseTest {

    public static String token;

    private static final Logger logger = LoggerFactory.getLogger(NexumUserCredit.class.getName());

    @Test(testName = "Check nexum user credit score", description = "Verify nexum credit score flow ")
    @Parameters({"PhoneNo", "Flow"})

    public void nexumCredit(String PhoneNo, String Flow) {
        /*** get nexum credit check  API call for credit check ***/
        logger.info("credit similation Api started  " + PhoneNo);
        Response nexumCredit = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.lamfFlowRequestSpec()).log().all()
                .when()
                .get(SmallcaseResource.nexumUserCredit)
                .then()
                .extract().response();

        /*** json schema response validation ***/
        logger.info("credit similation API Ended  " + PhoneNo);

        ResponseAssert.assertThat(nexumCredit).returns_200_OK().hasValidSchema(IConst.GET_NEXUMUSERCREDIT_SCHEMA);
        System.out.println(nexumCredit.asString());

        writeRequestAndResponseInReport(writer.toString(), nexumCredit.prettyPrint(), Flow);
    }
}
