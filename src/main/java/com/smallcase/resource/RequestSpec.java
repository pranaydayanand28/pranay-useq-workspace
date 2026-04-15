package com.smallcase.resource;

import commonutils.ConfigRead;
import commonutils.DataToShare;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

public class RequestSpec {

    public static RequestSpecification requestSpecification(String broker){

        String csrf = (String) DataToShare.getValue("CSRF" + broker);
        String jwt = (String) DataToShare.getValue("JWT" + broker);

        if (jwt == null || jwt.isEmpty()) {
            throw new IllegalArgumentException("JWT token is missing for broker: " + broker);
        }
        if (csrf == null || csrf.isEmpty()) {
            throw new IllegalArgumentException("CSRF token is missing for broker: " + broker);
        }
        if (broker == null || broker.isEmpty()) {
            throw new IllegalArgumentException("Broker cannot be null or empty");
        }

        return new RequestSpecBuilder()
                .setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("cookie", "jwt=" + jwt)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-sc-broker", broker)
                .addHeader("x-csrf-token", csrf)
                .build();
    }


    public static RequestSpecification requestSpecificationForSpecificPublisherNameAndType(String broker, String publisherName, String publisherType) {
        String csrf = (String) DataToShare.getValue("CSRF" + broker);
        String jwt = (String) DataToShare.getValue("JWT" + broker);

        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("cookie", "jwt=" + jwt)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-sc-broker", broker)
                .addHeader("x-sc-publisher", publisherName)
                .addHeader("x-sc-publishertype", publisherType)
                .addHeader("x-csrf-token", csrf).build();
    }

    public static RequestSpecification requestSpecification() throws IOException {
        String csrf = (String) DataToShare.getValue("CSRF");
        String jwt = (String) DataToShare.getValue("JWT");

        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("cookie", "jwt=" + jwt)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-csrf-token", csrf).build();
    }

    public static RequestSpecification requestSpecificationForUnauthorized() {

        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("cookie", "jwt=" + "")
                .addHeader("Content-Type", "application/json")
                .addHeader("x-csrf-token", "").build();
    }

    public static RequestSpecification requestSpecificationForNonLoggedInUser(String broker) {
        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("x-sc-broker", broker)
                .addHeader("Content-Type", "application/json").build();
    }

    //This spec is for cases when we don't want to send any broker details to the APIs, mainly open APIs which check market status and all...
    public static RequestSpecification requestSpecificationWithoutBrokerNameInHeaderAndLoggedOutState() {
        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("Content-Type", "application/json").build();
    }


    public static RequestSpecification secureAuthSamLogin() {
        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("otp_url"))
                .addHeader("x-sc-source", "web").addHeader("x-service-test-automation", "true")
                .addHeader("Content-Type", "application/json").addHeader("User-Agent","qa_automation_bot_VqbBUNSeD")
                .addHeader("x-client-id", ConfigRead.getPropertyValue("client_id"))
                .addHeader("x-amzn-waf-ip-originatingcountry-allowed", "true").build();
    }

    public static RequestSpecification emailLogin() {
        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("otp_url"))
                .addHeader("x-sc-source", "web").addHeader("x-service-test-automation", "true")
                .addHeader("Content-Type", "application/json")
                .addHeader("x-client-id",ConfigRead.getPropertyValue("Email_client_id")).addHeader("cloudfront-viewer-country-name","India")
                .addHeader("x-amzn-waf-ip-originatingcountry-allowed", "true").build();
    }

    /*** This method is used for Sam user flow ***/
    public static RequestSpecification samFlowRequestSpec() {
        String sam = (String) DataToShare.getValue("samJwt");
        String csrf = (String) DataToShare.getValue("samCsrf");

        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                .addHeader("x-sc-sam", sam)
                .addHeader("x-csrf-token", csrf)
                .addHeader("Content-Type", "application/json").build();
    }

    //Setup authorization headers for GET request of SAM or BAM or Gateway based on the parameter
    public static RequestSpecification getRequestSpec(String auth) {
        RequestSpecBuilder specs = new RequestSpecBuilder();

        if (auth.equalsIgnoreCase("sam")){
            String jwt = (String) DataToShare.getValue("samJwt");
            String csrf = (String) DataToShare.getValue("samCsrf");
            specs.addHeader("x-sc-sam", jwt).addHeader("x-csrf-token", csrf);
        }
        else if (auth.equalsIgnoreCase("smallcase")) {
            String jwt = (String) DataToShare.getValue("smallcaseJwt");
            String csrf = (String) DataToShare.getValue("smallcaseCsrf");
            specs.addHeader("x-sc-jwt", jwt).addHeader("x-csrf-token", csrf);
        }
        else if (auth.equalsIgnoreCase("gateway")) {
            String jwt = (String) DataToShare.getValue("gatewayJwt");
            String csrf = (String) DataToShare.getValue("gatewayCsrf");
            specs.addHeader("x-sc-gateway", jwt).addHeader("x-sc-csrf", csrf);
        }
        return specs.build();
    }

    //Setup authorization and ContentType headers for POST request of SAM or BAM or Gateway based on the parameter
    public static RequestSpecification postRequestSpec(String auth) {
        RequestSpecBuilder specs = new RequestSpecBuilder();

        if (auth.equalsIgnoreCase("sam")){
            String jwt = (String) DataToShare.getValue("samJwt");
            String csrf = (String) DataToShare.getValue("samCsrf");
            specs.addHeader("x-sc-sam", jwt).addHeader("x-csrf-token", csrf);
        }
        else if (auth.equalsIgnoreCase("smallcase")) {
            String jwt = (String) DataToShare.getValue("smallcaseJwt");
            String csrf = (String) DataToShare.getValue("smallcaseCsrf");
            specs.addHeader("x-sc-jwt", jwt).addHeader("x-csrf-token", csrf);
        }
        else if (auth.equalsIgnoreCase("gateway")) {
            String jwt = (String) DataToShare.getValue("gatewayJwt");
            String csrf = (String) DataToShare.getValue("gatewayCsrf");
            specs.addHeader("x-sc-gateway", jwt).addHeader("x-sc-csrf", csrf);
        }
        return specs.addHeader("Content-Type", "application/json").build();
    }

    public static RequestSpecification samFlowRequestSpec(String key) {
        String sam = (String) DataToShare.getValue("samJwt");
        String csrf = (String) DataToShare.getValue("samCsrf");

        RequestSpecBuilder reqSpecs = new RequestSpecBuilder()
                .setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"));

        if (key.equalsIgnoreCase("invalidSamJWT")) {
            reqSpecs.addHeader("x-sc-sam", "dfghj5fgh3we65_fghnm57i")
                    .addHeader("x-csrf-token", csrf);
        }
        else if (key.equalsIgnoreCase("invalidCSRF")) {
            reqSpecs.addHeader("x-sc-sam", sam)
                    .addHeader("x-csrf-token", "sd6hj4rt");
        }
        else if(key.equalsIgnoreCase("invalid")) {
            reqSpecs.addHeader("x-sc-sam", "dfghj5fgh3we65_fghnm57i")
                    .addHeader("x-csrf-token", "sd6hj4rt");
        }
        return reqSpecs.addHeader("Content-Type", "application/json").build();
    }

    public static RequestSpecification lamfFlowRequestSpec() {
        String lspJwt = (String) DataToShare.getValue("lspJwt");
        String lspCsrf = (String) DataToShare.getValue("lspCsrf");

        return new RequestSpecBuilder().setBaseUri(ConfigRead.getPropertyValue("nexum-api"))
                .addHeader("x-lsp-jwt", lspJwt)
                .addHeader("x-lsp-csrf", lspCsrf)
                .addHeader("Content-Type", "application/json").build();
    }

    public static RequestSpecification smallboardSpec() {

        if (ConfigRead.getPropertyValue("smallcaseapi_url").equals("https://api-stag.smallcase.com")) {
            return new RequestSpecBuilder()
                    .setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                    .addHeader("Authorization", "Bearer " + System.getenv("collectionSBStageAuth"))
                    .addHeader("Content-Type", "application/json")
                    .build();

        } else {
            return new RequestSpecBuilder()
                    .setBaseUri(ConfigRead.getPropertyValue("smallcaseapi_url"))
                    .addHeader("Authorization", "Bearer " + System.getenv("collectionSBProdAuth"))
                    .addHeader("Content-Type", "application/json")
                    .build();
        }
    }

    public static RequestSpecification pushRebalanceSpec() {

        if (ConfigRead.getPropertyValue("smallboard_url").equals("https://smallboard-be.stag.smallcase.com")) {
            return new RequestSpecBuilder()
                    .setBaseUri(ConfigRead.getPropertyValue("smallboard_url"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-jwt", System.getenv("pushRebalance_StageJwt"))
                    .build();

        } else {
            return new RequestSpecBuilder()
                    .setBaseUri(ConfigRead.getPropertyValue("smallboardProd_url"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-jwt", System.getenv("pushRebalance_ProdJwt"))
                    .build();
        }
    }

    public static RequestSpecification paginationSpec() {
        return new RequestSpecBuilder()
                .addQueryParam("pageNo", 1)
                .addQueryParam("pageSize", 10).build();
    }

    public static RequestSpecification checkSessionSpec() {

        if (ConfigRead.getPropertyValue("broker_url").equals("https://scb.stag.smallcase.com")) {
            return new RequestSpecBuilder()
                    .setBaseUri(ConfigRead.getPropertyValue("broker_url"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-domain-token", System.getenv("scb_stage_domain_token"))
                    .build();

        } else {
            return new RequestSpecBuilder()
                    .setBaseUri(ConfigRead.getPropertyValue("broker_url"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-domain-token", System.getenv("scb_prod_domain_token"))
                    .build();
        }
    }
}