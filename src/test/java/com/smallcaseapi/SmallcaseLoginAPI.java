package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.google.gson.Gson;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import com.smallcase.resource.pojos.SmallcaseLoginRequest;
import com.smallcase.resource.pojos.smallcaseLoginResponse.SmallcaseLogin;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.IConst;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class SmallcaseLoginAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SmallcaseLoginAPI.class);
    private final Gson gson = new Gson();

    public SmallcaseLoginAPI() {}

    @Test(dataProvider = "data", priority = 100, testName = "Validate login with correct credentials",
            description = "Smallcase Login: Validate login with correct credentials", groups = { "sanity" })
    public void checkValidLoginWithValidReqTokenAndJWT(Map<String, String> arguments) {
        try {
            String broker = arguments.get("broker");
            String reqToken = (String) DataToShare.getValue("REQUEST_TOKEN" + broker);
            String cookies = (String) DataToShare.getValue("jwtTRD" + broker);

            SmallcaseLoginRequest smallcaseLoginPayload = new SmallcaseLoginRequest(reqToken);

            logger.info("Reading base URL for {}", broker);
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");

            logger.info("Generating request for Login API for {}", broker);
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("cookie", "jwt_trd=" + cookies)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(smallcaseLoginPayload))
                    .when()
                    .post(SmallcaseResource.smallcaseLogin);

            SmallcaseLogin responseBody = response.getBody().as(SmallcaseLogin.class);
            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

            logger.info("Validating response for {}", broker);
            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .hasMandatoryObjectsPresent("x-csrf-token")
                    .hasMandatoryObjectsPresent("jwt");

            DataToShare.setValue("CSRF" + broker, responseBody.getData().getCsrf());
            DataToShare.setValue("JWT" + broker, responseBody.getData().getJwt());
            logger.info("Test passed for {}", broker);
        } catch (Exception e) {
            logger.error("Error during login test with valid credentials: ", e);
            throw e;
        }
    }

    @Test(dataProvider = "data", priority = 104, testName = "Validate login with invalid credentials",
            description = "Smallcase Login: Validate login with invalid credentials")
    public void checkInvalidLoginWithInvalidReqTokenAndJWT(Map<String, String> arguments) {
        try {
            String broker = arguments.get("broker");

            logger.info("Reading base URL for {}", broker);
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");

            logger.info("Generating invalid payload for negative test for {}", broker);
            SmallcaseLoginRequest smallcaseLoginPayload = new SmallcaseLoginRequest("abc");

            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("cookie", "jwt_trd=")
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(smallcaseLoginPayload))
                    .when()
                    .post(SmallcaseResource.smallcaseLogin);

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

            logger.info("Validating response for {}", broker);
            ResponseAssert.assertThat(response)
                    .returns_401_UNAUTHORIZED()
                    .returnsValidErrorMessage(ApiConstants.invalidTokenMessage);
            logger.info("Test passed for {}", broker);
        } catch (Exception e) {
            logger.error("Error during login test with invalid credentials: ", e);
            throw e;
        }
    }

    @Test(dataProvider = "data", priority = 103, testName = "Validate login with no credentials",
            description = "Smallcase Login: Validate login with no credentials")
    public void checkInvalidLoginWithNoCredentials(Map<String, String> arguments) {
        try {
            String broker = arguments.get("broker");

            logger.info("Reading base URL for {}", broker);
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");

            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("cookie", "jwt_trd=" + DataToShare.getValue("jwtTRD" + broker))
                    .header("Content-Type", "application/json")
                    .body(" ")
                    .when()
                    .post(SmallcaseResource.smallcaseLogin);

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

            logger.info("Validating response for {}", broker);
            ResponseAssert.assertThat(response)
                    .returns_500_INTERNAL_SERVER_ERROR();
            logger.info("Test passed for {}", broker);
        } catch (Exception e) {
            logger.error("Error during login test with no credentials: ", e);
            throw e;
        }
    }

    @Test(dataProvider = "data", priority = 102, testName = "Validate response JSON schema",
            description = "Smallcase Login: Validate response JSON schema for login")
    public void checkJSONSchema(Map<String, String> arguments) {
        try {
            String broker = arguments.get("broker");
            String reqToken = (String) DataToShare.getValue("REQUEST_TOKEN" + broker);
            String cookies = (String) DataToShare.getValue("jwtTRD" + broker);

            SmallcaseLoginRequest smallcaseLoginPayload = new SmallcaseLoginRequest(reqToken);

            logger.info("Reading base URL for {}", broker);
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");

            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("cookie", "jwt_trd=" + cookies)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(smallcaseLoginPayload))
                    .when()
                    .post(SmallcaseResource.smallcaseLogin);

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

            logger.info("Validating JSON schema for {}", broker);
            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasValidSchema(IConst.smallcaseLoginResponseJSONSchema);
            logger.info("Test passed for {}", broker);
        } catch (Exception e) {
            logger.error("Error during JSON schema validation test: ", e);
            throw e;
        }
    }

    /**
     * Method to test login for a specific broker with valid JWT and request token.
     */
    @Test(testName = "Validate login with correct credentials for a specific broker", description = "Smallcase Login: Validate login with correct credentials for a specific broker")
    @Parameters({"broker"})
    public void loginWithValidJWTAndRequestToken(String broker) {
        try {
            // Fetching JWT and request token dynamically based on the broker.
            String reqToken = (String) DataToShare.getValue("REQUEST_TOKEN" + broker);
            String cookies = (String) DataToShare.getValue("jwtTRD" + broker);

            // Create the login request payload with the request token.
            SmallcaseLoginRequest smallcaseLoginPayload = new SmallcaseLoginRequest(reqToken);

            logger.info("Reading base URL for {}", broker);
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");

            logger.info("Generating request for Login API for {}", broker);
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("cookie", "jwt_trd=" + cookies)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(smallcaseLoginPayload))
                    .when()
                    .post(SmallcaseResource.smallcaseLogin);

            // Extract the response body and log it.
            SmallcaseLogin responseBody = response.getBody().as(SmallcaseLogin.class);
            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

            logger.info("Validating response for {}", broker);
            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .hasMandatoryObjectsPresent("x-csrf-token")
                    .hasMandatoryObjectsPresent("jwt");

            // Store CSRF and JWT tokens in DataToShare for future use.
            DataToShare.setValue("CSRF" + broker, responseBody.getData().getCsrf());
            DataToShare.setValue("JWT" + broker, responseBody.getData().getJwt());

            DataToShare.setValue("smallcaseCsrf", responseBody.getData().getCsrf());
            DataToShare.setValue("smallcaseJwt", responseBody.getData().getJwt());

            logger.info("Login test passed for {}", broker);
        } catch (Exception e) {
            logger.error("Error during login test with valid credentials for {}: ", broker, e);
            throw e;
        }
    }
}