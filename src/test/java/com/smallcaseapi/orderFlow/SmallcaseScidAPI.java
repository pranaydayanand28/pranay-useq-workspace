package com.smallcaseapi.orderFlow;

import com.asserts.ResponseAssert;
import com.google.gson.Gson;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.GetSmallcaseID;
import commonutils.JsonPathFinder;
import groovy.json.StringEscapeUtils;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class SmallcaseScidAPI {

    private static final Logger logger = LoggerFactory.getLogger(SmallcaseScidAPI.class.getName());
    private static Response response;

    @SneakyThrows
    public static String smallcaseScid(String broker) {

        /**
         * scid API call to validate mandatory key and to make final Payload which will
         * have order array
         */

        try {
            response = given()
                    .spec(RequestSpec.requestSpecification(broker))
                    .queryParam("scid", new GetSmallcaseID().getSCID())
                    .when().get(SmallcaseResource.SmallcaseScid);

            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();

        } catch (Exception e) {
            logger.error("The request failed with response " + response.asString());
        }
        int constituentsSize = JsonPathFinder.getJsPath(response).get("data.constituents.size()");
        HashMap<String, Object> sidinfo = new HashMap<String, Object>();
        HashMap<String, Object> orderInfo = new HashMap<String, Object>();
        JSONArray sidinfArr = new JSONArray();
        JSONArray orderArr = new JSONArray();

        for (int i = 0; i < constituentsSize; i++) {

            String sidValue = JsonPathFinder.getJsPath(response).get("data.constituents[" + i + "].sid");

            sidinfo.put("", sidValue);
            orderInfo.put("sid", sidValue);
            orderInfo.put("quantity", 2);
            orderInfo.put("transactionType", "BUY");

            /**
             * Gson to convert java object into json file. One file for Price api call and
             * other to use for order array
             */
            Gson g = new Gson();
            sidinfArr.add(g.toJson(sidinfo));
            orderArr.add(g.toJson(orderInfo));

        }

        String sidJson = StringEscapeUtils.unescapeJava(sidinfArr.toJSONString());

        String replc = sidJson.replace("[\"{", "");
        String finalSid = replc.replace("}\"]", "").replace("\"\":", "").replace("{\"", "").replace("}\"", "")
                .replace("", "").replace("\"", "").replace(",", "&");

        System.out.println("final sid =====> " + finalSid);

        String orderJson = StringEscapeUtils.unescapeJava(orderArr.toJSONString());
        System.out.println("orderCpy ======>" + orderJson);
        String finalOrderArr = orderJson.replace("\"{", "{").replace("}\"", "}");
        System.out.println(finalOrderArr);

        /**
         * priceandchange API call to check actual market price for sid
         */

        Response respprice = given().spec(RequestSpec.requestSpecification(broker)).queryParam("stocks", finalSid)
                .urlEncodingEnabled(false).when().get(SmallcaseResource.priceandchange).then().extract()
                .response();

        return finalOrderArr;

    }

    public static String generateMixedOrdersFromScid(String broker, int quantity) {

        try {
            response = given()
                    .spec(RequestSpec.requestSpecification(broker))
                    .queryParam("scid", new GetSmallcaseID().getSCID())
                    .when().get(SmallcaseResource.SmallcaseScid);

            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();

        } catch (Exception e) {
            logger.error("The request failed with response " + (response != null ? response.asString() : "null"));
        }

        int constituentsSize = JsonPathFinder.getJsPath(response).get("data.constituents.size()");
        JSONArray sidinfArr = new JSONArray();
        JSONArray orderArr = new JSONArray();
        Gson g = new Gson();

        for (int i = 0; i < constituentsSize; i++) {
            String sidValue = JsonPathFinder.getJsPath(response).get("data.constituents[" + i + "].sid");

            // Alternate between BUY and SELL
            String transactionType = (i % 2 == 0) ? "BUY" : "SELL";

            // sid info for price API
            HashMap<String, Object> sidinfo = new HashMap<>();
            sidinfo.put("", sidValue);
            sidinfArr.add(g.toJson(sidinfo));

            // order info for placing order
            HashMap<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("sid", sidValue);
            orderInfo.put("quantity", quantity);
            orderInfo.put("transactionType", transactionType);
            orderArr.add(g.toJson(orderInfo));
        }

        // Prepare finalSid string for price API
        String sidJson = StringEscapeUtils.unescapeJava(sidinfArr.toJSONString());
        String finalSid = sidJson.replace("[\"{", "")
                .replace("}\"]", "")
                .replace("\"\":", "")
                .replace("{\"", "")
                .replace("}\"", "")
                .replace("\"", "")
                .replace(",", "&");

        // Prepare final order array JSON
        String orderJson = StringEscapeUtils.unescapeJava(orderArr.toJSONString());
        String finalOrderArr = orderJson.replace("\"{", "{").replace("}\"", "}");

        // Print the final orders payload for verification
        System.out.println("Generated Orders Array (Mixed BUY & SELL) =====>");
        System.out.println(finalOrderArr);

        // Price API call
        Response respprice = given()
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam("stocks", finalSid)
                .urlEncodingEnabled(false)
                .when().get(SmallcaseResource.priceandchange)
                .then().extract()
                .response();

        return finalOrderArr;
    }
}