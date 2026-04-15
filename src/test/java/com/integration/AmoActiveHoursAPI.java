package com.integration;

import com.asserts.ResponseAssert;
import com.smallcase.resource.SmallcaseResource;
import commonutils.BrokerConfigValidator;
import commonutils.TimeConverter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AmoActiveHoursAPI extends IntegrationBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AmoActiveHoursAPI.class);

    @DataProvider(name = "broker")
    public Object[][] broker() {
        String[] brokers = {
                "icici", "kite", "hdfc", "axis", "kotak", "sbi",
                "upstox", "groww", "dhan", "fisdom"
        };

        Object[][] data = new Object[brokers.length][1];
        for (int i = 0; i < brokers.length; i++) {
            data[i][0] = brokers[i];
        }
        return data;
    }

    @Test(dataProvider = "broker",
            testName = "To validate AMO open window timing",
            description = "AMO Window Timing : to validate AMO open window timing for the broker")
    public void testAmoActiveHours(String broker) {

        // Set broker for this test
        setBroker(broker);
        logWithCurrentBroker("Starting AMO Active Hours test");
        
        // Create payload in map format
        Map<String, Object> payload = new HashMap<>();
        payload.put("brokerName", broker);
        payload.put("options", new HashMap<>());
        
        Map<String, String> context = new HashMap<>();
        context.put("brokerFunctionLogId", "e63ef361-0a27-4f06-a5d6-94967dbe472c");
        payload.put("context", context);

        logWithCurrentBroker("Payload created: " + payload);

        // Make API call
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .header("Content-Type", "application/json")
                .header("x-domain-token", getXDomainToken())
                .body(payload)
                .log().all()
                .when()
                .post(SmallcaseResource.amoActiveHours);

        // Log response
        logWithCurrentBroker("Response received with status code: " + response.getStatusCode());
        logWithCurrentBroker("Response body: " + response.getBody().asString());

        // Assertions using ResponseAssert
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        // Additional assertions for response structure
        ResponseAssert.assertThat(response)
                .verifyEquality(response.jsonPath().getBoolean("error"), false)
                .verifyEquality(response.jsonPath().get("response.place.start") != null, true)
                .verifyEquality(response.jsonPath().get("response.place.end") != null, true)
                .verifyEquality(response.jsonPath().get("response.cancel.start") != null, true)
                .verifyEquality(response.jsonPath().get("response.cancel.end") != null, true);

        // Convert Unix timestamps to IST and log
        String convertedTimestamps = convertAndLogTimestamps(response, broker);

        // Step 9 & 10: Validate AMO window timings against broker configuration
        ValidationResult validationResult = validateAmoWindowTimings(response, broker);

        // Prepare report content with actual response, converted timestamps, and validation results
        String reportContent = "ACTUAL RESPONSE:\n" + response.prettyPrint() + 
                              "\n\nCONVERTED TIMESTAMPS (IST):\n" + convertedTimestamps +
                              "\n\nAMO WINDOW VALIDATION:\n" + validationResult.reportContent;

        // Write to report with broker information
        writeRequestAndResponseInReportWithBroker(writer.toString(), reportContent, "AMO Active Hours",broker);
        
        // Step 10: Fail the test case if validation fails
        if (!validationResult.isValid) {
            String failureMessage = "AMO Window Validation FAILED for " + broker + 
                                  "\nValidation Details: " + validationResult.errorMessage +
                                  "\nRequest: " + writer.toString() +
                                  "\nResponse: " + response.prettyPrint();
            
            logWithCurrentBroker("❌ TEST FAILED: " + failureMessage);
            throw new AssertionError(failureMessage);
        }
        
        logWithCurrentBroker("AMO Active Hours test completed successfully");
    }

    private String convertAndLogTimestamps(Response response, String broker) {
        StringBuilder convertedTimestamps = new StringBuilder();
        
        try {
            // Extract timestamps from response
            long placeStart = response.jsonPath().getLong("response.place.start");
            long placeEnd = response.jsonPath().getLong("response.place.end");
            long cancelStart = response.jsonPath().getLong("response.cancel.start");
            long cancelEnd = response.jsonPath().getLong("response.cancel.end");

            // Validate timestamps
            if (!TimeConverter.isValidTimestamp(placeStart) || !TimeConverter.isValidTimestamp(placeEnd) ||
                !TimeConverter.isValidTimestamp(cancelStart) || !TimeConverter.isValidTimestamp(cancelEnd)) {
                throw new IllegalArgumentException("Invalid timestamp values found in response");
            }

            // Build formatted string for report using TimeConverter utility
            convertedTimestamps.append("=== AMO Active Hours for ").append(broker).append(" ===\n");
            convertedTimestamps.append("PLACE ORDER TIMINGS:\n");
            convertedTimestamps.append("  ").append(TimeConverter.formatTimeForReport(placeStart, "Start")).append("\n");
            convertedTimestamps.append("  ").append(TimeConverter.formatTimeForReport(placeEnd, "End")).append("\n");
            convertedTimestamps.append("CANCEL ORDER TIMINGS:\n");
            convertedTimestamps.append("  ").append(TimeConverter.formatTimeForReport(cancelStart, "Start")).append("\n");
            convertedTimestamps.append("  ").append(TimeConverter.formatTimeForReport(cancelEnd, "End")).append("\n");

            // Log converted timestamps
            logWithBroker("=== AMO Active Hours ===", broker);
            logWithBroker("Place Order - " + TimeConverter.formatTimeForReport(placeStart, "Start"), broker);
            logWithBroker("Place Order - " + TimeConverter.formatTimeForReport(placeEnd, "End"), broker);
            logWithBroker("Cancel Order - " + TimeConverter.formatTimeForReport(cancelStart, "Start"), broker);
            logWithBroker("Cancel Order - " + TimeConverter.formatTimeForReport(cancelEnd, "End"), broker);
            logWithBroker("Original Unix Timestamps:", broker);
            logWithBroker("Place Start: " + placeStart + ", Place End: " + placeEnd, broker);
            logWithBroker("Cancel Start: " + cancelStart + ", Cancel End: " + cancelEnd, broker);

        } catch (Exception e) {
            logWithBroker("Error converting timestamps: " + e.getMessage(), broker);
            convertedTimestamps.append("Error converting timestamps: ").append(e.getMessage());
        }
        
        return convertedTimestamps.toString();
    }

    /**
     * Step 9 & 10: Validate AMO window timings against broker configuration
     * Validates working days (Monday-Friday) and handles weekends (Saturday-Sunday)
     * @param response API response containing timestamps
     * @param broker Broker name for validation
     * @return ValidationResult object containing validation status and report content
     */
    private ValidationResult validateAmoWindowTimings(Response response, String broker) {
        ValidationResult result = new ValidationResult();
        StringBuilder validationResults = new StringBuilder();
        
        try {
            // Extract timestamps from response
            long placeStart = response.jsonPath().getLong("response.place.start");
            long placeEnd = response.jsonPath().getLong("response.place.end");
            long cancelStart = response.jsonPath().getLong("response.cancel.start");
            long cancelEnd = response.jsonPath().getLong("response.cancel.end");

            // Initialize broker configuration validator
            BrokerConfigValidator validator = new BrokerConfigValidator();
            
            // Validate AMO window timings
            BrokerConfigValidator.ValidationResult validationResult = validator.validateAmoWindow(
                broker, placeStart, placeEnd, cancelStart, cancelEnd);

            // Set validation status
            result.isValid = validationResult.isValid;
            result.errorMessage = validationResult.errorMessage;

            // Log validation results
            logWithBroker("=== AMO Window Validation ===", broker);
            logWithBroker("Validation Status: " + (validationResult.isValid ? "PASSED" : "FAILED"), broker);
            logWithBroker("Details: " + validationResult.getDisplayMessage(), broker);

            // Build validation results for report
            validationResults.append("=== AMO Window Validation for ").append(broker).append(" ===\n");
            validationResults.append("Status: ").append(validationResult.isValid ? "✅ PASSED" : "❌ FAILED").append("\n");
            validationResults.append("Details: ").append(validationResult.getDisplayMessage()).append("\n");
            
            // Add current day information
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            boolean isWeekend = now.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || 
                              now.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
            validationResults.append("Current Day: ").append(now.getDayOfWeek()).append("\n");
            validationResults.append("Is Weekend: ").append(isWeekend ? "Yes" : "No").append("\n");
            
            // Add timestamp details for validation
            validationResults.append("\nTimestamp Details:\n");
            validationResults.append("Place Order: ").append(TimeConverter.formatTimeForReport(placeStart, "Start"))
                           .append(" to ").append(TimeConverter.formatTimeForReport(placeEnd, "End")).append("\n");
            validationResults.append("Cancel Order: ").append(TimeConverter.formatTimeForReport(cancelStart, "Start"))
                           .append(" to ").append(TimeConverter.formatTimeForReport(cancelEnd, "End")).append("\n");

            // Step 10: If validation fails, add failure details to report
            if (!validationResult.isValid) {
                validationResults.append("\n❌ VALIDATION FAILURE DETAILS:\n");
                validationResults.append("Expected AMO window timings from broker configuration do not match actual API response.\n");
                validationResults.append("This indicates a potential issue with the AMO window configuration.\n");
                validationResults.append("Please verify the broker configuration and API response alignment.\n");
                
                // Log failure for debugging
                logWithBroker("❌ AMO Window validation FAILED for " + broker, broker);
                logWithBroker("Failure reason: " + validationResult.errorMessage, broker);
            } else {
                logWithBroker("✅ AMO Window validation PASSED for " + broker, broker);
            }

        } catch (Exception e) {
            String errorMsg = "Error during AMO window validation: " + e.getMessage();
            logWithBroker(errorMsg, broker);
            validationResults.append("❌ VALIDATION ERROR: ").append(errorMsg).append("\n");
            
            // Set validation as failed for exceptions
            result.isValid = false;
            result.errorMessage = errorMsg;
        }
        
        result.reportContent = validationResults.toString();
        return result;
    }

    /**
     * Inner class to hold validation results
     */
    private static class ValidationResult {
        public boolean isValid = true;
        public String errorMessage = "";
        public String reportContent = "";
    }
}
