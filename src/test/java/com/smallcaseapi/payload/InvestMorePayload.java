package com.smallcaseapi.payload;

import java.io.IOException;

import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.SourceType;
import com.smallcaseapi.orderFlow.SmallcaseScidAPI;

import commonutils.DataToShare;
import commonutils.GetSmallcaseID;

public class InvestMorePayload {

	/**
	 * Generates the payload for the Invest More API call.
	 *
	 * @param broker The broker name to generate order payload for.
	 * @return JSON payload string.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String investMore(String broker) throws IOException, InterruptedException {
		String iscid = (String) DataToShare.getValue("iscid");
		String scid = new GetSmallcaseID().getSCID();
		String ordersPayload = SmallcaseScidAPI.smallcaseScid(broker);

		String investMorePayload = "{\n" +
				"  \"iscid\": \"" + iscid + "\",\n" +
				"  \"source\": \"" + SourceType.PROFESSIONAL + "\",\n" +
				"  \"scid\": \"" + scid + "\",\n" +
				"  \"orders\": " + ordersPayload + ",\n" +
				"  \"label\": \"" + ScOrderLabel.investMoreLabel + "\",\n" +
				"  \"clientType\": \"" + SmallcaseResource.clientType + "\",\n" +
				"  \"consent\": true\n" +
				"}";

		return investMorePayload;
	}
}