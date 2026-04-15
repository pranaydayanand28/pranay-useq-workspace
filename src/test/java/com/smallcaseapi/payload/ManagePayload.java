package com.smallcaseapi.payload;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.SourceType;
import com.smallcaseapi.orderFlow.SmallcaseScidAPI;

import commonutils.DataToShare;
import commonutils.GetSmallcaseID;

public class ManagePayload {

	/**
	 * Generates payload for Manage API call using broker-specific orders.
	 *
	 * @param broker Broker name (e.g., kite, icici)
	 * @return JSON payload string
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String managePayload(String broker) throws FileNotFoundException, IOException, InterruptedException {
		String iscid = (String) DataToShare.getValue("iscid");
		String scid = new GetSmallcaseID().getSCID();
		String orders = SmallcaseScidAPI.smallcaseScid(broker);

		StringBuilder manageOrder = new StringBuilder();
		manageOrder.append("{")
				.append("\"iscid\": \"").append(iscid).append("\",")
				.append("\"source\": \"").append(SourceType.PROFESSIONAL).append("\",")
				.append("\"scid\": \"").append(scid).append("\",")
				.append("\"orders\": ").append(orders).append(",")
				.append("\"label\": \"").append(ScOrderLabel.manageLabel).append("\",")
				.append("\"clientType\": \"").append(SmallcaseResource.clientType).append("\",")
				.append("\"consent\": true")
				.append("}");

		return manageOrder.toString();
	}
}