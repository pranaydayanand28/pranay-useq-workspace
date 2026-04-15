package com.smallcaseapi.payload;

import java.io.IOException;
import commonutils.DataToShare;

public class EndSipPayload {

	/**
	 * Constructs the payload for End SIP API call using iscid from shared data.
	 *
	 * @return JSON payload as a String
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String endSip() throws IOException, InterruptedException {
		String iscid = (String) DataToShare.getValue("iscid");

		String endSipPayload = "{"
				+ "\"iscid\": \"" + iscid + "\""
				+ "}";

		System.out.println("Payload: " + endSipPayload);
		return endSipPayload;
	}
}