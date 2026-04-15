package com.integration;

import com.smallcaseapi.BaseTest;
import commonutils.ConfigRead;
import io.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;

import java.io.PrintStream;
import java.io.StringWriter;

public class IntegrationBaseTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationBaseTest.class);
    protected String broker;
    protected String xDomainToken;

    /**
     * Setup method for integration tests
     * Sets up broker URL and initializes broker parameter
     */
    @BeforeMethod
    public void setupIntegration() {
        // Log environment information for debugging
        logEnvironmentInfo();
        
        // Set base URL for broker API
        String brokerUrl = ConfigRead.getPropertyValue("broker_url");
        RestAssured.baseURI = brokerUrl;
        logger.info("Integration Base URL set to: {}", brokerUrl);
        
        // Set x-domain-token from GitHub secrets based on environment
        xDomainToken = getXDomainTokenFromSecrets();
        logger.info("X-Domain-Token loaded successfully");
        
        // Initialize writer and captor for logging
        super.setUp();
        initializeCaptorAndWriter();
    }

    /**
     * Initialize captor and writer for request/response logging
     * This method ensures proper logging setup for integration tests
     */
    protected void initializeCaptorAndWriter() {
        resetCaptorAndWriter();
        logger.info("Captor and writer initialized for integration test");
    }

    /**
     * Get the current captor for request logging
     * @return PrintStream captor for logging requests
     */
    protected PrintStream getCaptor() {
        return BaseTest.captor;
    }

    /**
     * Get the current writer for response logging
     * @return StringWriter writer for capturing responses
     */
    protected StringWriter getWriter() {
        return BaseTest.writer;
    }

    /**
     * Enhanced reporting method that includes broker information
     * @param requestContent - Request content to log
     * @param responseContent - Response content to log
     * @param testDescription - Test description with broker info
     */
    protected void writeRequestAndResponseInReportWithBroker(String requestContent, String responseContent, String testDescription) {
        String enhancedDescription = testDescription;
        if (broker != null && !broker.isEmpty()) {
            enhancedDescription = testDescription + " [Broker: " + broker + "]";
        }
        
        logger.info("Writing to report: {}", enhancedDescription);
        writeRequestAndResponseInReport(requestContent, responseContent, enhancedDescription);
    }

    /**
     * Enhanced reporting method that includes explicit broker information
     * @param requestContent - Request content to log
     * @param responseContent - Response content to log
     * @param testDescription - Test description
     * @param brokerName - Explicit broker name to include in report
     */
    protected void writeRequestAndResponseInReportWithBroker(String requestContent, String responseContent, String testDescription, String brokerName) {
        String enhancedDescription = testDescription + " [Broker: " + brokerName + "]";
        
        logger.info("Writing to report: {}", enhancedDescription);
        writeRequestAndResponseInReport(requestContent, responseContent, enhancedDescription);
    }

    /**
     * Method to set broker parameter (can be called from test methods)
     * @param brokerName - Name of the broker
     */
    protected void setBroker(String brokerName) {
        this.broker = brokerName;
        logger.info("Broker set to: {}", brokerName);
    }

    /**
     * Get current broker name
     * @return current broker name
     */
    protected String getBroker() {
        return this.broker;
    }

    /**
     * Get x-domain-token from GitHub secrets based on environment
     * @return x-domain-token value
     */
    protected String getXDomainTokenFromSecrets() {
        String brokerUrl = ConfigRead.getPropertyValue("broker_url");
        String token;
        
        if (brokerUrl.equals("https://scb.stag.smallcase.com")) {
            /*STAGE_DOMAINTOKEN taking domain token from jenkins for staging environment*/
            token = System.getenv("STAGE_DOMAINTOKEN");
            logger.info("Fetching x-domain-token for STAGING environment");
        } else {
            /*PROD_DOMAINTOKEN taking domain token from jenkins for production environment*/
            token = System.getenv("PROD_DOMAINTOKEN");
            logger.info("Fetching x-domain-token for PRODUCTION environment");
        }
        
        if (token == null || token.trim().isEmpty()) {
            String errorMsg = "X-Domain-Token not found. Please ensure STAGE_DOMAINTOKEN or PROD_DOMAINTOKEN environment variables are set.";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        
        logger.info("Successfully loaded x-domain-token");
        return token;
    }

    /**
     * Get x-domain-token from properties
     * @return x-domain-token value
     */
    protected String getXDomainToken() {
        return this.xDomainToken;
    }

    /**
     * Enhanced logging method for integration tests
     * @param message - Log message
     * @param brokerName - Broker name for context
     */
    protected void logWithBroker(String message, String brokerName) {
        logger.info("[{}] {}", brokerName, message);
    }

    /**
     * Enhanced logging method using current broker
     * @param message - Log message
     */
    protected void logWithCurrentBroker(String message) {
        if (broker != null && !broker.isEmpty()) {
            logWithBroker(message, broker);
        } else {
            logger.info(message);
        }
    }

    /**
     * Log environment information for debugging purposes
     */
    private void logEnvironmentInfo() {
        logger.info("=== Environment Information ===");
        logger.info("Environment property (env): '{}'", System.getProperty("env", "NOT_SET"));
        logger.info("Environment property length: {}", System.getProperty("env", "").length());
        logger.info("STAGE_DOMAINTOKEN available: {}", System.getenv("STAGE_DOMAINTOKEN") != null ? "YES" : "NO");
        logger.info("PROD_DOMAINTOKEN available: {}", System.getenv("PROD_DOMAINTOKEN") != null ? "YES" : "NO");
        
        // Log all system properties that contain "env" for debugging
        System.getProperties().entrySet().stream()
                .filter(entry -> entry.getKey().toString().toLowerCase().contains("env"))
                .forEach(entry -> logger.info("System property {}: '{}'", entry.getKey(), entry.getValue()));
        
        // Log all environment variables that contain "DOMAIN" or "TOKEN" for debugging
        System.getenv().entrySet().stream()
                .filter(entry -> entry.getKey().toUpperCase().contains("DOMAIN") || 
                               entry.getKey().toUpperCase().contains("TOKEN"))
                .forEach(entry -> logger.info("Environment variable {}: {}", entry.getKey(), 
                        entry.getValue().length() > 10 ? entry.getValue().substring(0, 10) + "..." : entry.getValue()));
        
        logger.info("=== End Environment Information ===");
    }

}
