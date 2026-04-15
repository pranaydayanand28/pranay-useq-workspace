package com.otpservice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import commonutils.*;
import io.restassured.response.Response;

import org.apache.commons.io.output.WriterOutputStream;
import org.testng.annotations.*;

import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;

import resource.reports.ExtentReport;
import resource.reports.LogStatus;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

public class OtpServiceBaseTest {

    static protected StringWriter writer;
    static protected PrintStream captor;
    private static Logger logger = LoggerFactory.getLogger(OtpServiceBaseTest.class);

    /* initializing the extent report and perform logout */
    @BeforeSuite(description = "Runs at start of OTP service tests")

    public void setup() {

        ExtentReport.initialize();
        DataToShare.flushMapData();
        // Delete the client id from otp service
        Response deleteResponse = given()
                .spec(RequestSpec.otpServiceClient())
                .queryParam("clientId",
                        ConfigRead.getPropertyValue("otp_service_qa_client"))
                .when()
                .delete(ClientResource.client);

        logger.info(deleteResponse.asString());
        logger.info("Starting OTP service test suite " + CurrentDate.EPOCH_DATE());
    }

    /* Opening the extent report automatically after the test suite execution */
    @AfterSuite()
    public void afterSuite() {
        ExtentReport.report.flush(); // Clear the extent report
        DataToShare.flushMapData(); // Flush Global variables from map
        logger.info("Ending OTP service test suite " + CurrentDate.EPOCH_DATE());
    }

    /* This method helps to write the response to the extent report */
    @BeforeMethod()
    public void setUp() {
        writer = new StringWriter();
        captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);
    }

    protected void initializeWriterAndCaptor() {
        if (writer == null || captor == null) {
            writer = new StringWriter();
            captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);
        }
    }

    /* Format the api string and log in Extent Report */
    protected static void formatAPIAndLogInReport(String content) {
        String contentType = isJSON(content) ? "json" : "plaintext";
        String requestResponseHTMLTemplate = "<div class=\"requestResponseDiv\" data-type=\"" +
                contentType + "\">\n" +
                "<pre>\n" +
                content +
                "</pre>\n" +
                "</div>\n";
        String buttonContainerHTMLTemplate = "<div class='button-container'>" +
                "<button class='dropdown-button' onclick='toggleContent(this)'>" +
                "<span><pre>&#x1F53D;<div class='button-responsive'> Expand</div></pre></span>" +
                "</button>" +
                "<button class='copy-button' onclick='copyToClipboard(this)'>" +
                "<span><pre>&#x1F4CB;<div class='button-responsive'> Copy</div></pre></span>" +
                "</button>" +
                "<button class='maximize-button' onclick='maximizeScreen(this)'>" +
                "<span><pre><i class=\"fas fa-expand\"></i><div class='button-responsive'> Maximize</div></pre></span>" +
                "</button>" +
                "</div>";
        String formattedContent = "<div class='requestResponseDiv-container'>" +
                "<div class='aceCodeContainer' style=\"display: none;\">" +
                requestResponseHTMLTemplate +
                "</div>" +
                buttonContainerHTMLTemplate +
                "</div>";
        LogStatus.info(formattedContent);
    }

    /*To check the content is a valid json or not*/
    private static boolean isJSON(String content) {
        try {
            new Gson().fromJson(content, Object.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    /**
     * Method to be called in every test to log response to the report
     */
    public static void writeRequestAndResponseInReport(String request, String response , String Flow) {
        LogStatus.info("---- Request ---" , Flow);
        formatAPIAndLogInReport(request);
        LogStatus.info("---- Response ---");
        formatAPIAndLogInReport(response);
    }
}
