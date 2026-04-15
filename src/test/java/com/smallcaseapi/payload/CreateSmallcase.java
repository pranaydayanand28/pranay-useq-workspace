package com.smallcaseapi.payload;

import com.google.gson.Gson;
import com.smallcase.resource.pojos.createdSmallcase.Request.*;
import commonutils.IConst;
import commonutils.ReadJSON;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateSmallcase {

    static SidInfo sidInfo1 = SidInfo.builder().setName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sidInfo.name1"))
            .setSector(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sidInfo.sector1"))
            .setTicker(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sidInfo.ticker1")).build();

    static SidInfo sidInfo2 = SidInfo.builder().setName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sidInfo.name2"))
            .setSector(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sidInfo.sector2"))
            .setTicker(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sidInfo.ticker2")).build();

    static Constituent constituent1 = Constituent.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1"))
            .setShares(ReadJSON.readJsonAndGetAsDouble(IConst.CREATED_STOCK_PICKS, "shares1"))
            .setLocked(ReadJSON.readJsonAndGetAsBoolean(IConst.CREATED_STOCK_PICKS, "locked1"))
            .setWeight(ReadJSON.readJsonAndGetAsDouble(IConst.CREATED_STOCK_PICKS, "weight1"))
            .setSidInfo(sidInfo1).build();

    static Constituent constituent2 = Constituent.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2"))
            .setShares(ReadJSON.readJsonAndGetAsDouble(IConst.CREATED_STOCK_PICKS, "shares2"))
            .setLocked(ReadJSON.readJsonAndGetAsBoolean(IConst.CREATED_STOCK_PICKS, "locked2"))
            .setWeight(ReadJSON.readJsonAndGetAsDouble(IConst.CREATED_STOCK_PICKS, "weight2"))
            .setSidInfo(sidInfo2).build();

    static ArrayList<String> segmentList = new ArrayList<>(Arrays.asList(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1")
            , ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2")));

    static Segment segment = Segment.builder().setLabel("Test Segment").setConstituents(segmentList).build();
    static Info info = Info.builder().setName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Name"))
            .setShortDescription(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Description"))
            .setTier(null).build();

    static Stats stats = Stats.builder().setInitialValue(100).build();



    public static String createdPayload(){
        ArrayList<Constituent> constituents = new ArrayList<>();
        constituents.add(constituent1);
        constituents.add(constituent2);

        ArrayList<Segment> segments = new ArrayList<>();
        segments.add(segment);

        Root root = Root.builder().setDid(null).setScid(null).setSource("CREATED").setConstituents(constituents).setSegments(segments).setCompositionScheme("WEIGHTS")
                .setInfo(info).setStats(stats).build();
        Gson g = new Gson();
        return g.toJson(root);

    }
}
