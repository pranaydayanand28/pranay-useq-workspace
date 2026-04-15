package com.samBamConnect;

import com.smallcaseapi.BaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.ConfigRead;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.*;

import static io.restassured.RestAssured.given;

public class ConnectHelper extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ConnectHelper.class);
    private String gatewaySdkToken;
    private String transactionId;
    private String transactionJwt;
    private String smallcaseAuthToken;

    //Generate Gateway SDK Token
    public void generateGatewaySdkToken(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
            logger.info("Generating Gateway SDK Token");

            Response sdkTokenResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.getRequestSpec("sam"))
                    .log().all()
                    .when().get(SmallcaseResource.sdkToken)
                    .then().extract().response();

            logger.debug("Generate Gateway SDK Token response: {}", sdkTokenResponse.asString());

            if (sdkTokenResponse.statusCode() == 200) {
                gatewaySdkToken = JsonPathFinder.getJsPath(sdkTokenResponse).get("data.gatewaySdkToken").toString();
                if (gatewaySdkToken == null) {
                    logger.error("Gateway SDK token is null, Generate Gateway SDK Token response: {}", sdkTokenResponse.asString());
                }
            } else {
                logger.error("Generating Gateway SDK Token failed with status code: {}", sdkTokenResponse.statusCode());
                Assert.fail("Generating Gateway SDK Token failed with status code: "+ sdkTokenResponse.statusCode());
            }
            writeRequestAndResponseInReport(writer.toString(), sdkTokenResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Generating Gateway SDK Token: {}", e.getMessage(), e);
        }
    }

    //Initiate Gateway Session
    public void initiateGatewaySession(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("gatewayapi_url");
            logger.info("Initiate Gateway Session");

            Map<String, String> initSessionPayload = new HashMap<>();
            initSessionPayload.put("sdkToken", gatewaySdkToken);

            Response initSessionResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .headers("Content-Type", "application/json")
                    .body(initSessionPayload)
                    .log().all()
                    .when().post(SmallcaseResource.initSession)
                    .then().extract().response();

            logger.debug("Initiate Gateway Session response: {}", initSessionResponse.asString());

            if (initSessionResponse.statusCode() == 200) {
                String gatewayJwt = JsonPathFinder.getJsPath(initSessionResponse).get("data.gatewayToken").toString();
                String gatewayCsrf = JsonPathFinder.getJsPath(initSessionResponse).get("data.csrf").toString();
                if (gatewayJwt == null || gatewayCsrf == null) {
                    logger.error("Gateway JWT or CSRF is null, Initiate Gateway Session response: {}", initSessionResponse.asString());
                    Assert.fail("Gateway JWT or CSRF is null, Initiate Gateway Session response: "+ initSessionResponse.asString());
                } else {
                    DataToShare.setValue("gatewayJwt", gatewayJwt);
                    DataToShare.setValue("gatewayCsrf", gatewayCsrf);
                }
            } else {
                logger.error("Initiate Gateway Session failed with status code: {}", initSessionResponse.statusCode());
                Assert.fail("Initiate Gateway Session failed with status code: "+ initSessionResponse.statusCode());
            }
            writeRequestAndResponseInReport(writer.toString(), initSessionResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Initiate Gateway Session: {}", e.getMessage(), e);
        }
    }

    //Initiate Connect Transaction
    public void initiateConnectTransaction(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
            logger.info("Initiate Connect Transaction");

            Map<String, String> transactionPayload = new HashMap<>();
            transactionPayload.put("transactionType", "CONNECT");

            Response transactionResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.postRequestSpec("sam"))
                    .body(transactionPayload)
                    .log().all()
                    .when().post(SmallcaseResource.transaction)
                    .then().extract().response();

            logger.debug("Initiate Connect Transaction response: {}", transactionResponse.asString());

            if (transactionResponse.statusCode() == 200) {
                transactionId = JsonPathFinder.getJsPath(transactionResponse).get("data.transactionId").toString();
                if (transactionId == null) {
                    logger.error("transactionId is null, Initiate Connect Transaction response: {}", transactionResponse.asString());
                    Assert.fail("transactionId is null, Initiate Connect Transaction response: "+ transactionResponse.asString());
                }
            } else {
                logger.error("Initiate Connect Transaction failed with status code: {}", transactionResponse.statusCode());
                Assert.fail("Initiate Connect Transaction failed with status code: "+ transactionResponse.statusCode());
            }
            writeRequestAndResponseInReport(writer.toString(), transactionResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Initiate Connect Transaction: {}", e.getMessage(), e);
        }
    }

    //Update Transaction at Gateway
    public void updateTransactionAtGateway(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("gatewayapi_url");
            logger.info("Update Transaction at Gateway");

            Response transactionUpdateResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .queryParam("transactionId", transactionId)
                    .spec(RequestSpec.getRequestSpec("gateway"))
                    .log().all()
                    .when().get(SmallcaseResource.gatewayTransaction)
                    .then().extract().response();

            logger.debug("Update Transaction at Gateway response: {}", transactionUpdateResponse.asString());
            if (transactionUpdateResponse.statusCode() == 200) {
                transactionId = JsonPathFinder.getJsPath(transactionUpdateResponse).get("data.transaction.transactionId").toString();
                if (transactionId == null) {
                    logger.error("transactionId is null, Update Transaction at Gateway response: {}", transactionUpdateResponse.asString());
                    Assert.fail("transactionId is null, Update Transaction at Gateway response: "+ transactionUpdateResponse.asString());
                }
            } else {
                logger.error("Update Transaction at Gateway failed with status code: {}", transactionUpdateResponse.statusCode());
                Assert.fail("Update Transaction at Gateway failed with status code: "+ transactionUpdateResponse.statusCode());
            }
            if (JsonPathFinder.getJsPath(transactionUpdateResponse).get("data.transaction.status").toString().equalsIgnoreCase("COMPLETED")) {
                smallcaseAuthToken = JsonPathFinder.getJsPath(transactionUpdateResponse).get("data.transaction.success.smallcaseAuthToken").toString();
                if (smallcaseAuthToken == null) {
                    logger.error("smallcaseAuthToken is null, Update Transaction at Gateway response: {}", transactionUpdateResponse.asString());
                    Assert.fail("smallcaseAuthToken is null, Update Transaction at Gateway response: "+ transactionUpdateResponse.asString());
                }
            }
            writeRequestAndResponseInReport(writer.toString(), transactionUpdateResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Update Transaction at Gateway: {}", e.getMessage(), e);
        }
    }

    //Initiate Gateway Connect
    public void gatewayConnect(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("auth_url");
            logger.info("Initiate Gateway Connect");

            Map<String, String> gatewayConnectPayload = new HashMap<>();
            gatewayConnectPayload.put("transactionId", transactionId);

            Response gatewayConnectResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.postRequestSpec("smallcase"))
                    .body(gatewayConnectPayload)
                    .log().all()
                    .when().post(SmallcaseResource.gatewayConnect)
                    .then().extract().response();

            logger.debug("Initiate Gateway Connect response: {}", gatewayConnectResponse.asString());

            writeRequestAndResponseInReport(writer.toString(), gatewayConnectResponse.prettyPrint(), flow);
            ResponseAssert.assertThat(gatewayConnectResponse).returns_200_OK();
        }
        catch (Exception e) {
            logger.error("Exception during Initiate Gateway Connect: {}", e.getMessage(), e);
        }
    }

    //Gateway Session Refresh
    public void gatewaySessionRefresh(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("auth_url");
            logger.info("Gateway Session Refresh");

            Map<String, Object> gatewaySessionRefreshPayload = new HashMap<>();
            Map<String, Boolean> userConsentObject = new HashMap<>();
            userConsentObject.put("complete", true);
            gatewaySessionRefreshPayload.put("transactionId", transactionId);
            gatewaySessionRefreshPayload.put("userConsents", userConsentObject);

            Response gatewaySessionRefreshResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.postRequestSpec("smallcase"))
                    .body(gatewaySessionRefreshPayload)
                    .log().all()
                    .when().post(SmallcaseResource.gatewaySessionRefresh)
                    .then().extract().response();

            logger.debug("Gateway Session Refresh response: {}", gatewaySessionRefreshResponse.asString());

            if (gatewaySessionRefreshResponse.statusCode() == 200) {
                transactionJwt = JsonPathFinder.getJsPath(gatewaySessionRefreshResponse).get("data.jwt_transaction").toString();
                if (transactionJwt == null) {
                    logger.error("transactionJwt is null, Gateway Session Refresh response: {}", gatewaySessionRefreshResponse.asString());
                    Assert.fail("transactionJwt is null, Gateway Session Refresh response: "+ gatewaySessionRefreshResponse.asString());
                }
            } else {
                logger.error("Gateway Session Refresh failed with status code: {}", gatewaySessionRefreshResponse.statusCode());
                Assert.fail("Gateway Session Refresh failed with status code: "+ gatewaySessionRefreshResponse.statusCode());
            }
            writeRequestAndResponseInReport(writer.toString(), gatewaySessionRefreshResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Gateway Session Refresh: {}", e.getMessage(), e);
        }
    }

    //Generate Gateway Intent
    public void gatewayIntent(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
            logger.info("Generate Gateway Intent");

            Response gatewayIntentResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.getRequestSpec("smallcase"))
                    .header("gw-trx-jwt", transactionJwt)
                    .log().all()
                    .when().get(SmallcaseResource.gatewayIntent)
                    .then().extract().response();

            logger.debug("Generate Gateway Intent response: {}", gatewayIntentResponse.asString());

            writeRequestAndResponseInReport(writer.toString(), gatewayIntentResponse.prettyPrint(), flow);
            ResponseAssert.assertThat(gatewayIntentResponse).returns_200_OK();
        }
        catch (Exception e) {
            logger.error("Exception during Generate Gateway Intent: {}", e.getMessage(), e);
        }
    }

    //Connect SAM with BAM
    public void connect(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
            logger.info("Connect SAM with BAM");

            Map<String, String> connectPayload = new HashMap<>();
            connectPayload.put("transactionId", transactionId);
            connectPayload.put("gatewayAuthToken", smallcaseAuthToken);

            Response connectResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.postRequestSpec("sam"))
                    .body(connectPayload)
                    .when().post(SmallcaseResource.connect)
                    .then().extract().response();

            logger.debug("Connect SAM with BAM response: {}", connectResponse.asString());

            if (connectResponse.statusCode() == 200) {
                Boolean bamConnected = JsonPathFinder.getJsPath(connectResponse).get("data.flags.bamConnected");
                if (bamConnected.equals(true)) {
                    String samJwt = JsonPathFinder.getJsPath(connectResponse).get("data.sam").toString();
                    String samCsrf = JsonPathFinder.getJsPath(connectResponse).get("data.x-csrf-token").toString();
                    if (samJwt == null || samCsrf == null) {
                        logger.error("SAM JWT or CSRF is null, Connect SAM with BAM response: {}", connectResponse.asString());
                        Assert.fail("SAM JWT or CSRF is null, Connect SAM with BAM response: "+ connectResponse.asString());
                    } else {
                        DataToShare.setValue("samJwt", samJwt);
                        DataToShare.setValue("samCsrf", samCsrf);
                    }
                } else {
                    logger.error("SAM BAM Connection was failed, bamConnected is "+bamConnected," Check connect response: {}", connectResponse.asString());
                    Assert.fail("SAM BAM Connection was failed, bamConnected is "+bamConnected+" Check connect response: "+ connectResponse.asString());
                }  
            } else {
                logger.error("Connect SAM with BAM failed with status code: {}", connectResponse.statusCode());
                Assert.fail("Connect SAM with BAM failed with status code: "+ connectResponse.statusCode());
            }
            writeRequestAndResponseInReport(writer.toString(), connectResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Connect SAM with BAM: {}", e.getMessage(), e);
        }
    }

    //Check smallcase Session
    public void checkSession(String flow) {
        try {
            RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
            logger.info("Check smallcase Session");

            Response checkSessionResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.getRequestSpec("sam"))
                    .log().all()
                    .when().get(SmallcaseResource.checkSession)
                    .then().extract().response();

            logger.debug("Check smallcase Session response: {}", checkSessionResponse.asString());

            if (checkSessionResponse.statusCode() == 200) {
                Boolean isSamUser = JsonPathFinder.getJsPath(checkSessionResponse).get("data.isSamUser");
                Boolean isBamUser = JsonPathFinder.getJsPath(checkSessionResponse).get("data.isBamUser");
                if (isSamUser.equals(true) && isBamUser.equals(true)) {
                    String samJwt = JsonPathFinder.getJsPath(checkSessionResponse).get("data.sam").toString();
                    String samCsrf = JsonPathFinder.getJsPath(checkSessionResponse).get("data.x-csrf-token").toString();
                    if (samJwt == null || samCsrf == null) {
                        logger.error("SAM JWT or CSRF is null, Check smallcase Session response: {}", checkSessionResponse.asString());
                        Assert.fail("SAM JWT or CSRF is null, Check smallcase Session response: "+ checkSessionResponse.asString());
                    } else {
                        DataToShare.setValue("samJwt", samJwt);
                        DataToShare.setValue("samCsrf", samCsrf);
                    }
                } else {
                    logger.error("SAM BAM Connection was failed, isSamUser is "+isSamUser+" but isBamUser is "+isBamUser," Check smallcase Session response: {}", checkSessionResponse.asString());
                    Assert.fail("SAM BAM Connection was failed, isSamUser is "+isSamUser+" but isBamUser is "+isBamUser+" Check smallcase Session response: "+ checkSessionResponse.asString());
                }
            } else {
                logger.error("Check smallcase Session failed with status code: {}", checkSessionResponse.statusCode());
                Assert.fail("Check smallcase Session failed with status code: "+ checkSessionResponse.statusCode());
            }
            writeRequestAndResponseInReport(writer.toString(), checkSessionResponse.prettyPrint(), flow);
        }
        catch (Exception e) {
            logger.error("Exception during Check smallcase Session: {}", e.getMessage(), e);
        }
    }

}
