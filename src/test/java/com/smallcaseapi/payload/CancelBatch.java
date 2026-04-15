package com.smallcaseapi.payload;

import commonutils.DataToShare;
import java.io.IOException;

public class CancelBatch {
    public static String cancelBatchPayload() throws IOException {
        String iscid = (String) DataToShare.getValue("iscid");
        String batchId = (String) DataToShare.getValue("batchId");

        String cancelBatch = "{"
                + "\"iscid\": \"" + iscid + "\","
                + "\"batchId\": \"" + batchId + "\""
                + "}";

        return cancelBatch;
    }
}