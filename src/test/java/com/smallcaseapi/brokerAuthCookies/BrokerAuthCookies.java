package com.smallcaseapi.brokerAuthCookies;

import com.asserts.ResponseAssert;
import com.google.gson.Gson;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.pojos.BrokerLoginRequest;
import com.smallcase.resource.pojos.brokerLoginResponse.BrokerLogin;
import com.smallcase.resource.pojos.getRequestTokenResponse.GetRequestToken;
import com.smallcaseapi.BaseTest;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.IConst;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.Hashtable;
import static io.restassured.RestAssured.given;

public class BrokerAuthCookies extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BrokerAuthCookies.class.getName());
    private static final String leprechaun = fetchLeprechaun(); // write this method directly in line 37 if fresh leprechaun is required for each broker everytime
    public BrokerAuthCookies() {
    }

    @Test(dataProvider = "data", priority = -2, testName = "Validate Broker Login", description = "BrokerLogin : Perform broker Auth login and set cookie with valid data"
    , groups = { "sanity" })
    public static void brokerLogin(Hashtable<String, String> arguments) {
        logger.info("Initiating test and loading request POJO object for " + arguments.get("broker"));
        Gson gson = new Gson();
        BrokerLoginRequest brokerLoginPayload = BrokerLoginRequest.builder()
                .setBroker(arguments.get("brokerLeprechaun"))
                .setBrokerParams(arguments.get("brokerParam"))
                .setReqToken(leprechaun)
                .setApp(arguments.get("app"))
                .setDeviceType("web")
                .build();

        logger.info("Reading Auth baseURL for " + arguments.get("broker"));
        RestAssured.baseURI = ConfigRead.getPropertyValue("auth_url");

        logger.info("Generating request for BrokerLogin API for " + arguments.get("broker"));
        Response response =
                given()
                        .filter(new RequestLoggingFilter(captor))
                        .header("Content-Type", ContentType.JSON)
                        .body(gson.toJson(brokerLoginPayload)).when()
                        .post(SmallcaseResource.brokerLogIn);

        logger.info("Collecting response as POJO class object for " + arguments.get("broker"));
        BrokerLogin responseBody = response.getBody().as(BrokerLogin.class);
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), arguments.get("broker")); //to write response to Extent-Report

        logger.info("Checking validations for " + arguments.get("broker"));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit().hasValidSchema(IConst.BROKER_LOGIN_SCHEMA);
        //Write the jwt received to the hashmap to be used as global variable
        DataToShare.setValue("jwtTRD" + arguments.get("broker"), responseBody.getData().getJwt_trd());
        logger.info("Test passed for " + arguments.get("broker"));
    }

    @Test(priority = -1, dataProvider = "data", testName = "Validate Get Request Token API", description = "GetRequestToken : Perform broker Auth login with valid data"
            , groups = { "sanity" })
    public static void getRequestToken(Hashtable<String, String> arguments) {

        String jwt = (String) DataToShare.getValue("jwtTRD" + arguments.get("broker")); //cast into whichever data type the jwt is required(obtained from brokerAuth and used here as cookie)

        logger.info("Reading Auth baseURL for " + arguments.get("broker"));
        RestAssured.baseURI = ConfigRead.getPropertyValue("auth_url");

        logger.info("Generating request for getRequest Token API for " + arguments.get("broker"));
        Response response =
                given()
                        .filter(new RequestLoggingFilter(captor))
                        .header("cookie", "jwt_trd=" + jwt)
                        .param("app", arguments.get("app"))
                        .param("broker", arguments.get("broker"))
                        .when()
                        .get(SmallcaseResource.getRequestToken);

        logger.info("Collecting response as POJO class object for " + arguments.get("broker"));
        GetRequestToken responseBody = response.getBody().as(GetRequestToken.class);
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), arguments.get("broker")); //to write response to Extent-Report

        logger.info("Checking validations for " + arguments.get("broker"));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit().hasValidSchema(IConst.REQUEST_TOKEN_SCHEMA);

        DataToShare.setValue("REQUEST_TOKEN" + arguments.get("broker"), responseBody.getData().getReqToken());
        logger.info("Test passed for " + arguments.get("broker"));

    }

    @Test(testName = "Perform broker login + request token for specific broker", description = "Fetches fresh reqToken internally for the given broker")
    @Parameters({"broker", "brokerLeprechaun", "brokerParam", "app"})
    public void loginAndFetchRequestToken(String broker, String brokerLeprechaun, String brokerParam, String app) {
        logger.info("Initiating broker login and token fetch for: " + broker);

        // Dynamically fetch a fresh reqToken for this run
        String reqToken = fetchLeprechaun();

        Gson gson = new Gson();

        // Build payload with dynamic values
        BrokerLoginRequest brokerLoginPayload = BrokerLoginRequest.builder()
                .setBroker(brokerLeprechaun)
                .setBrokerParams(brokerParam)
                .setReqToken(reqToken)
                .setApp(app)
                .setDeviceType("web")
                .build();

        RestAssured.baseURI = ConfigRead.getPropertyValue("auth_url");

        // Call Broker Login API
        Response loginResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .header("Content-Type", ContentType.JSON)
                .body(gson.toJson(brokerLoginPayload))
                .post(SmallcaseResource.brokerLogIn);

        logger.info("Login response: " + loginResponse.prettyPrint());
        BrokerLogin brokerLoginBody = loginResponse.getBody().as(BrokerLogin.class);
        writeRequestAndResponseInReport(writer.toString(), loginResponse.prettyPrint(), broker);
        ResponseAssert.assertThat(loginResponse).returns_200_OK();

        String jwt = brokerLoginBody.getData().getJwt_trd();
        DataToShare.setValue("jwtTRD" + broker, jwt);

        // Call Get Request Token API
        Response tokenResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .header("cookie", "jwt_trd=" + jwt)
                .param("app", app)
                .param("broker", broker)
                .get(SmallcaseResource.getRequestToken);

        logger.info("Token response: " + tokenResponse.prettyPrint());
        GetRequestToken tokenBody = tokenResponse.getBody().as(GetRequestToken.class);
        writeRequestAndResponseInReport(writer.toString(), tokenResponse.prettyPrint(), broker);
        ResponseAssert.assertThat(tokenResponse).returns_200_OK();

        DataToShare.setValue("REQUEST_TOKEN" + broker, tokenBody.getData().getReqToken());
        logger.info("Successfully logged in and fetched request token for broker: " + broker);
    }
}