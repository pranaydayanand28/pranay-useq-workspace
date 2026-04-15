package com.smallcaseapi.orderFlow;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import java.util.HashMap;

import commonutils.GetSmallcaseID;
import org.json.simple.JSONArray;
import com.google.gson.Gson;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.JsonPathFinder;
import groovy.json.StringEscapeUtils;
import io.restassured.response.Response;

public class SIPWeightConfig {

	public static String sipconfig(String broker) throws IOException, InterruptedException {

		/**
		 * scid API call in order to fetch final weight config .
		 * 
		 */

		Response response = given().spec(RequestSpec.requestSpecification(broker))
				.queryParam("scid", new GetSmallcaseID().getSCID()).when().get(SmallcaseResource.SmallcaseScid).then()
				.extract().response();

		System.out.println("scid respo" + response.asString());

		int constituentsSize = JsonPathFinder.getJsPath(response).get("data.constituents.size()");
		HashMap<String, Object> weightconfig = new HashMap<String, Object>();

		JSONArray weightArr = new JSONArray();

		for (int i = 0; i < constituentsSize; i++) {

			String sidValue = JsonPathFinder.getJsPath(response).get("data.constituents[" + i + "].sid");
			float weightValue = JsonPathFinder.getJsPath(response).get("data.constituents[" + i + "].weight");
			weightconfig.put("sid", sidValue);
			weightconfig.put("weight", weightValue);

			/**
			 * Gson to convert java object into json file. One file for Price api call and
			 * other to use for order array
			 */
			Gson g = new Gson();
			weightArr.add(g.toJson(weightconfig));

		}

		String weightJson = StringEscapeUtils.unescapeJava(weightArr.toJSONString());

		String replc = weightJson.replace("\"{", "{");
		String finalWeightconfg = replc.replace("}\"", "}");

		System.out.println("final" + finalWeightconfg);

		return finalWeightconfg;

	}

}
