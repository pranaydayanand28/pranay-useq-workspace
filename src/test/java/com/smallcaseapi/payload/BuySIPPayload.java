package com.smallcaseapi.payload;

import java.io.IOException;

import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.SourceType;
import com.smallcaseapi.orderFlow.SIPWeightConfig;
import com.smallcaseapi.orderFlow.SmallcaseScidAPI;

import commonutils.CurrentDate;
import commonutils.GetSmallcaseID;

public class BuySIPPayload {

	/**
	 * Generates payload for Buy+SIP API with label "buy".
	 *
	 * @param broker the broker identifier
	 * @return JSON payload as String
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String buySIP(String broker) throws IOException, InterruptedException {

		StringBuilder buysipPayload = new StringBuilder();

		buysipPayload.append("{")
				.append("\"label\": \"").append(ScOrderLabel.buyLabel).append("\",")
				.append("\"scheduledDate\": \"").append(CurrentDate.date()).append("\",")
				.append("\"frequency\": \"").append(SmallcaseResource.frequency).append("\",")
				.append("\"sipAmount\": \"").append(SmallcaseResource.sipAmount).append("\",")
				.append("\"orders\": ").append(SmallcaseScidAPI.smallcaseScid(broker)).append(",")
				.append("\"weightConfig\": ").append(SIPWeightConfig.sipconfig(broker)).append(",")
				.append("\"scid\": \"").append(new GetSmallcaseID().getSCID()).append("\",")
				.append("\"source\": \"").append(SourceType.PROFESSIONAL).append("\",")
				.append("\"variety\": \"regular\"")
				.append("}");

		System.out.println("Payload: " + buysipPayload);
		return buysipPayload.toString();
	}
}