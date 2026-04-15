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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import commonutils.ConfigRead;

import static io.restassured.RestAssured.given;

public class CreateClient extends OtpServiceBaseTest {
        private static final Logger logger = LoggerFactory.getLogger(CreateClient.class.getName());

        @BeforeMethod
        public void beforeEachTest() {
                // Delete the client before each test to avoid 409 conflicts when tests run in parallel
                try {
                        given()
                                        .spec(RequestSpec.otpServiceClient())
                                        .queryParam("clientId",
                                                        ConfigRead.getPropertyValue("otp_service_qa_client"))
                                        .when()
                                        .delete(ClientResource.client);
                } catch (Exception e) {
                        // Ignore if client doesn't exist
                        logger.debug("Client deletion before test failed (might not exist): " + e.getMessage());
                }
        }

        @AfterMethod
        public void afterEachTest() {
                // Clean up after each test
                try {
                        given()
                                        .spec(RequestSpec.otpServiceClient())
                                        .queryParam("clientId",
                                                        ConfigRead.getPropertyValue("otp_service_qa_client"))
                                        .when()
                                        .delete(ClientResource.client);
                } catch (Exception e) {
                        // Ignore if client doesn't exist
                        logger.debug("Client deletion after test failed (might not exist): " + e.getMessage());
                }
        }

        @Test(testName = "OTP service client creation", description = "To validate client creation on OTP service", priority = 1)
        @Parameters({"Flow"})
        public void createClientMinimal(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();

                logger.info("Calling OTP service for client creation");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint() ,Flow);

        }

        @Test(testName = "OTP service client creation template validation", description = "To validate error message on missing template on client creation")
        @Parameters({"Flow"})
        public void createClientSmsTemplateValidation(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                // TODO: should have a better typesafe way to do this
                Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");
                Map<String, Object> smsConfig = (Map<String, Object>) config.get("smsCommsConfig");
                smsConfig.remove("template");

                logger.info("Calling OTP service for client creation");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage(
                                                "'Template' is always required for SMS Config as a fallback template");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint() , Flow);

        }

        @Test(testName = "Create client with WhatsApp delivery", description = "To validate client creation with WhatsApp delivery method")
        @Parameters({"Flow"})
        public void createClientWithWhatsapp(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getClientConfigWithWhatsapp();

                logger.info("Calling OTP service for client creation with WhatsApp");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with Email delivery", description = "To validate client creation with Email delivery method")
        @Parameters({"Flow"})
        public void createClientWithEmail(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getClientConfigWithEmail();

                logger.info("Calling OTP service for client creation with Email");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with multiple delivery methods", description = "To validate client creation with SMS, WhatsApp and Email")
        @Parameters({"Flow"})
        public void createClientWithMultipleDeliveryMethods(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getClientConfigWithMultipleDeliveryMethods();

                logger.info("Calling OTP service for client creation with multiple delivery methods");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with rate limits", description = "To validate client creation with country code and country name rate limits")
        @Parameters({"Flow"})
        public void createClientWithRateLimits(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getClientConfigWithRateLimits();

                logger.info("Calling OTP service for client creation with rate limits");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with phone country code whitelist", description = "To validate client creation with phone country code whitelist")
        @Parameters({"Flow"})
        public void createClientWithWhitelist(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getClientConfigWithWhitelists();

                logger.info("Calling OTP service for client creation with whitelist");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with phone country code blacklist", description = "To validate client creation with phone country code blacklist")
        @Parameters({"Flow"})
        public void createClientWithBlacklist(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getClientConfigWithBlacklists();

                logger.info("Calling OTP service for client creation with blacklist");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .hasMandatoryObjectsPresent("clientId");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        // Negative Test Cases

        @Test(testName = "Create client with invalid verification method", description = "To validate error on invalid verificationMethodDefault")
        @Parameters({"Flow"})
        public void createClientInvalidVerificationMethod(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                clientCreatePayload.put("verificationMethodDefault", "invalid_method");

                logger.info("Calling OTP service for client creation with invalid verification method");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("Invalid default verification method");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with invalid delivery method", description = "To validate error on unsupported delivery method")
        @Parameters({"Flow"})
        public void createClientInvalidDeliveryMethod(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                deliveryMethods.add("push");
                clientCreatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service for client creation with invalid delivery method");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("Invalid delivery method specified");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client WhatsApp without config", description = "To validate error when WhatsApp delivery is specified but config is missing")
        @Parameters({"Flow"})
        public void createClientWhatsappMissingConfig(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                deliveryMethods.add("whatsapp");
                clientCreatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service for client creation with WhatsApp but missing config");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client Email without config", description = "To validate error when Email delivery is specified but config is missing")
        @Parameters({"Flow"})
        public void createClientEmailMissingConfig(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                deliveryMethods.add("email");
                clientCreatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service for client creation with Email but missing config");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client missing required fields", description = "To validate error when required config fields are missing")
        @Parameters({"Flow"})
        public void createClientMissingRequiredFields(String Flow) {
                Map<String, Object> clientCreatePayload = new HashMap<>();
                clientCreatePayload.put("clientId", "test-client-missing-fields");
                // Missing verificationMethodDefault, deliveryMethods, config

                logger.info("Calling OTP service for client creation with missing required fields");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with rate limit missing default", description = "To validate error when countryCodeRateLimit is missing default entry")
        @Parameters({"Flow"})
        public void createClientRateLimitMissingDefault(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");

                Map<String, Integer> countryCodeRateLimit = new HashMap<>();
                countryCodeRateLimit.put("+91", 5);
                // Missing "default" entry
                config.put("countryCodeRateLimit", countryCodeRateLimit);

                // enable flag for enforce country code rate limit for all requests, while making sure the existing flags are not overridden
                Map<String, Object> flags = (Map<String, Object>) config.get("flags");
                flags.put("enforceCountryCodeRateLimitForAllRequests", true);
                config.put("flags", flags);

                logger.info("Calling OTP service for client creation with rate limit missing default");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("CountryCodeRateLimitMap must have default value specified");

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Create client with empty delivery methods", description = "To validate error when deliveryMethods array is empty")
        @Parameters({"Flow"})
        public void createClientEmptyDeliveryMethods(String Flow) {
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                clientCreatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service for client creation with empty delivery methods");
                Response clientCreateResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + clientCreateResponse);
                // empty delivery methods should be allowed, as by default SMS is enabled
                ResponseAssert.assertThat(clientCreateResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), clientCreateResponse.prettyPrint(), Flow);
        }
}
