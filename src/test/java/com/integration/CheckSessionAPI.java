package com.integration;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.RedisUtil;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CheckSessionAPI extends BaseTest {

    private JSONObject requestBody;
    private static final Logger logger = LoggerFactory.getLogger(CheckSessionAPI.class);

    @DataProvider(name = "brokerData")
    public Object[][] brokerData() {
        String[] brokers = {"kite", "axis", "hdfc", "kotak", "angelbroking", "upstox",
                "aliceblue", "dhan", "edelweiss", "fisdom", "fundzbazar", "groww",
                "iifl", "motilal", "sbi"};
        List<Object[]> data = new ArrayList<>();

        for (String broker : brokers) {
            String envKey = "userId_" + broker.toLowerCase(); // matches GitHub env key
            String userId = System.getenv(envKey);

            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("No USERID found for {}", broker);
                userId = ""; // avoid null pointer
            }

            data.add(new Object[]{broker, userId});
        }

        return data.toArray(new Object[0][0]);
    }

    @Test(
            dataProvider = "brokerData",
            testName = "Check User Session",
            description = "To validate checkSession API"
    )
    public void checkUserSession(String broker, String userId) {

        // Dynamically fetch ITestContext from current test result
        org.testng.ITestContext context = Reporter.getCurrentTestResult().getTestContext();

        String segment = context.getCurrentXmlTest().getParameter("segment");
        String ipAddress = context.getCurrentXmlTest().getParameter("ipAddress");
        String brokerFunctionLogId = context.getCurrentXmlTest().getParameter("brokerFunctionLogId");

        if (userId == null || userId.isEmpty()) {
            logger.warn("Skipping {} — Missing userId env", broker);
            throw new SkipException("Missing userId for broker: " + broker);
        }

        System.setProperty("brokerName", broker);
        System.setProperty("userId", userId);

        logger.info("\n====================== {} ======================", broker.toUpperCase());
        logger.info("Broker={} Segment={} IP={} LogId={}", broker, segment, ipAddress, brokerFunctionLogId);

        try {
            // Redis ping
            String pingResponse = RedisUtil.ping();
            logger.info("Redis ping response: {}", pingResponse);

            String redisKey = "AT:" + userId;
            String latestActiveAccessToken = null;
            long latestExpireAt = 0;
            long currentTime = System.currentTimeMillis() / 1000;

            boolean redisEmpty = false;

            // Get latest active or any available token from Redis
            try (Jedis jedis = RedisUtil.getResource()) {
                if (jedis != null && jedis.exists(redisKey)) {
                    Map<String, String> userData = jedis.hgetAll(redisKey);

                    if (userData == null || userData.isEmpty()) {
                        redisEmpty = true;
                    } else {
                        logger.info("Token entries found for {} -> {}", redisKey, userData.size());

                        // Step 1️⃣: Try to find the latest *active* token
                        for (Map.Entry<String, String> entry : userData.entrySet()) {
                            try {
                                JSONObject json = new JSONObject(entry.getValue());
                                long expireAt = json.optLong("expireAt", 0);
                                if (expireAt > currentTime && expireAt > latestExpireAt) {
                                    latestExpireAt = expireAt;
                                    latestActiveAccessToken = json.optString("at", null);
                                }
                            } catch (Exception e) {
                                logger.warn("Invalid token JSON for key {}: {}", entry.getKey(), e.getMessage());
                            }
                        }

                        // Step 2️⃣: If no active token found, pick *any* token
                        if (latestActiveAccessToken == null) {
                            logger.warn("No active token found for user {} — picking any token available", userId);
                            for (Map.Entry<String, String> entry : userData.entrySet()) {
                                try {
                                    JSONObject json = new JSONObject(entry.getValue());
                                    latestActiveAccessToken = json.optString("at", null);
                                    if (latestActiveAccessToken != null && !latestActiveAccessToken.isEmpty()) {
                                        logger.info("Fallback token selected from Redis for {}", userId);
                                        break;
                                    }
                                } catch (Exception e) {
                                    logger.warn("Invalid token JSON while picking fallback: {}", e.getMessage());
                                }
                            }
                        }

                        if (latestActiveAccessToken == null) {
                            logger.error("No token (active or inactive) found in Redis for {}", userId);
                        } else {
                            logger.info("Selected access token (expiresAt={}): {}", latestExpireAt, latestActiveAccessToken);
                        }
                    }

                } else {
                    logger.warn("Redis key not found or Jedis unavailable: {}", redisKey);
                    redisEmpty = true;
                }
            }

            // Skip the test if Redis has no tokens
            if (redisEmpty || latestActiveAccessToken == null || latestActiveAccessToken.isEmpty()) {
                logger.warn("Skipping test — no token found in Redis for user {}", userId);
                throw new SkipException("No tokens found in Redis for user: " + userId);
            }

            // Build request body
            requestBody = new JSONObject();
            requestBody.put("brokerName", broker);

            JSONObject options = new JSONObject();
            options.put("segment", segment);
            options.put("accessToken", latestActiveAccessToken);
            options.put("ipAddress", ipAddress);
            requestBody.put("options", options);

            JSONObject ctx = new JSONObject();
            ctx.put("userId", userId);
            ctx.put("brokerFunctionLogId", brokerFunctionLogId);
            requestBody.put("context", ctx);

            // Hit checkSession API
            Response response = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.checkSessionSpec())
                    .log().all()
                    .body(requestBody.toString())
                    .when()
                    .post(SmallcaseResource.brokercheckSession)
                    .then()
                    .log().all()
                    .extract()
                    .response();

            logger.info("Response: {}", response.prettyPrint());

            int statusCode = response.getStatusCode();
            logger.info("HTTP Status Code: {}", statusCode);

            // Broker status validation
            if (statusCode >= 500) {
                logger.error("Broker seems DOWN - Received {} status", statusCode);
                Assert.fail("Broker is DOWN. Status Code: " + statusCode);
            } else if (statusCode >= 200 && statusCode < 500) {
                logger.info("Broker is UP - Status Code: {}", statusCode);
                Assert.assertTrue(true, "Broker is UP and responding");
            } else {
                logger.error("Unexpected HTTP status code: {}", statusCode);
                Assert.fail("Unexpected status code: " + statusCode);
            }

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        } catch (SkipException se) {
            throw se; // rethrow skip cleanly
        } catch (Exception e) {
            logger.error("Error executing checkSession API", e);
            throw e;
        }
    }
}