package com.otpservice;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.asserts.ResponseAssert;
import com.otpService.resource.AuthResource;
import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;

import commonutils.ConfigRead;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Auth extends OtpServiceBaseTest {
        private static final Logger logger = LoggerFactory.getLogger(Auth.class.getName());

        public String createClientAndGetClientSecret(Map<String, Object> clientCreatePayload) {
                // create client
                Response createdClient = given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();

                String clientSecret = createdClient.jsonPath().getMap("data").get("clientSecret").toString();

                return clientSecret;
        }

        public String createTestUserForClient(String phoneCountryCode, String phoneNo) {
                // create test user for the client

                String otp = "9999";

                Map<String, String> testUser = new HashMap<>();
                testUser.put("phoneCountryCode", phoneCountryCode);
                testUser.put("phone", phoneNo);
                testUser.put("otp", otp);

                Response createdTestUser = given()
                                .spec(RequestSpec.otpServiceTestUsers())
                                .body(testUser)
                                .queryParam("clientId", ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .post(ClientResource.testUser)
                                .then().extract().response();

                return createdTestUser.asString();
        }

        public String getAuthToken(Map<String, String> secureAuthPayload, String clientSecret) {
                Response authTokenResponse = given().filter(new RequestLoggingFilter(captor))
                                .header("x-client-secret", clientSecret)
                                .spec(RequestSpec.otpServiceAuth())
                                .body(secureAuthPayload).log().all()
                                .when()
                                .post(AuthResource.authToken)
                                .then()
                                .extract()
                                .response();

                String authToken = JsonPathFinder.getJsPath(authTokenResponse).get("data.authToken").toString();
                return authToken;
        }

        @BeforeMethod
        public void beforeEachTest() {
                // delete the client
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);
        }

        @Test(testName = "verify web SMS template", description = "verify whether the Web SMS template works as expected")
        @Parameters({"Flow"})
        public void testWebTemplate(String Flow) {
                // create client with web template
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();

                // create templates
                Map<String, Object> templates = new HashMap<>();

                Map<String, Object> webTemplate = new HashMap<>();
                webTemplate.put("templateId", "1207171266527191021");
                webTemplate.put("templateBody", "Your OTP is: %OTP% for smallcase.\n@stag.smallcase.com %OTP%");

                templates.put("web", webTemplate);

                Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");
                Map<String, Object> smsCommsConfig = (Map<String, Object>) config.get("smsCommsConfig");
                smsCommsConfig.put("templates", templates);

                String clientSecret = createClientAndGetClientSecret(clientCreatePayload);

                // TODO: read from config?
                String phoneCountryCode = "+SC";
                String phoneNo = "9999999999";
                String createdTestUser = createTestUserForClient(phoneCountryCode, phoneNo);
                logger.info("Created test user for qa client", createdTestUser);

                /*** SecureAuth API call for Auth token generation ***/
                Map<String, String> secureAuthPayload = new HashMap<>();
                secureAuthPayload.put("phone", phoneNo);
                secureAuthPayload.put("phoneCountryCode", phoneCountryCode);
                logger.info("Generating authTokenResponse request for otp service login flow" + phoneNo);
                String authToken = getAuthToken(secureAuthPayload, clientSecret);
                logger.info("collecting authToken for " + phoneNo);

                logger.info("Trigger Otp request for OTP service" + phoneNo);
                Response triggerOtpResponse = given().filter(new RequestLoggingFilter(captor))
                                .header("x-client-secret", clientSecret)
                                .spec(RequestSpec.otpServiceAuth()).header("x-Auth-jwt", authToken).log().all()
                                .header("x-sc-source", "web")
                                .body("{}")
                                .when()
                                .post(AuthResource.triggerOtp)
                                .then()
                                .extract()
                                .response();

                logger.info(triggerOtpResponse.asPrettyString());

                String templateString = webTemplate.get("templateBody").toString().replaceAll("%OTP%", "9999");
                ResponseAssert.assertThat(triggerOtpResponse).returns_200_OK().verifyResponseData("Text",
                                templateString);
        }

        @Test(testName = "verify Android SMS template", description = "verify whether the Android SMS template works as expected")
        @Parameters({"Flow"})
        public void testAndroidTemplate(String Flow) {
                // create client with web template
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();

                // create templates
                Map<String, Object> templates = new HashMap<>();

                Map<String, Object> androidTemplate = new HashMap<>();
                androidTemplate.put("templateId", "1207171266527191021");
                androidTemplate.put("templateBody", "Your OTP is: %OTP% for smallcase. %HASH%");
                androidTemplate.put("smsHashString", "p/oierut");

                templates.put("android", androidTemplate);

                Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");
                Map<String, Object> smsCommsConfig = (Map<String, Object>) config.get("smsCommsConfig");
                smsCommsConfig.put("templates", templates);

                String clientSecret = createClientAndGetClientSecret(clientCreatePayload);

                // TODO: read from config?
                String phoneCountryCode = "+SC";
                String phoneNo = "9999999999";
                String createdTestUser = createTestUserForClient(phoneCountryCode, phoneNo);
                logger.info("Created test user for qa client", createdTestUser);

                /*** SecureAuth API call for Auth token generation ***/
                Map<String, String> secureAuthPayload = new HashMap<>();
                secureAuthPayload.put("phone", phoneNo);
                secureAuthPayload.put("phoneCountryCode", phoneCountryCode);
                logger.info("Generating authTokenResponse request for otp service login flow" + phoneNo);
                String authToken = getAuthToken(secureAuthPayload, clientSecret);
                logger.info("collecting authToken for " + phoneNo);

                logger.info("Trigger Otp request for OTP service" + phoneNo);
                Response triggerOtpResponse = given().filter(new RequestLoggingFilter(captor))
                                .header("x-client-secret", clientSecret)
                                .header("x-sc-source", "android")
                                .spec(RequestSpec.otpServiceAuth()).header("x-Auth-jwt", authToken).log().all()
                                .body("{}")
                                .when()
                                .post(AuthResource.triggerOtp)
                                .then()
                                .extract()
                                .response();

                logger.info(triggerOtpResponse.asPrettyString());

                String templateString = androidTemplate.get("templateBody").toString().replaceAll("%OTP%", "9999")
                                .replaceAll("%HASH%", androidTemplate.get("smsHashString").toString());
                ResponseAssert.assertThat(triggerOtpResponse).returns_200_OK().verifyResponseData("Text",
                                templateString);
                writeRequestAndResponseInReport(writer.toString(), triggerOtpResponse.prettyPrint() , Flow);
        }

}
