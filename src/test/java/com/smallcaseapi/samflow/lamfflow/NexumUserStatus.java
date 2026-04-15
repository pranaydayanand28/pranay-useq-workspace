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

public class NexumUserStatus extends BaseTest {

    public static String token;
    private static final Logger logger = LoggerFactory.getLogger(NexumUserStatus.class.getName());

    @Test(testName = "Check  nexum user status   ", description = "Verify nexum user status  ")
    @Parameters({"PhoneNo", "Flow"})
    public void nexumUserStatus(String PhoneNo, String Flow) {
        logger.info("nexum user status API started  " + PhoneNo);

        /*** get nexum user status  API call for status of user  ***/

        Response nexumUserstatus = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.lamfFlowRequestSpec()).log().all()
                .when()
                .get(SmallcaseResource.nexumUserStatus)
                .then()
                .extract().response();
        logger.info("nexum user status API Call Ended " + PhoneNo);
        /*** json schema response validation ***/
        ResponseAssert.assertThat(nexumUserstatus).returns_200_OK().hasValidSchema(IConst.GET_NEXUMUSERSTATUS_SCHEMA);


        writeRequestAndResponseInReport(writer.toString(), nexumUserstatus.prettyPrint(), "");

    }
}
