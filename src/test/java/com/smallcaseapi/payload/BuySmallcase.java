package com.smallcaseapi.payload;

import java.io.IOException;
import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SourceType;
import com.smallcaseapi.BaseTest;
import com.smallcaseapi.orderFlow.SmallcaseScidAPI;
import commonutils.GetSmallcaseID;

public class BuySmallcase extends BaseTest{

	public static String buyOrderPayload(String broker) throws IOException, InterruptedException {
		String orders = SmallcaseScidAPI.smallcaseScid(broker);
		String scid = new GetSmallcaseID().getSCID();

		String buyPayload = "{\n" +
				"  \"label\": \"" + ScOrderLabel.buyLabel + "\",\n" +
				"  \"orders\": " + orders + ",\n" +
				"  \"scid\": \"" + scid + "\",\n" +
				"  \"source\": \"" + SourceType.PROFESSIONAL + "\",\n" +
				"  \"variety\": \"regular\"\n" +
				"}";

		System.out.println("Generated payload:\n" + buyPayload);
		return buyPayload;
	}
}
