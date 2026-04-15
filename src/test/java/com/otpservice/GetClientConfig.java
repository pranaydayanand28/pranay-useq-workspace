package com.otpservice;

import com.asserts.ResponseAssert;
import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetClientConfig extends OtpServiceBaseTest {
        private static final Logger logger = LoggerFactory.getLogger(GetClientConfig.class.getName());

        @BeforeMethod
        public void beforeEachTest() {
                // Create a client before each test
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();

                given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload)
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .extract()
                                .response();
        }

        @AfterMethod
        public void afterEachTest() {
                // Delete the client after each test
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);
        }

        @Test(testName = "Get public client config", description = "To validate retrieving public client configuration")
        @Parameters({"Flow"})
        public void getPublicClientConfig(String Flow) {
                logger.info("Calling OTP service to get public client config");

                // remove x-client-internal-secret from headers, should work without that
                Response getConfigResponse = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.otpServiceAuth())
                .header("x-client-internal-secret", "")
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get("/client/config")
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getConfigResponse);
                ResponseAssert.assertThat(getConfigResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                // Verify response contains config structure
                Map<String, Object> data = getConfigResponse.jsonPath().getMap("data");
                assert data.containsKey("flags") : "flags should be present";
                // check for enableClientSecureTokenForAllRequests inside flags key
                Map<String, Object> flags = (Map<String, Object>) data.get("flags");
                assert flags.containsKey("enableClientSecureTokenForAllRequests") : "enableClientSecureTokenForAllRequests should be present";

                writeRequestAndResponseInReport(writer.toString(), getConfigResponse.prettyPrint(), Flow);
        }
        
        // Negative Test Cases

        @Test(testName = "Get config for non-existent client", description = "To validate error when client doesn't exist")
        @Parameters({"Flow"})
        public void getConfigNonExistentClient(String Flow) {
                // Delete the client first
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                logger.info("Calling OTP service to get config for non-existent client");
                Response getConfigResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceAuth())
                                .header("x-client-internal-secret", "")
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get("/client/config")
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getConfigResponse);
                ResponseAssert.assertThat(getConfigResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getConfigResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get config without clientId parameter", description = "To validate error when clientId is missing")
        @Parameters({"Flow"})
        public void getConfigWithoutClientId(String Flow) {
                logger.info("Calling OTP service to get config without clientId parameter");
                Response getConfigResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceAuth())
                                .header("x-client-internal-secret", "")
                                // No clientId query parameter
                                .log().all()
                                .when()
                                .get("/client/config")
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getConfigResponse);
                ResponseAssert.assertThat(getConfigResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getConfigResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get config with empty clientId", description = "To validate error when clientId is empty")
        @Parameters({"Flow"})
        public void getConfigEmptyClientId(String Flow) {
                logger.info("Calling OTP service to get config with empty clientId");
                Response getConfigResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceAuth())
                                .header("x-client-internal-secret", "")
                                .queryParam("clientId", "")
                                .log().all()
                                .when()
                                .get("/client/config")
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getConfigResponse);
                ResponseAssert.assertThat(getConfigResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getConfigResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get config with malformed clientId", description = "To validate error when clientId is malformed")
        @Parameters({"Flow"})
        public void getConfigMalformedClientId(String Flow) {
                logger.info("Calling OTP service to get config with malformed clientId");
                Response getConfigResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceAuth())
                                .header("x-client-internal-secret", "")
                                .queryParam("clientId", "!!invalid@@clientId##")
                                .log().all()
                                .when()
                                .get("/client/config")
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getConfigResponse);
                ResponseAssert.assertThat(getConfigResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getConfigResponse.prettyPrint(), Flow);
        }
}


