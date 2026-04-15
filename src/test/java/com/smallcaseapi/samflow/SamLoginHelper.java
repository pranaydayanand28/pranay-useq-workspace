package com.smallcaseapi.samflow;

import com.otpservice.otpVerification.PhoneVerification;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class SamLoginHelper extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SamLoginHelper.class);

    private static String authToken;
    private static String token;

    private PhoneVerification phoneVerification = new PhoneVerification();

    public void processLogin(String phone, boolean isInternal, String flow) {
        try{
            authToken = phoneVerification.generateAuthToken(phone, isInternal);
            if (authToken == null) {
                throw new RuntimeException("Auth token generation failed for phone: " + phone);
            }

            if (!phoneVerification.triggerOtp(authToken)) {
                throw new RuntimeException("OTP triggering failed for phone: " + phone);
            }

            token = phoneVerification.verifyOtp(authToken, isInternal);
            if (token == null) {
                throw new RuntimeException("OTP verification failed for phone: " + phone);
            }

            if (!samLogin(authToken, token, flow)) {
                throw new RuntimeException("SAM login failed for phone: " + phone);
            }
            logger.info("SAM login successful for phone: {}", phone);
        }catch (Exception e) {
            logger.error("Error during processLogin for phone: {}, error: {}", phone, e.getMessage(), e);
            throw e;
        }
    }

    // SAM Login
    public boolean samLogin(String authToken, String token, String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
            Map<String, String> samLoginPayload = new HashMap<>();
            samLoginPayload.put("verificationToken", token);

            logger.info("Logging in SAM user");

            Response samLoginResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .header("x-Auth-jwt", authToken)
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "qa_automation_bot_VqbBUNSeD")
                    .body(samLoginPayload).log().all()
                    .post(SmallcaseResource.samLogin)
                    .then().extract().response();

            logger.debug("SAM login response: {}", samLoginResponse.asString());

            if (samLoginResponse.statusCode() == 200) {
                String samCsrf = JsonPathFinder.getJsPath(samLoginResponse).get("data.x-csrf-token").toString();
                String samJwt = JsonPathFinder.getJsPath(samLoginResponse).get("data.sam").toString();
                DataToShare.setValue("samCsrf", samCsrf);
                DataToShare.setValue("samJwt", samJwt);
                writeRequestAndResponseInReport(writer.toString(), samLoginResponse.prettyPrint(), flow);
                return true;
            } else {
                logger.error("SAM login failed with status code: {}", samLoginResponse.statusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception during SAM login: {}", e.getMessage(), e);
            return false;
        }
    }
}