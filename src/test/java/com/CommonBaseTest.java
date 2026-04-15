package com;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.smallcaseapi.BaseTest;
import commonutils.*;
import org.apache.commons.io.output.WriterOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import resource.reports.ExtentReport;
import resource.reports.LogStatus;

import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class CommonBaseTest {
    static protected StringWriter writer;
    static protected PrintStream captor;
    static String leprechaun;
    private static Logger logger = LoggerFactory.getLogger(BaseTest.class);

    /**initializing the extent report */
    @BeforeSuite(description = "Setup extent report", groups = { "sanity" })
    public void setup(){

        ExtentReport.initialize();
        DataToShare.flushMapData();
        logger.info("Starting test suite "+ CurrentDate.EPOCH_DATE());
    }

    /**Opening the extent report automatically after the test suite execution*/
    @AfterSuite(groups = {"sanity"})
    public void afterSuite(){
        ExtentReport.report.flush(); //Clear the extent report
        DataToShare.flushMapData();  //Flush Global variables from map
        logger.info("Ending test suite "+ CurrentDate.EPOCH_DATE());
    }

    /** This method helps to write the response to the extent report */
    @BeforeMethod(groups = {"sanity"})
    public void setUp() {
        writer = new StringWriter();
        captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);
    }

    /** Format the api string and log in Extent Report*/
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

    /**To check the content is a valid json or not**/
    private static boolean isJSON(String content) {
        try {
            new Gson().fromJson(content, Object.class);
            return true;
        } catch (JsonSyntaxException ex) {
            return false;
        }
    }

    /** This method  we call to generate request and response for each test in extent report */
    public static void writeRequestAndResponseInReport(String request, String response, String Feature) {
        LogStatus.info("---- Request ---", Feature);
        formatAPIAndLogInReport(request);
        LogStatus.info("---- Response ---");
        formatAPIAndLogInReport(response);
    }
    protected void resetCaptorAndWriter() {
        writer = new StringWriter();
        captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);
    }
}

