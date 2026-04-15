package com.otpservice.otpVerification;

import com.otpservice.OtpServiceBaseTest;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.DataToShare;
import commonutils.EmailTestUser;
import commonutils.JsonPathFinder;
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

public class EmailVerification extends OtpServiceBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerification.class);
    public EmailVerification() { initializeWriterAndCaptor(); }

    @Test(description = "OTP verification for Email")
    public void emailOtpVerification() {
        logger.info("--------------- OTP verification for Email Test started ---------------");

        String email = EmailTestUser.generateRandomEmail();
        String authToken = generateAuthToken(email);
        Assert.assertTrue(triggerOtp(email, authToken), "Trigger OTP API returned false");
        String verificationToken = verifyOtp(email, authToken);
        Assert.assertTrue(verifcationStatus(verificationToken), "OTP Verification Test failed");

        logger.info("--------------- OTP verification for Email Test passed ---------------");
    }

    //Generate authToken for the SSO login
    public String generateAuthToken(String email) {
        try {
            Map<String, String> secureAuthPayload = new HashMap<>();
            secureAuthPayload.put("email", email);

            logger.info("Generating authToken request for SSO login: {}", email);
            Response authTokenResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.emailLogin())
                    .body(secureAuthPayload).log().all()
                    .when().post(SmallcaseResource.secureAuth)
                    .then().extract().response();

            logger.debug("Auth Token Response: {}", authTokenResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), "");

            if (authTokenResponse.getStatusCode() != 200) {
                throw new RuntimeException("Failed to obtain authToken. Response: " + authTokenResponse.asString());
            }
            String authToken = JsonPathFinder.getJsPath(authTokenResponse).get("data.authToken").toString();
            if (authToken == null || authToken.isEmpty()) {
                throw new NullPointerException("authToken is null or empty");
            }
            logger.info("Successfully collected authToken for email.");
            return authToken;
        } catch (RuntimeException e) {
            logger.error("Error during authToken validation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during authToken validation: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during authToken validation", e);
        }
    }

    //Trigger OTP for the SSO login
    public boolean triggerOtp(String email, String authToken) {
        try {
            Map<String, String> triggerOtpPayload = new HashMap<>();
            triggerOtpPayload.put("recaptchaToken", "xyz");

            logger.info("Triggering OTP request for SSO login: {}", email);
            Response triggerOtpResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.emailLogin())
                    .header("x-Auth-jwt", authToken)
                    .body(triggerOtpPayload).log().all()
                    .when().post(SmallcaseResource.triggerOtp)
                    .then().extract().response();
            
            logger.debug("OTP Trigger Response: {}", triggerOtpResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), triggerOtpResponse.prettyPrint(), "");

            if (triggerOtpResponse.getStatusCode() != 200) {
                logger.error("Failed to trigger OTP. Response: " + triggerOtpResponse.asString());
                return false;
            }
            return true;
        } catch (RuntimeException e) {
            logger.error("Error during OTP trigger: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during OTP trigger: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during OTP trigger", e);
        }
    }

    // Verify OTP for the SSO login
    public String verifyOtp(String email, String authToken) {
        try {
            String otp = (String) DataToShare.getValue("emailOtp");
            if (otp == null) {
                throw new NullPointerException("OTP is null for email: " + email);
            }

            Map<String, String> verifyOtpPayload = new HashMap<>();
            verifyOtpPayload.put("otp", otp);

            logger.info("Verifying OTP for SSO login: {}", email);
            Response verifyOtpResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.emailLogin())
                    .header("x-Auth-jwt", authToken)
                    .body(verifyOtpPayload).log().all()
                    .when().post(SmallcaseResource.verifyOtp)
                    .then().extract().response();
            
            logger.debug("OTP Verification Response: {}", verifyOtpResponse.asString());
            writeRequestAndResponseInReport(writer.toString(), verifyOtpResponse.prettyPrint(), "");

            if (verifyOtpResponse.getStatusCode() != 200) {
                throw new RuntimeException("Failed to verify OTP. Response: " + verifyOtpResponse.asString());
            }
            String verificationToken = JsonPathFinder.getJsPath(verifyOtpResponse).get("data.token").toString();
            if (verificationToken == null || verificationToken.isEmpty()) {
                throw new NullPointerException("Token is null after OTP verification");
            }
            return verificationToken;
        } catch (NullPointerException e) {
            logger.error("NullPointerException in verifyOtp: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            logger.error("RuntimeException in verifyOtp: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in verifyOtp: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during OTP verification", e);
        }
    }

    public boolean verifcationStatus(String verificationToken) {
        try {
            logger.info("Checking OTP Verification status");

            String otpEmailClientSecretStage = System.getenv("otpEmailClientSecretStage");
            Response verifcationStatusResponse = given()
                    .config(RestAssured.config().logConfig(io.restassured.config
                            .LogConfig.logConfig().blacklistHeader("x-client-secret")))
                    .filter(new RequestLoggingFilter(captor)).spec(RequestSpec.emailLogin())
                    .header("x-client-secret", otpEmailClientSecretStage)
                    .queryParam("verificationToken", verificationToken).log().all()
                    .get(SmallcaseResource.verificationStatus).then().extract().response();

            System.out.println("OTP Verification status response:" + verifcationStatusResponse.asString());
            logger.debug("OTP Verification status response: {}", verifcationStatusResponse.asString());
            String requestLog = writer.toString().replace(otpEmailClientSecretStage, "*****");
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
