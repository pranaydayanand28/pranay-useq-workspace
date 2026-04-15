package com.smallcaseapi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.smallboard.GetLeprechaun;
import com.smallcase.resource.SmallcaseResource;
import commonutils.*;
import io.restassured.RestAssured;
import org.apache.commons.io.output.WriterOutputStream;
import org.testng.annotations.*;
import resource.reports.ExtentReport;
import resource.reports.LogStatus;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import static io.restassured.RestAssured.given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {

    static protected StringWriter writer;
    static protected PrintStream captor;
    static String leprechaun;
    private static Logger logger = LoggerFactory.getLogger(BaseTest.class);

    /*initializing the extent report and perform logout*/
    @BeforeSuite(description = "To evade Broker Switch error scenario, runs at start of login tests", groups = { "sanity" })
    public void setup(){
        RestAssured.baseURI = ConfigRead.getPropertyValue("auth_url");
        given().when().post(SmallcaseResource.logout);
        ExtentReport.initialize();
        DataToShare.flushMapData();
        logger.info("Starting test suite "+ CurrentDate.EPOCH_DATE());
    }

    public static String fetchLeprechaun(){
        leprechaun = GetLeprechaun.newLeprechaun();
        logger.info("==========================");
        logger.info("Test suite running on URL "+ ConfigRead.getPropertyValue("smallcaseapi_url"));
        logger.info("Leprechaun generated "+ leprechaun);
        logger.info("==========================");
        return leprechaun;
    }

    /*Opening the extent report automatically after the test suite execution*/
    @AfterSuite(groups = {"sanity"})
    public void afterSuite(){
        ExtentReport.report.flush(); //Clear the extent report
        DataToShare.flushMapData();  //Flush Global variables from map
        logger.info("Ending test suite "+ CurrentDate.EPOCH_DATE());
    }

    /*This method helps to write the response to the extent report*/
    @BeforeMethod(groups = {"sanity"})
    public void setUp() {
        writer = new StringWriter();
        captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);
    }

    /*Format the api string and log in Extent Report*/
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

/*    Method to be called in every test to log response to the report, brokerName variable is used to tag every testcase for a specific broker*/
    public static void writeRequestAndResponseInReport(String request, String response, String brokeName) {
        LogStatus.info("---- Request ---", brokeName);
        formatAPIAndLogInReport(request);
        LogStatus.info("---- Response ---");
        formatAPIAndLogInReport(response);
    }

    protected void resetCaptorAndWriter() {
        writer = new StringWriter();
        captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);
    }

    /*Generic data provider method to be used across all tests to run tests on all brokers in parallel*/
    @DataProvider(name = "data", parallel = false)
    public Object[][] brokerData(Method m) throws IOException {
        return ExcelRead.getPayloadAsHashTable(System.getProperty("user.dir") + IConst.excelSheetPath, IConst.brokerLoginPayload);
    }
}