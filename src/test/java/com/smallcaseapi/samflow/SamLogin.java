package com.smallcaseapi.samflow;

import com.smallcaseapi.BaseTest;
import commonutils.PhoneNoTestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class SamLogin extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SamLogin.class);

    private SamLoginHelper samLoginHelper = new SamLoginHelper();

    @Test(testName = "To validate SAM login", description = "SAM login with valid input")
    @Parameters({"PhoneNo","isInternalParam","Flow"})
    public void samLogin(String phone, String isInternalParam, String flow) {
        boolean isInternal = Boolean.parseBoolean(isInternalParam);
        try {
            if (phone.startsWith("${") && phone.endsWith("}")) {
                String actualValue = PhoneNoTestUser.generatePhoneNumber();
                samLoginHelper.processLogin(actualValue, isInternal, flow);
            } else {
                samLoginHelper.processLogin(phone, isInternal, flow);
            }
        } catch (Exception e) {
            logger.error("Error during SAM login process for phone: {}, error: {}", phone, e.getMessage(), e);
            throw new RuntimeException("Login process failed for phone: " + phone, e);
        }
    }
}