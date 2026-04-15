package com.smallcaseapi.samflow;

import com.otpservice.otpVerification.EmailVerification;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.EmailTestUser;
import commonutils.JsonPathFinder;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Handles the SSO login flow for Smallcase users.
 */
public class SSOLogin extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SSOLogin.class);
    private static String authToken;
    private static String verificationToken;
    private static String emailSecureAuth;

    private EmailVerification emailVerification = new EmailVerification();

    /**
     * Test to validate the SSO login flow.
     *
     * @param Flow The login flow type.
     */
    @Test(testName = "To validate SSO login", description = "SSO login flow")
    @Parameters({"Flow"})
    public void samLogin(String Flow) {
        try {
            String baseUrl = ConfigRead.getPropertyValue("otp_url");
            RestAssured.baseURI = baseUrl;

            String email = EmailTestUser.generateRandomEmail();
            logger.info("Starting SSO login process for email: {}", email);

            // Step 1: Generate authToken
            authToken = emailVerification.generateAuthToken(email);

            // Step 2: Trigger OTP
            Assert.assertTrue(emailVerification.triggerOtp(email, authToken),
                    "OTP triggering failed for phone: " + email);

            // Step 3: Verify OTP
            verificationToken = emailVerification.verifyOtp(email, authToken);

            // Step 4: Perform SAM login
            performSamLogin(email, Flow);

        } catch (Exception e) {
            logger.error("Error during SSO login flow for email: {}", e.getMessage(), e);
            throw new RuntimeException("SSO login failed", e);
        }
    }

    /**
     * Performs the final SAM login step using the verified token.
     *
     * @param email The user's email.
     * @param Flow  The login flow type.
     */
    private void performSamLogin(String email, String Flow) {
        try {
            logger.info("Performing SAM login for email: {}", email);
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");

            Map<String, String> ssoLoginPayload = new HashMap<>();
            ssoLoginPayload.put("verificationToken", verificationToken);
            ssoLoginPayload.put("provider", ConfigRead.getPropertyValue("provider"));

            Response ssoLoginResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("Content-Type", "application/json")
                    .log().all()
                    .body(ssoLoginPayload)
                    .when().post(SmallcaseResource.ssoSignin)
                    .then().extract().response();
            logger.debug("SAM Login Response: {}", ssoLoginResponse.asString());

            long responseTimeInMs = ssoLoginResponse.time();
            logger.info("Response time: {}ms", responseTimeInMs);

            emailSecureAuth = JsonPathFinder.getJsPath(ssoLoginResponse).get("data.secureToken").toString();
            if (emailSecureAuth == null || emailSecureAuth.isEmpty()) {
                throw new NullPointerException("SecureToken is null after SSO login");
            }

            DataToShare.setValue("emailSecureAuth", emailSecureAuth);
            logger.info("SecureToken obtained successfully.");

            logger.info("Validating post SAM login for email: {}", email);
            writeRequestAndResponseInReport(writer.toString(), ssoLoginResponse.prettyPrint(), Flow);
        } catch (NullPointerException e) {
            logger.error("NullPointerException during SAM login: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            logger.error("RuntimeException during SAM login: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during SAM login: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during SAM login", e);
        }
    }
}