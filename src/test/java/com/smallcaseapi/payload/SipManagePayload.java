package com.smallcaseapi.payload;

import java.io.IOException;

import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.orderFlow.SIPWeightConfig;

import commonutils.CurrentDate;
import commonutils.DataToShare;

public class SipManagePayload {

	/**
	 * Generates payload for Manage SIP API call.
	 *
	 * @param broker Broker name (e.g., kite, icici)
	 * @return JSON payload as String
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String sipManage(String broker) throws IOException, InterruptedException {
		String iscid = (String) DataToShare.getValue("iscid");

		StringBuilder sipManagePayload = new StringBuilder();
		sipManagePayload.append("{")
				.append("\"iscid\": \"").append(iscid).append("\",")
				.append("\"scheduledDate\": \"").append(CurrentDate.date()).append("\",")
				.append("\"frequency\": \"").append(SmallcaseResource.frequency).append("\",")
				.append("\"sipAmount\": \"").append(SmallcaseResource.manageSipAm).append("\",")
				.append("\"weightConfig\": ").append(SIPWeightConfig.sipconfig(broker))
				.append("}");

		System.out.println("Payload: " + sipManagePayload);
		return sipManagePayload.toString();
	}
}