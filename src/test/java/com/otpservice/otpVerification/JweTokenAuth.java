package com.otpservice.otpVerification;

import java.util.HashMap;
import java.util.Map;

import com.otpservice.OtpServiceBaseTest;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.ConfigRead;
import commonutils.EmailTestUser;
import commonutils.JweTokenUtil;
import commonutils.PhoneNoTestUser;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.asserts.ResponseAssert;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class JweTokenAuth extends OtpServiceBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(JweTokenAuth.class.getName());

    @Test(testName = "JWE token auth using valid JWE token for Phone", description = "Verify OTP verification using valid JWE token for Phone")
    @Parameters({"flow"})
    public void testValidJweTokenPhone(String flow) {
        logger.info("OTP verification test using valid JWE token for Phone started");

        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); // Generate Phone Number
        String phoneCountryCode = ConfigRead.getPropertyValue("phoneCountryCode");

        // Generate JWE token dynamically
        String jweToken = JweTokenUtil.generateJweToken(phoneNumber, phoneCountryCode);
        logger.info("Generated JWE token for phone: {}, country code: {}", phoneNumber, phoneCountryCode);

        // Perform authToken API request with JWE token generated
        Response authTokenResponse = performAuthTokenRequest(jweToken, "phone");

        // Verify success response with status code 200
        ResponseAssert.assertThat(authTokenResponse).returns_200_OK().hasHeaderApplicationJSON();
        // Verify response contains authToken and recaptchaType
        Map<String, Object> dataMap = authTokenResponse.jsonPath().getMap("data");
        Assert.assertNotNull(dataMap, "Response data should not be null");
        Assert.assertTrue(dataMap.containsKey("authToken"), "Response should contain authToken");
        Assert.assertTrue(dataMap.containsKey("recaptchaType"), "Response should contain recaptchaType");
        // Verify authToken is valid and not null
        String authToken = dataMap.get("authToken").toString();
        Assert.assertTrue((authToken != null && !authToken.isEmpty()), "authToken should not be null or empty");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);

        // Validate successful OTP verification
        PhoneVerification phoneVerification = new PhoneVerification();
        Assert.assertTrue(phoneVerification.triggerOtp(authToken), "Trigger OTP API returned false");
        String verificationToken = phoneVerification.verifyOtp(authToken, false);
        Assert.assertTrue(phoneVerification.verifcationStatus(verificationToken), "OTP Verification Test failed");

        logger.info("OTP verification test using valid JWE token for Phone passed");
    }

    @Test(testName = "JWE token auth using valid JWE token for test account", description = "Verify OTP verification using valid JWE token for test account(+SC)")
    @Parameters({"flow"})
    public void testValidJweTokenWithTestAccount(String flow) {
        logger.info("OTP verification test using valid JWE token for for test account(+SC) started");

        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); // Generate Phone Number
        String phoneCountryCode = ConfigRead.getPropertyValue("internalphoneCountryCode");

        // Generate JWE token dynamically for test account
        String jweToken = JweTokenUtil.generateJweToken(phoneNumber, phoneCountryCode);
        logger.info("Generated JWE token for test account: phone: {}, country code: {}", phoneNumber, phoneCountryCode);

        // Perform authToken API request with JWE token generated
        Response authTokenResponse = performAuthTokenRequest(jweToken, "phone");

        // Verify success response with status code 200
        ResponseAssert.assertThat(authTokenResponse).returns_200_OK().hasHeaderApplicationJSON();
        // Verify response contains authToken
        Map<String, Object> dataMap = authTokenResponse.jsonPath().getMap("data");
        Assert.assertNotNull(dataMap, "Response data should not be null");
        Assert.assertTrue(dataMap.containsKey("authToken"), "Response should contain authToken");
        // Verify authToken is valid and not null
        String authToken = dataMap.get("authToken").toString();
        Assert.assertTrue((authToken != null && !authToken.isEmpty()), "authToken should not be null or empty");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);

        // Validate successful OTP verification
        PhoneVerification phoneVerification = new PhoneVerification();
        Assert.assertTrue(phoneVerification.triggerOtp(authToken), "Trigger OTP API returned false");
        String verificationToken = phoneVerification.verifyOtp(authToken, true);
        Assert.assertTrue(phoneVerification.verifcationStatus(verificationToken), "OTP Verification Test failed");

        logger.info("OTP verification test using valid JWE token for test account(+SC) passed");
    }

    @Test(testName = "JWE token auth using valid JWE token for Email", description = "Verify OTP verification using valid JWE token for Email")
    @Parameters({"flow"})
    public void testValidJweTokenEmail(String flow) {
        logger.info("OTP verification test using valid JWE token for Email");

        String email = EmailTestUser.generateRandomEmail(); // Generate Email

        // Generate JWE token dynamically
        String jweToken = JweTokenUtil.generateJweToken(email);
        logger.info("Generated JWE token for Email: {}", email);

        // Perform authToken API request with JWE token generated
        Response authTokenResponse = performAuthTokenRequest(jweToken, "email");

        // Verify success response with status code 200 response
        ResponseAssert.assertThat(authTokenResponse).returns_200_OK().hasHeaderApplicationJSON();
        // Verify response contains authToken and recaptchaType
        Map<String, Object> dataMap = authTokenResponse.jsonPath().getMap("data");
        Assert.assertNotNull(dataMap, "Response data should not be null");
        Assert.assertTrue(dataMap.containsKey("authToken"), "Response should contain authToken");
        Assert.assertTrue(dataMap.containsKey("recaptchaType"), "Response should contain recaptchaType");
        // Verify authToken is valid and not null
        String authToken = dataMap.get("authToken").toString();
        Assert.assertTrue((authToken != null && !authToken.isEmpty()), "authToken should not be null or empty");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);

        // Validate successful OTP verification
        EmailVerification emailVerification = new EmailVerification();
        Assert.assertTrue(emailVerification.triggerOtp(email, authToken), "Trigger OTP API returned false");
        String verificationToken = emailVerification.verifyOtp(email, authToken);
        Assert.assertTrue(emailVerification.verifcationStatus(verificationToken), "OTP Verification Test failed");

        logger.info("OTP verification test using valid JWE token for Email passed");
    }

    @Test(testName = "Test invalid JWE token", description = "Verify that invalid/malformed JWE token returns 400 error")
    @Parameters({"flow"})
    public void testInvalidJweToken(String flow) {
        logger.info("Testing invalid JWE token authentication");

        // Perform authToken API request with invalid JWE token
        Response authTokenResponse = performAuthTokenRequest("invalid.jwe.token.string.gibberish", "phone");

        // Verify error response with status code 400
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for invalid JWE token");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected invalid JWE token, test passed");
    }

    @Test(testName = "Test empty JWE token", description = "Verify that empty JWE token field returns 400 validation error")
    @Parameters({"flow"})
    public void testEmptyJweToken(String flow) {
        logger.info("Testing empty JWE token");

        // Perform authToken API request with empty JWE token
        Response authTokenResponse = performAuthTokenRequest("", "phone");

        // Verify error response with status code 400
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for empty JWE token");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected empty JWE token, test passed");
    }

    @Test(testName = "Test missing JWE token(empty payload)", description = "Verify that missing JWE token field(and no phone/email) returns 400 validation error")
    @Parameters({"flow"})
    public void testMissingJweToken(String flow) {
        logger.info("Testing empty response without JWE token");

        Map<String, String> authTokenPayload = new HashMap<>(); // Empty payload - no jweToken, phone, or email

        // Perform authToken API request with empty payload
        Response authTokenResponse = performAuthTokenRequest(authTokenPayload, "phone");

        // Verify error response with status code 400
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for empty request body");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected empty request body, test passed");
    }

    @Test(testName = "Test with both Phone JWE token and Phone number(mutual exclusivity)", description = "Verify that passing both Phone JWE token and Phone number returns 400 validation error")
    @Parameters({"flow"})
    public void testPhoneJweTokenWithPhone(String flow) {
        logger.info("Testing JWE token auth with both Phone JWE token and Phone number(should fail due to mutual exclusivity)");

        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); // Generate Phone Number
        String phoneCountryCode = ConfigRead.getPropertyValue("phoneCountryCode");

        // Generate JWE token dynamically
        String jweToken = JweTokenUtil.generateJweToken(phoneNumber, phoneCountryCode);
        logger.info("Generated JWE token for phone: {}, country code: {}", phoneNumber, phoneCountryCode);

        Map<String, String> authTokenPayload = new HashMap<>();
        authTokenPayload.put("jweToken", jweToken);
        authTokenPayload.put("phone", phoneNumber);
        authTokenPayload.put("phoneCountryCode", phoneCountryCode);

        // Perform authToken API request with JWE token generated along with Phone number
        Response authTokenResponse = performAuthTokenRequest(authTokenPayload, "phone");

        // Verify error response with status code 400 (mutual exclusivity violation)
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for mutual exclusivity violation");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected due to both Phone JWE token and Phone number included(mutual exclusivity), test passed");
    }

    @Test(testName = "Test with both Email JWE token and Email(mutual exclusivity)", description = "Verify that passing both Email JWE token and Email returns 400 validation error")
    @Parameters({"flow"})
    public void testEmailJweTokenWithEmail(String flow) {
        logger.info("Testing JWE token auth with both Email JWE token and Email(should fail due to mutual exclusivity)");

        String email = EmailTestUser.generateRandomEmail(); // Generate Email

        // Generate JWE token dynamically
        String jweToken = JweTokenUtil.generateJweToken(email);
        logger.info("Generated JWE token for email: {}", email);

        Map<String, String> authTokenPayload = new HashMap<>();
        authTokenPayload.put("jweToken", jweToken);
        authTokenPayload.put("email", email);

        // Perform authToken API request with Email JWE token generated along with Email
        Response authTokenResponse = performAuthTokenRequest(authTokenPayload, "phone");

        // Verify error response with status code 400 (mutual exclusivity violation)
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for mutual exclusivity violation");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected due to both Email JWE token and Email included(mutual exclusivity), test passed");
    }

    @Test(testName = "Test with both Phone JWE token and Email(mutual exclusivity)", description = "Verify that passing both Phone JWE token and Email returns 400 validation error")
    @Parameters({"flow"})
    public void testPhoneJweTokenWithEmail(String flow) {
        logger.info("Testing JWE token auth with both Phone JWE token and Email(should fail due to mutual exclusivity)");

        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); //Generate Phone Number
        String phoneCountryCode = ConfigRead.getPropertyValue("phoneCountryCode");
        String email = EmailTestUser.generateRandomEmail(); // Generate Email

        // Generate JWE token dynamically
        String jweToken = JweTokenUtil.generateJweToken(phoneNumber, phoneCountryCode);
        logger.info("Generated JWE token for phone: {}, country code: {}", phoneNumber, phoneCountryCode);

        Map<String, String> authTokenPayload = new HashMap<>();
        authTokenPayload.put("jweToken", jweToken);
        authTokenPayload.put("email", email);

        //Perform authToken API request with Phone JWE token generated along with Email
        Response authTokenResponse = performAuthTokenRequest(authTokenPayload, "phone");

        // Verify error response with status code 400 response (mutual exclusivity violation)
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for mutual exclusivity violation");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected due to both Phone JWE token and Email included(mutual exclusivity), test passed");
    }

    @Test(testName = "Test with both Email JWE token and Phone number(mutual exclusivity)", description = "Verify that passing both Email JWE token and Phone number returns 400 validation error")
    @Parameters({"flow"})
    public void testEmailJweTokenWithPhone(String flow) {
        logger.info("Testing JWE token auth with both Email JWE token and Phone number(should fail due to mutual exclusivity)");

        String email = EmailTestUser.generateRandomEmail(); // Generate Email
        String phoneNumber = PhoneNoTestUser.generatePhoneNumber(); //Generate Phone Number
        String phoneCountryCode = ConfigRead.getPropertyValue("phoneCountryCode");

        // Generate JWE token dynamically
        String jweToken = JweTokenUtil.generateJweToken(email);
        logger.info("Generated JWE token for email: {}", email);

        Map<String, String> authTokenPayload = new HashMap<>();
        authTokenPayload.put("jweToken", jweToken);
        authTokenPayload.put("phone", phoneNumber);
        authTokenPayload.put("phoneCountryCode", phoneCountryCode);

        //Perform authToken API request with Email JWE token generated and Phone number
        Response authTokenResponse = performAuthTokenRequest(authTokenPayload, "email");

        // Verify error response with status code 400 (mutual exclusivity violation)
        ResponseAssert.assertThat(authTokenResponse).returns_400_BADREQUEST().hasHeaderApplicationJSON();
        // Verify response contains error message
        Assert.assertTrue(authTokenResponse.asString().contains("errors"), "Response should contain error message for mutual exclusivity violation");

        writeRequestAndResponseInReport(writer.toString(), authTokenResponse.prettyPrint(), flow);
        logger.info("Correctly rejected due to both Email JWE token and Phone number included(mutual exclusivity), test passed");
    }

    public Response performAuthTokenRequest(String jweToken, String method) {
        logger.info("Generating authToken for jweToken: {}", jweToken);

        RequestSpecification spec = (method=="phone")?
                RequestSpec.secureAuthSamLogin() : RequestSpec.emailLogin();

        Map<String, String> authTokenPayload = new HashMap<>();
                authTokenPayload.put("jweToken", jweToken);

        Response authTokenResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(spec).body(authTokenPayload).log().all()
                .when().post(SmallcaseResource.secureAuth)
                .then().log().all().extract().response();

        logger.info("Auth token response: " + authTokenResponse.asPrettyString());

        return authTokenResponse;
    }

    public Response performAuthTokenRequest(Map<String, String> authTokenPayload, String method) {
        logger.info("Generating authToken request");

        RequestSpecification spec = (method=="phone")?
                RequestSpec.secureAuthSamLogin() : RequestSpec.emailLogin();

        Response authTokenResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(spec).body(authTokenPayload).log().all()
                .when().post(SmallcaseResource.secureAuth)
                .then().log().all().extract().response();

        logger.info("Auth token response: " + authTokenResponse.asPrettyString());

        return authTokenResponse;
    }
}
