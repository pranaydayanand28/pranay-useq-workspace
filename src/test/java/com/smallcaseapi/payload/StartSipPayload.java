package com.smallcaseapi.payload;

import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.orderFlow.SIPWeightConfig;
import commonutils.CurrentDate;
import commonutils.DataToShare;
import commonutils.GetSmallcaseID;

import java.io.IOException;

public class StartSipPayload {

    /**
     * Generates payload for Start SIP API call.
     *
     * @param broker Broker name (e.g., kite, icici)
     * @return JSON payload as a String
     * @throws IOException
     * @throws InterruptedException
     */
    public static String startSIP(String broker) throws IOException, InterruptedException {
        String iscid = (String) DataToShare.getValue("iscid");
        String scid = new GetSmallcaseID().getSCID();
        String name = new GetSmallcaseID().getSmallcaseName();
        String scheduledDate = CurrentDate.date();
        String frequency = SmallcaseResource.frequency;
        String amount = SmallcaseResource.sipAmount;
        String weightConfig = SIPWeightConfig.sipconfig(broker);

        StringBuilder payload = new StringBuilder();
        payload.append("{")
                .append("\"scheduledDate\": \"").append(scheduledDate).append("\",")
                .append("\"iscid\": \"").append(iscid).append("\",")
                .append("\"frequency\": \"").append(frequency).append("\",")
                .append("\"amount\": ").append(amount).append(",")
                .append("\"weightConfig\": ").append(weightConfig).append(",")
                .append("\"scid\": \"").append(scid).append("\",")
                .append("\"name\": \"").append(name).append("\"")
                .append("}");

        System.out.println("Request ------- " + payload);
        return payload.toString();
    }
}