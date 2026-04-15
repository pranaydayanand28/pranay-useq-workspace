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

public class GetClient extends OtpServiceBaseTest {
        private static final Logger logger = LoggerFactory.getLogger(GetClient.class.getName());

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

        @Test(testName = "Get existing client", description = "To validate retrieving an existing client configuration")
        @Parameters({"Flow"})
        public void getExistingClient(String Flow) {
                logger.info("Calling OTP service to get client details");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId", "clientSecret", "config");

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client with SMS delivery", description = "To validate client with SMS delivery method returns smsCommsConfig")
        @Parameters({"Flow"})
        public void getClientWithSmsDelivery(String Flow) {
                logger.info("Calling OTP service to get client with SMS delivery");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId", "config");

                // Verify SMS config is present
                Map<String, Object> data = getClientResponse.jsonPath().getMap("data");
                Map<String, Object> config = (Map<String, Object>) data.get("config");
                assert config.containsKey("smsCommsConfig") : "smsCommsConfig should be present";

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client with multiple delivery methods", description = "To validate client with multiple delivery methods")
        @Parameters({"Flow"})
        public void getClientWithMultipleDeliveryMethods(String Flow) {
                // Delete existing and create client with multiple delivery methods
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                Map<String, Object> clientCreatePayload = Common.getClientConfigWithMultipleDeliveryMethods();
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload)
                                .when()
                                .post(ClientResource.client);

                logger.info("Calling OTP service to get client with multiple delivery methods");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                // Verify all delivery configs are present
                Map<String, Object> data = getClientResponse.jsonPath().getMap("data");
                Map<String, Object> config = (Map<String, Object>) data.get("config");
                assert config.containsKey("smsCommsConfig") : "smsCommsConfig should be present";
                assert config.containsKey("whatsappCommsConfig") : "whatsappCommsConfig should be present";
                assert config.containsKey("emailCommsConfig") : "emailCommsConfig should be present";

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client with rate limits", description = "To validate client with rate limits configuration")
        @Parameters({"Flow"})
        public void getClientWithRateLimits(String Flow) {
                // Delete existing and create client with rate limits
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                Map<String, Object> clientCreatePayload = Common.getClientConfigWithRateLimits();
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload)
                                .when()
                                .post(ClientResource.client);

                logger.info("Calling OTP service to get client with rate limits");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                // Verify rate limit configs are present
                Map<String, Object> data = getClientResponse.jsonPath().getMap("data");
                Map<String, Object> config = (Map<String, Object>) data.get("config");
                assert config.containsKey("countryCodeRateLimit") : "countryCodeRateLimit should be present";
                assert config.containsKey("countryNameRateLimit") : "countryNameRateLimit should be present";
                assert config.containsKey("emailDomainRateLimit") : "emailDomainRateLimit should be present";

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client with feature flags", description = "To validate client feature flags are returned correctly")
        @Parameters({"Flow"})
        public void getClientWithFeatureFlags(String Flow) {
                logger.info("Calling OTP service to get client and verify feature flags");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                // Verify flags are present
                Map<String, Object> data = getClientResponse.jsonPath().getMap("data");
                Map<String, Object> config = (Map<String, Object>) data.get("config");
                assert config.containsKey("flags") : "flags should be present";

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        // Negative Test Cases

        @Test(testName = "Get non-existent client", description = "To validate error when trying to get a client that doesn't exist")
        @Parameters({"Flow"})
        public void getNonExistentClient(String Flow) {
                // Delete the client first
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                logger.info("Calling OTP service to get non-existent client");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client without clientId parameter", description = "To validate error when clientId parameter is missing")
        @Parameters({"Flow"})
        public void getClientWithoutClientId(String Flow) {
                logger.info("Calling OTP service to get client without clientId parameter");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                // No clientId query parameter
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client with empty clientId", description = "To validate error when clientId is empty")
        @Parameters({"Flow"})
        public void getClientEmptyClientId(String Flow) {
                logger.info("Calling OTP service to get client with empty clientId");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId", "")
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Get client with malformed clientId", description = "To validate error when clientId is malformed")
        @Parameters({"Flow"})
        public void getClientMalformedClientId(String Flow) {
                logger.info("Calling OTP service to get client with malformed clientId");
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId", "!!invalid@@clientId##")
                                .log().all()
                                .when()
                                .get(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + getClientResponse);
                ResponseAssert.assertThat(getClientResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), getClientResponse.prettyPrint(), Flow);
        }
}


