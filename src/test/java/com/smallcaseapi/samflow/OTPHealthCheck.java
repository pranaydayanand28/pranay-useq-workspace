package com.smallcaseapi.samflow;

import com.smallcaseapi.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * This class performs health checks on the OTP service.
 */
public class OTPHealthCheck extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(OTPHealthCheck.class);

    /**
     * Test to validate the health of the OTP service.
     */
    @Test
    public void healthCheck() {
        String otpServiceUrl = "https://otp.prod.smallcase.com";
        RestAssured.baseURI = otpServiceUrl;

        try {
            logger.info("Initiating health check for OTP service at: {}", otpServiceUrl);

            // Perform the health check
            Response prodOtpCheckResponse = given()
                    .header("Content-Type", ContentType.JSON)
                    .log().all()
                    .when().get("/healthcheck/")
                    .then().extract().response();

            // Log the response for verification
            logger.info("OTP Health Check Response: {}", prodOtpCheckResponse.asString());
            System.out.println("prodOtpCheck Response: " + prodOtpCheckResponse.asString());

            if (prodOtpCheckResponse.getStatusCode() != 200) {
                logger.error("OTP health check failed with status code: {}", prodOtpCheckResponse.getStatusCode());
                throw new RuntimeException("OTP service is unhealthy. Response: " + prodOtpCheckResponse.asString());
            }

        } catch (Exception e) {
            logger.error("An error occurred during the OTP health check: {}", e.getMessage(), e);
            throw new RuntimeException("OTP health check failed", e);
        }
    }
}