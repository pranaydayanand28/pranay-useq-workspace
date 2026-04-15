package com.otpservice;

import com.CommonBaseTest;
import com.google.gson.Gson;
import com.smallcase.resource.SmallcaseResource;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.PhoneAndEmailGenerator;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.apache.commons.io.output.WriterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import resource.reports.LogStatus;

import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PhoneTestUser extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(PhoneTestUser.class);

    /**
     * Registers test users with dynamically generated phone numbers and OTPs using an internal API.
     *
     * @param flow The flow parameter passed from the test suite.
     */
    @Test(testName = "Create User Test", description = "Test to create users with dynamic phone number and OTP")
    @Parameters({"flow"})
    public void setupTestUser(String flow) {
        String baseUrl = ConfigRead.getPropertyValue("Internal_url");
        String clientSecret = baseUrl.equalsIgnoreCase("https://otp.stag.smallcase.com")
                ? System.getenv("stageSecret")
                : System.getenv("prodSecret");

        RestAssured.baseURI = baseUrl;

        int numberOfUsers = Integer.parseInt(System.getProperty("phoneUsers", "1"));

        for (int i = 0; i < numberOfUsers; i++) {
            String phoneNumber = new PhoneAndEmailGenerator().generatePhoneNumber();
            String phoneOtp = new PhoneAndEmailGenerator().generateOtp();

            Map<String, String> testUser = new HashMap<>();
            testUser.put("phone", phoneNumber);
            testUser.put("phoneCountryCode", ConfigRead.getPropertyValue("internalphoneCountryCode"));
            testUser.put("otp", phoneOtp);

            DataToShare.setValue("phoneotp", phoneOtp);

            try {
                logger.info("Registering test user with phone number: {}", phoneNumber);

                // Set up writer to capture request
                StringWriter requestWriter = new StringWriter();
                PrintStream requestCapture = new PrintStream(new WriterOutputStream(requestWriter, StandardCharsets.UTF_8), true);

                // Send request with logging filter
                Response response = given()
                        .filter(new RequestLoggingFilter(requestCapture))
                        .header("x-internal-admin-id", ConfigRead.getPropertyValue("internalClientId"))
                        .header("x-internal-admin-secret", clientSecret)
                        .header("Content-Type", "application/json")
                        .queryParam("clientId", ConfigRead.getPropertyValue("client_id"))
                        .header("x-service-test-automation", "true")
                        .body(new Gson().toJson(testUser))
                        .when()
                        .post(SmallcaseResource.phoneTestuser)
                        .then()
                        .extract()
                        .response();

                // Capture full request log
                String requestLog = requestWriter.toString();

                // Log to console and report
                if (response.statusCode() == 200) {
                    logger.info("Test user registered successfully. Response: {}", response.asString());
                } else {
                    logger.error("Failed to register test user. Status code: {}, Response: {}",
                            response.statusCode(), response.asString());
                }

                LogStatus.pass("Test User Data = " + testUser.toString());
                writeRequestAndResponseInReport(requestLog, response.prettyPrint(), flow);
                Reporter.log("Generated Phone User: Phone: " + phoneNumber + ", OTP: " + phoneOtp, true);
            } catch (Exception e) {
                logger.error("Error occurred while registering test user with phone number: {}", phoneNumber, e);
                Reporter.log("Error occurred while registering test user with phone number: " + phoneNumber + ". Error: " + e.getMessage(), true);
                System.err.println("Failed to set up test user: " + e.getMessage());
            }
        }
    }
}