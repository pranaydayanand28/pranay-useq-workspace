package com.otpservice.otpVerification;

import com.otpservice.OtpServiceBaseTest;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import commonutils.PhoneNoTestUser;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PhoneVerification extends OtpServiceBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(PhoneVerification.class);
    public PhoneVerification() { initializeWriterAndCaptor(); }

    @Test(description = "OTP verification for Phone number")
    public void phoneOtpVerification() {
        logger.info("--------------- OTP verification for Phone number Test started ---------------");

        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); //Generate Phone Number
        String authToken = generateAuthToken(phoneNumber, true);
        Assert.assertTrue(triggerOtp(authToken), "Trigger OTP API returned false");
        String verificationToken = verifyOtp(authToken, true);
        Assert.assertTrue(verifcationStatus(verificationToken), "OTP Verification Test failed");

        logger.info("--------------- OTP verification for Phone number Test passed ---------------");
    }

    // Generate Auth Token
    public String generateAuthToken(String phone, boolean isInternal) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("otp_url");
            String phoneCountryCode = isInternal
                    ? ConfigRead.getPropertyValue("internalphoneCountryCode")
                    : ConfigRead.getPropertyValue("phoneCountryCode");

            Map<String, String> secureAuthPayload = new HashMap<>();
                secureAuthPayload.put("phone", isInternal && phone.startsWith("${") && phone.endsWith("}") ? PhoneNoTestUser.generatePhoneNumber() : phone);
                secureAuthPayload.put("phoneCountryCode", phoneCountryCode);

            logger.info("Generating authToken for phone: {}", phone);

            Response authTokenResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.secureAuthSamLogin())
                    .body(secureAuthPayload).log().all()
                    .post(SmallcaseResource.secureAuth)
                    .then().extract().response();

            logger.debug("Auth token response: {}", authTokenResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), "");

            if (authTokenResponse.statusCode() == 200) {
                return JsonPathFinder.getJsPath(authTokenResponse).get("data.authToken").toString();
            } else {
                logger.error("Auth token generation failed with status code: {} for phone: {}", authTokenResponse.statusCode(), phone);
                throw new RuntimeException("Auth token generation failed");
            }
        } catch (Exception e) {
            logger.error("Exception during Auth token generation for phone: {}: {}", phone, e.getMessage(), e);
            return null;
        }
    }

    // Trigger OTP
    public boolean triggerOtp(String authToken) {
        try {
            Map<String, String> triggerOtpPayload = new HashMap<>();
            triggerOtpPayload.put("recaptchaToken", "xyz");

            logger.info("Triggering OTP");

            Response triggerOtpResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.secureAuthSamLogin())
                    .header("x-Auth-jwt", authToken)
                    .body(triggerOtpPayload).log().all()
                    .post(SmallcaseResource.triggerOtp)
                    .then().extract().response();

            logger.debug("Trigger OTP response: {}", triggerOtpResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), triggerOtpResponse.prettyPrint(), "");

            if (triggerOtpResponse.statusCode() != 200) {
                logger.error("OTP triggering failed with status code: {}", triggerOtpResponse.statusCode());
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Exception during OTP triggering: {}", e.getMessage(), e);
            return false;
        }
    }

    // Trigger OTP SSO
    public boolean triggerOtpSSO(String authToken) {
        try {
            Map<String, String> triggerOtpPayload = new HashMap<>();
            String emailSecure = (String) DataToShare.getValue("emailSecureAuth");
            triggerOtpPayload.put("recaptchaToken", "xyz");
            triggerOtpPayload.put("secureToken", emailSecure != null ? emailSecure : "defaultSecureToken");

            logger.info("Triggering OTP for SSO");

            Response triggerOtpResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.secureAuthSamLogin())
                    .header("x-Auth-jwt", authToken)
                    .body(triggerOtpPayload).log().all()
                    .post(SmallcaseResource.triggerOtp)
                    .then().extract().response();

            logger.debug("Trigger OTP SSO response: {}", triggerOtpResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), triggerOtpResponse.prettyPrint(), "");

            if (triggerOtpResponse.statusCode() != 200) {
                logger.error("OTP triggering for SSO failed with status code: {}", triggerOtpResponse.statusCode());
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Exception during OTP triggering for SSO: {}", e.getMessage(), e);
            return false;
        }
    }

    // Verify OTP
    public String verifyOtp(String authToken, boolean isInternal) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("otp_url");
            Map<String, String> verifyOtpPayload = new HashMap<>();

            String otp = isInternal ? (String) DataToShare.getValue("phoneotp") : "9999";
            logger.info("Fetched OTP: {} for phone type: {}", otp, isInternal ? "Internal" : "External");
            if (isInternal && otp == null) {
                throw new RuntimeException("Failed to retrieve OTP for internal phone number");
            }

            verifyOtpPayload.put("otp", otp != null ? otp : "9999");
            logger.info("Verifying OTP");

            Response verifyOtpResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.secureAuthSamLogin())
                    .header("x-Auth-jwt", authToken)
                    .header("User-Agent", "qa_automation_bot_VqbBUNSeD")
                    .body(verifyOtpPayload).log().all()
                    .post(SmallcaseResource.verifyOtp)
                    .then().extract().response();

            System.out.println("Response verify OTP:" + verifyOtpResponse.asString());
            logger.debug("Verify OTP response: {}", verifyOtpResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), verifyOtpResponse.prettyPrint(), "");

            if (verifyOtpResponse.statusCode() == 200) {
                return JsonPathFinder.getJsPath(verifyOtpResponse).get("data.token").toString();
            } else {
                logger.error("OTP verification failed with status code: {}, response: {}", verifyOtpResponse.statusCode(), verifyOtpResponse.asString());
                throw new RuntimeException("OTP verification failed");
            }
        } catch (Exception e) {
            logger.error("Exception during OTP verification: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean verifcationStatus(String verificationToken) {
        try {
            logger.info("Checking OTP Verification status");

            String otpClientSecretStage = System.getenv("otpClientSecretStage");
            Response verifcationStatusResponse = given()
                    .config(RestAssured.config().logConfig(io.restassured.config
                            .LogConfig.logConfig().blacklistHeader("x-client-secret")))
                    .filter(new RequestLoggingFilter(captor)).spec(RequestSpec.secureAuthSamLogin())
                    .header("x-client-secret", otpClientSecretStage)
                    .queryParam("verificationToken", verificationToken).log().all()
                    .get(SmallcaseResource.verificationStatus).then().extract().response();

            System.out.println("OTP Verification status response:" + verifcationStatusResponse.asString());
            logger.debug("OTP Verification status response: {}", verifcationStatusResponse.asString());
            String requestLog = writer.toString().replace(otpClientSecretStage, "*****");
            writeRequestAndResponseInReport(requestLog, verifcationStatusResponse.prettyPrint(), "");

            if (verifcationStatusResponse.statusCode() != 200) {
                logger.error("OTP Verification status failed with status code: {}", verifcationStatusResponse.statusCode());
                return false;
            } else {
                return JsonPathFinder.getJsPath(verifcationStatusResponse).get("data.isVerified");
            }
        } catch (Exception e) {
            logger.error("Exception during OTP triggering for SSO: {}", e.getMessage(), e);
            return false;
        }
    }
}
