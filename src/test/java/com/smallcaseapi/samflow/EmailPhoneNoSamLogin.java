package com.smallcaseapi.samflow;

import com.otpservice.otpVerification.PhoneVerification;
import com.smallcaseapi.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class EmailPhoneNoSamLogin extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailPhoneNoSamLogin.class);

    private static String authToken;
    private static String token;

    private PhoneVerification phoneVerification = new PhoneVerification();
    private SamLoginHelper samLoginHelper = new SamLoginHelper();

    @Test(testName = "To validate SAM login", description = "Email with phone SAM login with valid input")
    @Parameters({"PhoneNo", "isInternalParam", "Flow"})
    public void emailSamLogin(String phone, String isInternalParam, String flow) {
        boolean isInternal = Boolean.parseBoolean(isInternalParam);
        try {
            logger.info("Processing login for phone number: {}", phone);
            processLogin(phone, isInternal, flow);
        } catch (Exception e) {
            logger.error("Error during SAM login process for phone: {}, error: {}", phone, e.getMessage(), e);
            throw new RuntimeException("Login process failed for phone: " + phone, e);
        }
    }

    private void processLogin(String phone, boolean isInternal, String flow) {
        try {
            authToken = phoneVerification.generateAuthToken(phone, isInternal);
            if (authToken == null) {
                throw new RuntimeException("Auth token generation failed for phone: " + phone);
            }

            if (!phoneVerification.triggerOtpSSO(authToken)) {
                throw new RuntimeException("OTP triggering failed for phone: " + phone);
            }

            token = phoneVerification.verifyOtp(authToken, isInternal);
            if (token == null) {
                throw new RuntimeException("OTP verification failed for phone: " + phone);
            }

            if (!samLoginHelper.samLogin(authToken, token, flow)) {
                throw new RuntimeException("SAM login failed for phone: " + phone);
            }

            logger.info("SAM login successful for phone: {}", phone);
        } catch (Exception e) {
            logger.error("Error during processLogin for phone: {}, error: {}", phone, e.getMessage(), e);
            throw e;
        }
    }
}