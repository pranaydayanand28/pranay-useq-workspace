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

import static io.restassured.RestAssured.given;

public class UpdateClient extends OtpServiceBaseTest {
        private static final Logger logger = LoggerFactory.getLogger(UpdateClient.class.getName());

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

        @Test(testName = "Update client with minimal config changes", description = "To validate updating client with minimal configuration changes")
        @Parameters({"Flow"})
        public void updateClientMinimal(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getMinimalUpdatePayload();

                logger.info("Calling OTP service to update client");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();


                // GET client to verify the updates
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
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

                // Verify the updates
                Map<String, Object> data = getClientResponse.jsonPath().getMap("data");
                Map<String, Object> updatedConfig = (Map<String, Object>) data.get("config");
                assert updatedConfig.get("otpDigits").equals(6) : "otpDigits should be updated to 6";
                assert updatedConfig.get("otpSendLimit").equals(3) : "otpSendLimit should be updated to 3";

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client OTP configuration", description = "To validate updating OTP digits, limits, and TTLs")
        @Parameters({"Flow"})
        public void updateClientOtpConfig(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                Map<String, Object> config = (Map<String, Object>) clientUpdatePayload.get("config");

                // Update OTP configuration
                config.put("otpDigits", 6);
                config.put("otpSendLimit", 3);
                config.put("otpVerifyLimit", 3);
                config.put("otpTtl", 300);

                logger.info("Calling OTP service to update client OTP configuration");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();


                // Get client to verify the updates
                Response getClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
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

                // Verify the updates
                Map<String, Object> data = getClientResponse.jsonPath().getMap("data");
                Map<String, Object> updatedConfig = (Map<String, Object>) data.get("config");
                assert updatedConfig.get("otpDigits").equals(6) : "otpDigits should be updated to 6";
                assert updatedConfig.get("otpSendLimit").equals(3) : "otpSendLimit should be updated to 3";
                assert updatedConfig.get("otpVerifyLimit").equals(3) : "otpVerifyLimit should be updated to 3";
                assert updatedConfig.get("otpTtl").equals(300) : "otpTtl should be updated to 300";

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client delivery methods - add WhatsApp", description = "To validate adding WhatsApp to delivery methods")
        @Parameters({"Flow"})
        public void updateClientAddWhatsapp(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getClientConfigWithMultipleDeliveryMethods();

                logger.info("Calling OTP service to update client delivery methods");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client SMS template", description = "To validate updating SMS template configuration")
        @Parameters({"Flow"})
        public void updateClientSmsTemplate(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                Map<String, Object> config = (Map<String, Object>) clientUpdatePayload.get("config");
                Map<String, Object> smsCommsConfig = (Map<String, Object>) config.get("smsCommsConfig");

                // Update SMS template
                Map<String, String> newTemplate = new HashMap<>();
                newTemplate.put("templateId", "new_template_id");
                newTemplate.put("templateBody", "Your new OTP is %OTP% for verification");
                smsCommsConfig.put("template", newTemplate);

                logger.info("Calling OTP service to update SMS template");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client rate limits", description = "To validate updating rate limit configuration")
        @Parameters({"Flow"})
        public void updateClientRateLimits(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getClientConfigWithRateLimits();

                logger.info("Calling OTP service to update client rate limits");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client feature flags", description = "To validate updating feature flags")
        @Parameters({"Flow"})
        public void updateClientFeatureFlags(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                Map<String, Object> config = (Map<String, Object>) clientUpdatePayload.get("config");

                // Update flags
                Map<String, Boolean> flags = (Map<String, Boolean>) config.get("flags");
                flags.put("enableTestAccountSetup", false);
                flags.put("blockNewIndianUsers", true);

                logger.info("Calling OTP service to update feature flags");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_200_OK()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client verification methods", description = "To validate updating verification methods")
        @Parameters({"Flow"})
        public void updateClientVerificationMethods(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                clientUpdatePayload.put("verificationMethodDefault", "manual");

                // when updating verification methods, we also need to update the captcha config for each device type and verification method combination
                // hence this updation should fail

                ArrayList<String> verificationMethods = new ArrayList<>();
                verificationMethods.add("manual");
                verificationMethods.add("invisible");
                clientUpdatePayload.put("verificationMethods", verificationMethods);

                logger.info("Calling OTP service to update verification methods");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("Captcha config: Missing combination of deviceType and verificationMethod");

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        // Negative Test Cases

        @Test(testName = "Update client missing required fields", description = "To validate error when required fields are missing")
        @Parameters({"Flow"})
        public void updateClientMissingRequiredFields(String Flow) {
                Map<String, Object> clientUpdatePayload = new HashMap<>();
                clientUpdatePayload.put("clientId",
                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"));
                // Missing verificationMethodDefault, deliveryMethods, config

                logger.info("Calling OTP service to update client with missing required fields");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client with invalid verification method", description = "To validate error on invalid verificationMethodDefault")
        @Parameters({"Flow"})
        public void updateClientInvalidVerificationMethod(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                clientUpdatePayload.put("verificationMethodDefault", "invalid_method");

                logger.info("Calling OTP service to update client with invalid verification method");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("Invalid default verification method");

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client with invalid delivery method", description = "To validate error on unsupported delivery method")
        @Parameters({"Flow"})
        public void updateClientInvalidDeliveryMethod(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                deliveryMethods.add("push_notification");
                clientUpdatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service to update client with invalid delivery method");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("Invalid delivery method specified");

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client SMS without template", description = "To validate error when SMS delivery is specified but template is missing")
        @Parameters({"Flow"})
        public void updateClientSmsMissingTemplate(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                Map<String, Object> config = (Map<String, Object>) clientUpdatePayload.get("config");
                Map<String, Object> smsConfig = (Map<String, Object>) config.get("smsCommsConfig");
                smsConfig.remove("template");

                logger.info("Calling OTP service to update client with SMS but missing template");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("'Template' is always required for SMS Config as a fallback template");

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client WhatsApp without config", description = "To validate error when WhatsApp delivery but missing config")
        @Parameters({"Flow"})
        public void updateClientWhatsappMissingConfig(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                deliveryMethods.add("whatsapp");
                clientUpdatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service to update client with WhatsApp but missing config");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client Email without config", description = "To validate error when Email delivery but missing config")
        @Parameters({"Flow"})
        public void updateClientEmailMissingConfig(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                ArrayList<String> deliveryMethods = new ArrayList<>();
                deliveryMethods.add("email");
                clientUpdatePayload.put("deliveryMethods", deliveryMethods);

                logger.info("Calling OTP service to update client with Email but missing config");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update non-existent client", description = "To validate error when updating a client that doesn't exist")
        @Parameters({"Flow"})
        public void updateNonExistentClient(String Flow) {
                // Delete the client first
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                commonutils.ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();

                logger.info("Calling OTP service to update non-existent client");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_404_NOTFOUND()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit();

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Update client rate limit missing default", description = "To validate error when countryCodeRateLimit missing default")
        @Parameters({"Flow"})
        public void updateClientRateLimitMissingDefault(String Flow) {
                Map<String, Object> clientUpdatePayload = Common.getValidMinimalClientConfig();
                Map<String, Object> config = (Map<String, Object>) clientUpdatePayload.get("config");

                // country code rate limit is only used when enforceCountryCodeRateLimitForAllRequests is true
                Map<String, Object> flags = (Map<String, Object>) config.get("flags");
                flags.put("enforceCountryCodeRateLimitForAllRequests", true);
                config.put("flags", flags);

                Map<String, Integer> countryCodeRateLimit = new HashMap<>();
                countryCodeRateLimit.put("+91", 5);
                // Missing "default" entry
                config.put("countryCodeRateLimit", countryCodeRateLimit);

                logger.info("Calling OTP service to update client with rate limit missing default");
                Response updateClientResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientUpdatePayload).log().all()
                                .when()
                                .patch(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                logger.info("Response:" + updateClientResponse);
                ResponseAssert.assertThat(updateClientResponse)
                                .returns_400_BADREQUEST()
                                .hasHeaderApplicationJSON()
                                .isWithinAcceptedTimeLimit()
                                .returnsValidErrorMessage("CountryCodeRateLimitMap must have default value specified");

                writeRequestAndResponseInReport(writer.toString(), updateClientResponse.prettyPrint(), Flow);
        }
}


