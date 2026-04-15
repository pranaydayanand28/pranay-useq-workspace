package com.otpservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import commonutils.ConfigRead;

// TODO: should this class be part of the test package or source ?
public class Common {
    public static Map<String, Object> getValidMinimalClientConfig() {
        Map<String, Object> clientCreatePayload = new HashMap<>();
        clientCreatePayload.put("clientId", ConfigRead.getPropertyValue("otp_service_qa_client"));
        clientCreatePayload.put("verificationMethodDefault", "none");

        ArrayList<String> deliveryMethods = new ArrayList<>();
        deliveryMethods.add("sms");
        clientCreatePayload.put("deliveryMethods", deliveryMethods);

        ArrayList<String> verificationMethods = new ArrayList<>();
        verificationMethods.add("manual");
        clientCreatePayload.put("verificationMethods", verificationMethods);

        Map<String, Object> config = new HashMap<>();
        config.put("otpDigits", 4);
        config.put("otpSendLimit", 5);
        config.put("otpVerifyLimit", 5);
        config.put("otpTtl", 180);
        config.put("otpSendLimitTtl", 21600);
        config.put("otpRetryLimitTtl", 30);

        Map<String, Object> smsCommsConfig = new HashMap<>();
        smsCommsConfig.put("senderId", "QA-ATMTN");
        smsCommsConfig.put("ttl", "1");

        Map<String, String> template = new HashMap<>();
        template.put("templateId", "1207165969107430908");
        template.put("templateBody", "%OTP% is the OTP to verify your phone number on smallcase app");

        smsCommsConfig.put("template", template);
        config.put("smsCommsConfig", smsCommsConfig);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("enableInternalRouteForAllRequests", true);
        flags.put("enableTestAccountSetup", true);

        config.put("flags", flags);

        clientCreatePayload.put("config", config);

        return clientCreatePayload;
    }

    public static Map<String, Object> getClientConfigWithWhatsapp() {
        Map<String, Object> clientCreatePayload = new HashMap<>();
        clientCreatePayload.put("clientId", ConfigRead.getPropertyValue("otp_service_qa_client"));
        clientCreatePayload.put("verificationMethodDefault", "none");

        ArrayList<String> deliveryMethods = new ArrayList<>();
        deliveryMethods.add("whatsapp");
        clientCreatePayload.put("deliveryMethods", deliveryMethods);

        ArrayList<String> verificationMethods = new ArrayList<>();
        verificationMethods.add("manual");
        clientCreatePayload.put("verificationMethods", verificationMethods);

        Map<String, Object> config = new HashMap<>();
        config.put("otpDigits", 4);
        config.put("otpSendLimit", 5);
        config.put("otpVerifyLimit", 5);
        config.put("otpTtl", 180);
        config.put("otpSendLimitTtl", 21600);
        config.put("otpRetryLimitTtl", 30);

        Map<String, Object> whatsappCommsConfig = new HashMap<>();
        whatsappCommsConfig.put("ecosystem", "gupshup");
        whatsappCommsConfig.put("ttl", "1");
        whatsappCommsConfig.put("vendor", "gupshup");

        Map<String, Object> template = new HashMap<>();
        template.put("templateId", "test_whatsapp_template");
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", "otp_template");
        templateData.put("namespace", "test_namespace");
        template.put("templateData", templateData);
        template.put("isAuthTemplate", true);

        whatsappCommsConfig.put("template", template);
        config.put("whatsappCommsConfig", whatsappCommsConfig);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("enableInternalRouteForAllRequests", true);
        flags.put("enableTestAccountSetup", true);

        config.put("flags", flags);

        clientCreatePayload.put("config", config);

        return clientCreatePayload;
    }

    public static Map<String, Object> getClientConfigWithEmail() {
        Map<String, Object> clientCreatePayload = new HashMap<>();
        clientCreatePayload.put("clientId", ConfigRead.getPropertyValue("otp_service_qa_client"));
        clientCreatePayload.put("verificationMethodDefault", "none");

        ArrayList<String> deliveryMethods = new ArrayList<>();
        deliveryMethods.add("email");
        clientCreatePayload.put("deliveryMethods", deliveryMethods);

        ArrayList<String> verificationMethods = new ArrayList<>();
        verificationMethods.add("manual");
        clientCreatePayload.put("verificationMethods", verificationMethods);

        Map<String, Object> config = new HashMap<>();
        config.put("otpDigits", 4);
        config.put("otpSendLimit", 5);
        config.put("otpVerifyLimit", 5);
        config.put("otpTtl", 180);
        config.put("otpSendLimitTtl", 21600);
        config.put("otpRetryLimitTtl", 30);

        Map<String, Object> emailCommsConfig = new HashMap<>();
        emailCommsConfig.put("fromAddress", "noreply@smallcase.com");
        emailCommsConfig.put("ttl", "1");

        Map<String, String> template = new HashMap<>();
        template.put("subject", "Your OTP for smallcase verification");
        template.put("body", "Your OTP is %OTP%. Please use this to verify your email.");

        emailCommsConfig.put("template", template);
        config.put("emailCommsConfig", emailCommsConfig);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("enableInternalRouteForAllRequests", true);
        flags.put("enableTestAccountSetup", true);

        config.put("flags", flags);

        clientCreatePayload.put("config", config);

        return clientCreatePayload;
    }

    public static Map<String, Object> getClientConfigWithMultipleDeliveryMethods() {
        Map<String, Object> clientCreatePayload = new HashMap<>();
        clientCreatePayload.put("clientId", ConfigRead.getPropertyValue("otp_service_qa_client"));
        clientCreatePayload.put("verificationMethodDefault", "none");

        ArrayList<String> deliveryMethods = new ArrayList<>();
        deliveryMethods.add("sms");
        deliveryMethods.add("whatsapp");
        deliveryMethods.add("email");
        clientCreatePayload.put("deliveryMethods", deliveryMethods);

        ArrayList<String> verificationMethods = new ArrayList<>();
        verificationMethods.add("manual");
        clientCreatePayload.put("verificationMethods", verificationMethods);

        Map<String, Object> config = new HashMap<>();
        config.put("otpDigits", 4);
        config.put("otpSendLimit", 5);
        config.put("otpVerifyLimit", 5);
        config.put("otpTtl", 180);
        config.put("otpSendLimitTtl", 21600);
        config.put("otpRetryLimitTtl", 30);

        // SMS Config
        Map<String, Object> smsCommsConfig = new HashMap<>();
        smsCommsConfig.put("senderId", "QA-ATMTN");
        smsCommsConfig.put("ttl", "1");

        Map<String, String> smsTemplate = new HashMap<>();
        smsTemplate.put("templateId", "1207165969107430908");
        smsTemplate.put("templateBody", "%OTP% is the OTP to verify your phone number on smallcase app");

        smsCommsConfig.put("template", smsTemplate);
        config.put("smsCommsConfig", smsCommsConfig);

        // WhatsApp Config
        Map<String, Object> whatsappCommsConfig = new HashMap<>();
        whatsappCommsConfig.put("ecosystem", "gupshup");
        whatsappCommsConfig.put("ttl", "1");
        whatsappCommsConfig.put("vendor", "gupshup");

        Map<String, Object> whatsappTemplate = new HashMap<>();
        whatsappTemplate.put("templateId", "test_whatsapp_template");
        
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", "otp_template");
        templateData.put("namespace", "test_namespace");
        whatsappTemplate.put("templateData", templateData);
        whatsappTemplate.put("isAuthTemplate", true);

        whatsappCommsConfig.put("template", whatsappTemplate);
        config.put("whatsappCommsConfig", whatsappCommsConfig);

        // Email Config
        Map<String, Object> emailCommsConfig = new HashMap<>();
        emailCommsConfig.put("fromAddress", "noreply@smallcase.com");
        emailCommsConfig.put("ttl", "1");

        Map<String, String> emailTemplate = new HashMap<>();
        emailTemplate.put("subject", "Your OTP for smallcase verification");
        emailTemplate.put("body", "Your OTP is %OTP%. Please use this to verify your email.");

        emailCommsConfig.put("template", emailTemplate);
        config.put("emailCommsConfig", emailCommsConfig);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("enableInternalRouteForAllRequests", true);
        flags.put("enableTestAccountSetup", true);

        config.put("flags", flags);

        clientCreatePayload.put("config", config);

        return clientCreatePayload;
    }

    public static Map<String, Object> getClientConfigWithRateLimits() {
        Map<String, Object> clientCreatePayload = getValidMinimalClientConfig();
        Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");

        // Add country code rate limits
        Map<String, Integer> countryCodeRateLimit = new HashMap<>();
        countryCodeRateLimit.put("default", 10);
        countryCodeRateLimit.put("+91", 5);
        countryCodeRateLimit.put("+1", 15);
        config.put("countryCodeRateLimit", countryCodeRateLimit);
        config.put("countryCodeRateLimitDuration", 3600);

        // Add country name rate limits
        Map<String, Integer> countryNameRateLimit = new HashMap<>();
        countryNameRateLimit.put("default", 20);
        countryNameRateLimit.put("India", 10);
        countryNameRateLimit.put("USA", 25);
        config.put("countryNameRateLimit", countryNameRateLimit);
        config.put("countryNameRateLimitDuration", 3600);

        // Add email domain rate limits
        Map<String, Integer> emailDomainRateLimit = new HashMap<>();
        emailDomainRateLimit.put("default", 15);
        emailDomainRateLimit.put("gmail.com", 10);
        config.put("emailDomainRateLimit", emailDomainRateLimit);
        config.put("emailDomainRateLimitDuration", 3600);

        return clientCreatePayload;
    }

    public static Map<String, Object> getClientConfigWithWhitelists() {
        Map<String, Object> clientCreatePayload = getValidMinimalClientConfig();
        Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");

        ArrayList<String> phoneCountryCodeWhitelist = new ArrayList<>();
        phoneCountryCodeWhitelist.add("+91");
        phoneCountryCodeWhitelist.add("+1");
        phoneCountryCodeWhitelist.add("+44");
        config.put("phoneCountryCodeWhitelist", phoneCountryCodeWhitelist);

        return clientCreatePayload;
    }

    public static Map<String, Object> getClientConfigWithBlacklists() {
        Map<String, Object> clientCreatePayload = getValidMinimalClientConfig();
        Map<String, Object> config = (Map<String, Object>) clientCreatePayload.get("config");

        ArrayList<String> phoneCountryCodeBlacklist = new ArrayList<>();
        phoneCountryCodeBlacklist.add("+92");
        phoneCountryCodeBlacklist.add("+86");
        config.put("phoneCountryCodeBlacklist", phoneCountryCodeBlacklist);

        return clientCreatePayload;
    }

    public static Map<String, Object> getValidUpdatePayload() {
        // Similar to create but used for update operations
        return getValidMinimalClientConfig();
    }

    public static Map<String, Object> getMinimalUpdatePayload() {
        Map<String, Object> clientUpdatePayload = new HashMap<>();
        clientUpdatePayload.put("clientId", ConfigRead.getPropertyValue("otp_service_qa_client"));
        clientUpdatePayload.put("verificationMethodDefault", "none");

        ArrayList<String> deliveryMethods = new ArrayList<>();
        deliveryMethods.add("sms");
        clientUpdatePayload.put("deliveryMethods", deliveryMethods);

        ArrayList<String> verificationMethods = new ArrayList<>();
        verificationMethods.add("manual");
        clientUpdatePayload.put("verificationMethods", verificationMethods);

        Map<String, Object> config = new HashMap<>();
        config.put("otpDigits", 6);
        config.put("otpSendLimit", 3);
        config.put("otpVerifyLimit", 3);
        config.put("otpTtl", 300);
        config.put("otpSendLimitTtl", 3600);
        config.put("otpRetryLimitTtl", 60);

        Map<String, Object> smsCommsConfig = new HashMap<>();
        smsCommsConfig.put("senderId", "QA-TEST");
        smsCommsConfig.put("ttl", "2");

        Map<String, String> template = new HashMap<>();
        template.put("templateId", "1207165969107430908");
        template.put("templateBody", "Your verification OTP is %OTP%");

        smsCommsConfig.put("template", template);
        config.put("smsCommsConfig", smsCommsConfig);

        Map<String, Boolean> flags = new HashMap<>();
        flags.put("enableInternalRouteForAllRequests", true);
        flags.put("enableTestAccountSetup", false);

        config.put("flags", flags);

        clientUpdatePayload.put("config", config);

        return clientUpdatePayload;
    }
}