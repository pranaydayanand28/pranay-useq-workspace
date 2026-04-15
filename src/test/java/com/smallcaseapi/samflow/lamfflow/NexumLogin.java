package com.smallcaseapi.samflow.lamfflow;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class NexumLogin extends BaseTest {

    private static String lspCsrf;
    private static String lspJwt;

    public static String token;
    private static final Logger logger = LoggerFactory.getLogger(NexumLogin.class.getName());

    @Test(testName = "Check Nexum login with valid data ", description = "Verify nexum login flow ")
    @Parameters({"PhoneNo", "Flow"})
    public void nexumLogin(String PhoneNo, String Flow) {

        /*** get connect Token API call for connect token generation ***/
        logger.info("getConnect Token API call Started " + PhoneNo);
        Response getConnectToken = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .when()
                .get(SmallcaseResource.nexumConnectToken)
                .then()
                .extract().response();
        token = JsonPathFinder.getJsPath(getConnectToken).get("data.connectToken").toString();


        /*** nexum login  API call for  login ***/


        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("connectToken", token);

        logger.info("Nexum Login api call started  " + PhoneNo);
        RestAssured.baseURI = ConfigRead.getPropertyValue("nexum-api");
        Response nexumLogin = given()
                .filter(new RequestLoggingFilter(captor)).header("Content-Type", "application/json").body(loginPayload)

                .when()
                .post(SmallcaseResource.nexumLogin)
                .then()
                .extract().response();


        logger.info("Nexum api call  Ended " + PhoneNo);


        lspJwt = JsonPathFinder.getJsPath(nexumLogin).get("data.lspJwt").toString();
        lspCsrf = JsonPathFinder.getJsPath(nexumLogin).get("data.lspCsrf").toString();

        /*** json schema response validation ***/
        ResponseAssert.assertThat(nexumLogin).returns_200_OK().hasValidSchema(IConst.GET_NEXUMLOGIN_SCHEMA);

        writeRequestAndResponseInReport(writer.toString(), nexumLogin.prettyPrint(), Flow);

        DataToShare.setValue("lspCsrf", lspCsrf);
        DataToShare.setValue("lspJwt", lspJwt);

    }
}
