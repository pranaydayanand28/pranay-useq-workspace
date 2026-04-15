package com.smallcaseapi.payload;

import java.io.IOException;

import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.SourceType;
import com.smallcaseapi.orderFlow.SmallcaseScidAPI;

import commonutils.DataToShare;
import commonutils.GetSmallcaseID;

public class ExitSmallcasePayload {

	/**
	 * Generates payload for Place Order API with 'exit' label.
	 *
	 * @param broker Broker name (e.g., kite, icici)
	 * @return JSON payload as a String
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String exitPayload(String broker) throws IOException, InterruptedException {
		String iscid = (String) DataToShare.getValue("iscid");

		// Replace BUY with SELL for exit orders
		String orderArr = SmallcaseScidAPI.smallcaseScid(broker).replace("BUY", "SELL");

		StringBuilder exitOrder = new StringBuilder();
		exitOrder.append("{")
				.append("\"iscid\": \"").append(iscid).append("\",")
				.append("\"source\": \"").append(SourceType.PROFESSIONAL).append("\",")
				.append("\"scid\": \"").append(new GetSmallcaseID().getSCID()).append("\",")
				.append("\"orders\": ").append(orderArr).append(",")
				.append("\"label\": \"").append(ScOrderLabel.exitLabel).append("\",")
				.append("\"clientType\": \"").append(SmallcaseResource.clientType).append("\",")
				.append("\"consent\": true")
				.append("}");

		return exitOrder.toString();
	}
}