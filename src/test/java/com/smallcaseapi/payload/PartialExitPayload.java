package com.smallcaseapi.payload;

import java.io.IOException;

import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.SourceType;
import com.smallcaseapi.orderFlow.SmallcaseScidAPI;

import commonutils.DataToShare;
import commonutils.GetSmallcaseID;

public class PartialExitPayload {

	/**
	 * Generates payload for partial exit API call.
	 *
	 * @param broker Broker name (e.g., kite, icici)
	 * @return JSON payload as String
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String partialPayload(String broker) throws IOException, InterruptedException {
		String iscid = (String) DataToShare.getValue("iscid");
		String orderArr = SmallcaseScidAPI.smallcaseScid(broker).replace("BUY", "SELL");
		String scid = new GetSmallcaseID().getSCID();

		StringBuilder partialExit = new StringBuilder();
		partialExit.append("{")
				.append("\"iscid\": \"").append(iscid).append("\",")
				.append("\"source\": \"").append(SourceType.PROFESSIONAL).append("\",")
				.append("\"scid\": \"").append(scid).append("\",")
				.append("\"orders\": ").append(orderArr).append(",")
				.append("\"label\": \"").append(ScOrderLabel.partialExitLabel).append("\",")
				.append("\"clientType\": \"").append(SmallcaseResource.clientType).append("\",")
				.append("\"consent\": true")
				.append("}");

		return partialExit.toString();
	}
}