package com.smallcaseapi.createsc;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;

import commonutils.ExcelRead;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import commonutils.TextFileWriter;
import groovy.json.StringEscapeUtils;
import io.restassured.response.Response;

public class StockInfoAPI implements IConst {

	//Logger logger = Logger.getLogger(StockInfoAPI.class);

	@Test
	public static String stockInfo(String broker) throws IOException{

		/*
		 * In this class execution happening for stockInfo API in order to fetch Payload
		 * for Save API .
		 */

		ExcelRead excelRead = new ExcelRead();
		String Sheet = "Stocks search";
		String sheet1 = "StockIndex";
		/*
		 * Taking query for stockInfo api from stockquey sheet and storing in stockquery
		 * variable
		 */
		String stockquery = excelRead.getCellData(StockQuery, Sheet, 0, 2);
		System.out.println("check" + stockquery);
		Response response = given().spec(RequestSpec.requestSpecification(broker)).urlEncodingEnabled(false).log().all()
				.queryParam(stockquery).when().get(SmallcaseResource.stockInfo).then().extract().response();
		
		System.out.println(response.asString());

		/*
		 * In order to create payload for Save API we need to create 1)JSONobject
		 * allValue which is upper most object for this payload. 2)info Map to add
		 * information object detail (name,shortDescription)
		 */

		JSONObject allValue = new JSONObject();
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> stats = new HashMap<String, Object>();

		/*
		 * Adding all value inside object allvalue
		 */
		allValue.put("source", excelRead.getCellData(StockQuery, sheet1, 6, 2));
		allValue.put("scid", null);
		allValue.put("did", null);
		allValue.put("compositionScheme", excelRead.getCellData(StockQuery, sheet1, 3, 2));

		/*
		 * Adding info & stats object related key value pair inside info object .
		 */
		info.put("name", excelRead.getCellData(StockQuery, sheet1, 7, 2));
		info.put("shortDescription", excelRead.getCellData(StockQuery, sheet1, 8, 2));
		stats.put("initialValue", 100);
		/*
		 * Adding info object value inside info key in allValue jsonobject
		 */

		allValue.put("info", info);
		allValue.put("stats", stats);

		Gson g = new Gson();

		/*
		 * Creating object of List ,json object and json array to make payload in same
		 * structure . 1)jsonArrayPayload is object of arraylist where we will be adding
		 * our constituents array in proper indexing . 2)constituentsARR where we will
		 * be adding final constituents Java Objects into their JSON representation
		 * 3)requestParams object to add key value pair .
		 */
		List<Map<String, Object>> jsonArrayPayload = new ArrayList<>();
		HashMap<String, Object> constituents = new HashMap<String, Object>();
		JSONArray constituentsARR = new JSONArray();
		JSONObject requestParams = new JSONObject();

		/**
		 * Taking count of Xl sheet from where we are taking our stock which is query
		 * param for stockinfo api . doing iteration based on xl row count .
		 */

		int count = excelRead.getRowCount(StockQuery,sheet1);
		for (int i = 2; i <= count; i++) {

			/**
			 * Storing stock value in Value variable which we are taking from sheet1.
			 */
			String Value = excelRead.getCellData(StockQuery, sheet1, 0, i);
			System.out.println(excelRead.getCellData(StockQuery, sheet1, 0, i));

			/*
			 * Storing sector , name, ticker info from stockinfo api response from each
			 * iteration .
			 **/

			String sectorNAme = JsonPathFinder.getJsPath(response).get("data." + Value + ".stock.info.sector");
			String name = JsonPathFinder.getJsPath(response).get("data." + Value + ".stock.info.name");
			String ticker = JsonPathFinder.getJsPath(response).get("data." + Value + ".stock.info.ticker");
			String sid = JsonPathFinder.getJsPath(response).get("data." + Value + ".sid");

			System.out.println("first value" + i + "" + name + sectorNAme + ticker);

			/**
			 * Adding in below json object inside for loop so that in each iteration value
			 * getting stored . Its typ of object thats why storing in json object
			 */
			requestParams.put("name", name);
			requestParams.put("sector", sectorNAme);
			requestParams.put("ticker", ticker);

			/**
			 * Adding in constituents map which can have both string and object value .
			 */
			constituents.put("shares", 0.03);
			constituents.put("weight", 0.5);
			constituents.put("sid", sid);
			constituents.put("sidInfo", requestParams);

			/*
			 * Adding final constituent map inside constituentsARR , in which constituent
			 * java Objects into their JSON representation.
			 **/
			constituentsARR.add(g.toJson(constituents));
		}
		/*
		 * excelRead.getCellData(StockQuery, sheet1, 2, i) Inside jsonArrayPayload we
		 * are adding constituentsARR array.
		 */

		jsonArrayPayload.addAll(constituentsARR);
		/**
		 * Adding jsonArrayPayload inside main object(allValue) which will create
		 * constituents array inside that it will have all required values .
		 */
		allValue.put("constituents", jsonArrayPayload);

		System.out.println(allValue);

		/**
		 * We are creating final payload in valid json format .
		 */
		String payload = StringEscapeUtils.unescapeJava(allValue.toString());

		System.out.println(payload);

		String createPayload = payload.replace("\"{", "{").replace("}\"", "}");

		TextFileWriter.fileWriter(IConst.createPayload, createPayload);

		System.out.println("++++"+createPayload);
		return createPayload;


	}

}
