package com.smallcaseapi.payload;

import java.io.IOException;

import com.smallcase.resource.SmallcaseResource;
import commonutils.DataToShare;

public class FixBatchPayload {

	/**
	 * Constructs payload for fix batch API using iscid and batchId from shared data.
	 *
	 * @return JSON payload as a String
	 * @throws IOException
	 */
	public static String fixBatchPayload() throws IOException {
		String iscid = (String) DataToShare.getValue("iscid");
		String batchId = (String) DataToShare.getValue("batchId");

		String fixBatch = "{"
				+ "\"iscid\": \"" + iscid + "\","
				+ "\"batchId\": \"" + batchId + "\","
				+ "\"variety\": \"regular\","
				+ "\"consent\": false,"
				+ "\"clientType\": \"" + SmallcaseResource.clientType + "\""
				+ "}";

		return fixBatch;
	}
}