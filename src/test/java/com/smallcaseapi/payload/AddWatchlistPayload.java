package com.smallcaseapi.payload;

import commonutils.GetSmallcaseID;
import org.json.simple.JSONObject;

public class AddWatchlistPayload {

	public static String addWatchlistPayload() {

		return "{\r\n" + "	\"scid\": \"" + new GetSmallcaseID().getSCID() + "\"\r\n" + "}";

	}

	public static JSONObject addWatchListInvalidPayload() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sc", new GetSmallcaseID().getSCID());
		return jsonObject;
	}

	public static JSONObject addWatchListInvalidSCID() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("scid", "wrong_101");
		return jsonObject;
	}
}
