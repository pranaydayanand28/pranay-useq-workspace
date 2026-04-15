package com.otpService.resource;

import commonutils.ConfigRead;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {

    public static RequestSpecification otpServiceClient() {

        if (ConfigRead.getPropertyValue("otp_service_url").equals("https://otp-stag.smallcase.com")||ConfigRead.getPropertyValue("otp_service_url").contains("https://sc-platform-otp-service-preview"))
        {
            return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("otp_service_url"))
                    .addHeader("x-sc-source", "web")
                    // added to allow access to otp service from github runner
                    .addHeader("User-Agent", "qa_automation_bot_VqbBUNSeD")
                    .addHeader("x-client-internal-secret", System.getenv("otpServiceClientSecret"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-service-test-automation", "true")
                    .build();
        } else {
            return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("otp_service_url"))
                    .addHeader("x-sc-source", "web")
                    // added to allow access to otp service from github runner
                    .addHeader("User-Agent", "qa_automation_bot_VqbBUNSeD")
                    .addHeader("x-client-internal-secret", System.getenv("otpServiceProdClientSecret"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-service-test-automation", "true")
                    .build();
        }
    }

    public static RequestSpecification otpServiceAuth() {
        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("otp_service_url"))
                .addHeader("x-sc-source", "web")
                .addHeader("User-Agent", "qa_automation_bot_VqbBUNSeD")
                .addHeader("x-client-id", ConfigRead.getPropertyValue("otp_service_qa_client"))
                .addHeader("Content-Type", "application/json")
                .addHeader("x-service-test-automation", "true")
                .build();
    }

    public static RequestSpecification otpServiceTestUsers() {
        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("otp_service_url"))
                .addHeader("x-sc-source", "web")
                .addHeader("User-Agent", "qa_automation_bot_VqbBUNSeD")
                .addHeader("x-internal-admin-id", ConfigRead.getPropertyValue("otp_service_admin_client_id"))
                .addHeader("x-internal-admin-secret", System.getenv("otpServiceAdminClientSecret"))
                .addHeader("Content-Type", "application/json")
                .addHeader("x-service-test-automation", "true")
                .build();
    }

    public static RequestSpecification internalAuth() {

        String baseUrl = ConfigRead.getPropertyValue("Internal_url");
        String internalAdminId = ConfigRead.getPropertyValue("internalClientId");
        String internalAdminSecret = baseUrl.contains("stag") ? System.getenv("stageSecret") : System.getenv("prodSecret");

        return new RequestSpecBuilder().setBaseUri(baseUrl)
                .addHeader("x-internal-admin-id", internalAdminId)
                .addHeader("x-internal-admin-secret", internalAdminSecret)
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
