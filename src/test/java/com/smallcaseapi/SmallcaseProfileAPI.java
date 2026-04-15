package com.smallcaseapi;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.ConfigRead;
import commonutils.ExcelRead;
import commonutils.JsonPathFinder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static commonutils.IConst.scid_stag;
import static commonutils.IConst.scid_prod;
import static io.restassured.RestAssured.given;

public class SmallcaseProfileAPI extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SmallcaseProfileAPI.class);
    private final Set<String> failedScids = new HashSet<>();

    /**
     * Core method to handle Smallcase Profile API validation for different views
     */
    private void validateSmallcaseProfile(String flow, String broker, boolean isSam, boolean isLoginView, boolean isBrokerLogin) {
        try {
            String baseUrl = ConfigRead.getPropertyValue("smallcaseapi_url");
            String excelPath = baseUrl.equals("https://api-stag.smallcase.com") ? scid_stag : scid_prod;
            String sheetName = baseUrl.equals("https://api-stag.smallcase.com") ? "scid_stag" : "scid_prod";

            logger.info("Environment: {}, Reading SCIDs from: {}",
                    baseUrl.equals("https://api-stag.smallcase.com") ? "STAGING" : "PRODUCTION", excelPath);

            List<String> scidList = ExcelRead.getCellData(excelPath, sheetName, 0);
            if (scidList == null || scidList.isEmpty())
                throw new RuntimeException("No SCIDs found in Excel sheet: " + sheetName);

            logger.info("Total SCIDs fetched: {}", scidList.size());

            RequestSpecification requestSpec = buildRequestSpecification(baseUrl, broker, isSam, isLoginView, isBrokerLogin);

            for (String scid : scidList) {
                try {
                    validateSingleScid(scid, requestSpec, isSam, flow);
                } catch (AssertionError ae) {
                    logger.error("SCID validation failed for SCID: {}. Error: {}", scid, ae.getMessage());
                    failedScids.add(scid);
                    writeRequestAndResponseInReport(writer.toString(), "SCID validation failed for: " + scid + "\nError: " + ae.getMessage(), flow);
                } catch (Exception e) {
                    logger.error("SCID validation encountered an exception for SCID: {}. Error: {}", scid, e.getMessage());
                    failedScids.add(scid);
                    writeRequestAndResponseInReport(writer.toString(), "SCID validation encountered an exception for: " + scid + "\nError: " + e.getMessage(), flow);
                }
            }

        } catch (Exception e) {
            logger.error("SCID API test setup failed. Exception: ", e);
            throw new RuntimeException("SCID API test setup failed due to an exception.", e);
        } finally {
            if (!failedScids.isEmpty()) {
                logger.warn("Total Failed SCIDs: {}", failedScids.size());
                logger.warn("List of Failed SCIDs: {}", String.join(", ", failedScids));

                throw new AssertionError("Test failed due to SCID validation errors. Check logs for details.");
            }
        }
    }


    /**
     * Builds Request Specification based on view type (Smallcase/Broker Login/Logout)
     */
    private RequestSpecification buildRequestSpecification(String baseUrl, String broker, boolean isSam, boolean isLoginView, boolean isBrokerLogin) {
        if (isBrokerLogin) {  // Broker LOGIN case
            return RequestSpec.requestSpecification(broker)
                    .filter(new RequestLoggingFilter(captor));
        }
        if (isLoginView) {  // Regular LOGIN (Smallcase)
            return RequestSpec.samFlowRequestSpec()
                    .filter(new RequestLoggingFilter(captor));
        } else {  // Smallcase/Broker Logout
            RequestSpecBuilder builder = new RequestSpecBuilder()
                    .setBaseUri(baseUrl)
                    .setContentType(ContentType.JSON)
                    .addFilter(new RequestLoggingFilter(captor))
                    .log(LogDetail.ALL);

            if (isSam) { // Smallcase Logout
                builder.addHeader("x-sc-publisher", "smallcase-website")
                        .addHeader("x-sc-publishertype", "distributor");
            }
            return builder.build();
        }
    }

    /**
     * Handles validation of individual SCID response
     */
    private void validateSingleScid(String expectedScid, RequestSpecification spec, boolean isSam, String flow) {
        String resource = isSam ? SmallcaseResource.samScid : SmallcaseResource.SmallcaseScid;

        logger.info("Fetching Smallcase Profile for SCID: {}", expectedScid);

        Response response = given()
                .spec(spec)
                .queryParam("scid", expectedScid)
                .when()
                .get(resource);

        if (response == null) {
            throw new RuntimeException("API response is null for SCID: " + expectedScid);
        }

        if (response.statusCode() == 500) {
            logger.error("Internal Server Error (500) for SCID: {}", expectedScid);
            failedScids.add(expectedScid);
            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint() + "\nFailed SCID: " + expectedScid, flow);
            return;
        }

        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        String actualScid = JsonPathFinder.getJsPath(response).getString("data.scid");
        if (!expectedScid.equals(actualScid)) {
            throw new AssertionError(String.format("SCID mismatch! Expected: %s, but got: %s", expectedScid, actualScid));
        }

        Object constituents = JsonPathFinder.getJsPath(response).get("data.constituents");
        validateConstituentsBasedOnFlow(constituents, flow);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), flow);

        logger.info("SCID validation successful: {}", actualScid);
    }

    /**
     * Validates the 'constituents' field based on the given flow (Login/Logout)
     */
    private void validateConstituentsBasedOnFlow(Object constituents, String flow) {
        if (flow.equalsIgnoreCase("logout")) { // Logout View
            if (constituents != null && !(constituents instanceof List<?> && ((List<?>) constituents).isEmpty())) {
                throw new AssertionError("Expected 'constituents' to be null or an empty list in Logout View, but got: " + constituents);
            }
        } else if (flow.equalsIgnoreCase("login")) { // Login View
            if (constituents == null || (constituents instanceof List<?> && ((List<?>) constituents).isEmpty())) {
                throw new AssertionError("Expected 'constituents' to be present in Login View, but got null or empty list");
            }
        } else {
            logger.warn("Unknown flow type provided: '{}'. No validation applied for constituents.", flow);
        }
    }

    // ---------- Test Methods ----------

    @Test(testName = "Validate Smallcase Profile API for SCIDs", description = "Validate Smallcase platform -Profile API check for Logout View")
    @Parameters({"Flow"})
    public void samSmallcaseLogoutView(String flow) {
        validateSmallcaseProfile(flow, null, true, false, false);
    }

    @Test(testName = "Validate Smallcase Profile API for SCIDs", description = "Validate Broker platform -Profile API check for Logout View")
    @Parameters({"Flow"})
    public void brokerSmallcaseLogoutView(String flow) {
        validateSmallcaseProfile(flow, null, false, false, false);
    }

    @Test(testName = "Validate Smallcase Profile API for SCIDs", description = "Validate Smallcase platform -Profile API check for Login View")
    @Parameters({"Flow"})
    public void samSmallcaseLoginView(String flow) {
        validateSmallcaseProfile(flow, null, true, true, false);
    }

    @Test(testName = "Validate Smallcase Profile API for SCIDs", description = "Validate Broker platform -Profile API check for Login View")
    @Parameters({"Flow","broker"})
    public void brokerSmallcaseLoginView(String flow, String broker) {
        System.setProperty("brokerNameForDP", broker);
        validateSmallcaseProfile(flow, broker, false, true, true);
    }
}