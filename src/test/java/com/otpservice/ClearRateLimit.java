package com.otpservice;

import com.CommonBaseTest;
import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;

import commonutils.ConfigRead;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.apache.commons.io.output.WriterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ClearRateLimit extends CommonBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ClearRateLimit.class);

    /** Clear otp rate limit for a given phone number
         * @param phoneCountryCode from the workflow dispatch
         * @param phoneNumber from the workflow dispatch
         * @param clientId from the workflow dispatch
    **/
    @Test(testName = "Clear OTP Rate Limit", description = "Clear OTP Rate Limit for a given phone number")
    public void clearRateLimit() {
        
        String phoneCountryCode = System.getProperty("phoneCountryCode");
        String phoneNumber = System.getProperty("phoneNumber");
        String clientId = System.getProperty("clientId");
        
        // Set default clientId based on environment if not provided or if default placeholder is used
        if (clientId == null || clientId.isEmpty() || clientId.equalsIgnoreCase("Default")) {
            clientId = ConfigRead.getPropertyValue("client_id");
        }

        // Validate required parameters
        if (phoneCountryCode == null || phoneCountryCode.isEmpty()) {
            Assert.fail("phoneCountryCode from workflow dispatch is required but was not provided");
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Assert.fail("phoneNumber from workflow dispatch is required but was not provided");
        }

        // Clear rate limit for the given phone number and client id
        try {
            logger.info("Clearing rate limit for phone: {} {} at client: {}", phoneCountryCode, phoneNumber, clientId);

            // Create Payload for clear rate limit
            Map<String, String> clearRateLimitPayload = new HashMap<>();
                clearRateLimitPayload.put("phoneCountryCode", phoneCountryCode);
                clearRateLimitPayload.put("phone", phoneNumber);
                clearRateLimitPayload.put("clientId", clientId);

            // Set up writer to capture request
            StringWriter requestWriter = new StringWriter();
            PrintStream requestCapture = new PrintStream(new WriterOutputStream(requestWriter, StandardCharsets.UTF_8), true);

            // Send request with logging filter
            Response response = given()
                    .filter(new RequestLoggingFilter(requestCapture))
                    .spec(RequestSpec.internalAuth())
                    .body(clearRateLimitPayload)
                    .when().post(ClientResource.clearRateLimit)
                    .then().extract().response();

            // Log to console and report
            if (response.statusCode() == 200) {
                logger.info("Cleared rate limit for phone: {} {} at client: {} successfully. Response: {}", phoneCountryCode, phoneNumber, clientId, response.asString());
            } else {
                logger.error("Failed to clear rate limit for phone: {} {} at client: {}. Status code: {}, Response: {}", phoneCountryCode, phoneNumber, clientId, response.statusCode(), response.asString());
                Assert.fail("Failed to clear rate limit for phone: " + phoneCountryCode + " " + phoneNumber + " at client: " + clientId + ". Status code: " + response.statusCode() + ". Response: " + response.asString());
            }
            writeRequestAndResponseInReport(requestWriter.toString(), response.prettyPrint(), "Cleared OTP Rate Limit for Phone: " + phoneCountryCode + " " + phoneNumber + " at client: " + clientId);
            Reporter.log("Cleared OTP Rate Limit for Phone: " + phoneCountryCode + " " + phoneNumber + " at client: " + clientId, true);
        } 
        catch (Exception e) {
            logger.error("Error occurred while clearing rate limit for phone: {} {} at client: {}", phoneCountryCode, phoneNumber, clientId, e);
            Assert.fail("Error occurred while clearing rate limit for phone: " + phoneCountryCode + " " + phoneNumber + " at client: " + clientId + ". Error: " + e.getMessage());
            Reporter.log("Error occurred while clearing rate limit for phone: " + phoneCountryCode + " " + phoneNumber + " at client: " + clientId + ". Error: " + e.getMessage(), true);
        }
    }
}