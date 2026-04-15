package com.smallcaseapi.payload;

import com.google.gson.Gson;
import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.SourceType;
import com.smallcase.resource.pojos.PlaceOrder.Request.Order;
import com.smallcase.resource.pojos.PlaceOrder.Request.Root;
import commonutils.DataToShare;
import commonutils.IConst;
import commonutils.ReadJSON;

import java.util.ArrayList;

public class PlaceOrder {

    public static String createPayload() {

        String did = (String) DataToShare.getValue("createdDID");
        //This is the order array for the first stock
        Order order1 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1"))
                .setQuantity(1).setTransactionType(ScOrderLabel.buyLabel).build();

        //This is the order array for the second stock
        Order order2 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2"))
                .setQuantity(1).setTransactionType(ScOrderLabel.buyLabel).build();

        //This list signifies the list of orders with their constituents whihc would later be put into the place orders request
        ArrayList<Order> list = new ArrayList<>();
        list.add(order1);
        list.add(order2);

        //Root builder is the name of the lombok builder payload created for place orders API, basically returns the payload for the placeOrders API as JSON
        Root root = Root.builder().setDid(did).setClientType(SmallcaseResource.clientType)
                .setConsent(false).setScid(did)
                .setOrders(list)
                .setVariety(SmallcaseResource.varietyType)
                .setLabel(ScOrderLabel.buyLabel)
                .setSmallcaseName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Name"))
                .setSource(SourceType.CREATED.name()).build();

        Gson g = new Gson();
        return g.toJson(root);
    }

    public static String createPayloadForIM() {

        String did = (String) DataToShare.getValue("createdDID");
        String iscid = (String) DataToShare.getValue("iscid");
        Order order1 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1"))
                .setQuantity(1).setTransactionType(ScOrderLabel.buyLabel).build();
        Order order2 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2"))
                .setQuantity(1).setTransactionType(ScOrderLabel.buyLabel).build();
        ArrayList<Order> list = new ArrayList<>();
        list.add(order1);
        list.add(order2);

        Root root = Root.builder().setIscid(iscid).setClientType(SmallcaseResource.clientType)
                .setConsent(false).setScid(did)
                .setOrders(list)
                .setVariety(SmallcaseResource.varietyType)
                .setLabel(ScOrderLabel.investMoreLabel)
                .setSmallcaseName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Name"))
                .setSource(SourceType.CREATED.name()).build();

        Gson g = new Gson();
        return g.toJson(root);
    }

    public static String createPayloadForManage() {

        String did = (String) DataToShare.getValue("createdDID");
        String iscid = (String) DataToShare.getValue("iscid");
        Order order1 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1"))
                .setQuantity(1).setTransactionType(ScOrderLabel.buyLabel).build();
        Order order2 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2"))
                .setQuantity(1).setTransactionType(ScOrderLabel.buyLabel).build();
        ArrayList<Order> list = new ArrayList<>();
        list.add(order1);
        list.add(order2);

        Root root = Root.builder().setIscid(iscid).setClientType(SmallcaseResource.clientType)
                .setConsent(false).setScid(did)
                .setOrders(list)
                .setVariety(SmallcaseResource.varietyType)
                .setLabel(ScOrderLabel.manageLabel)
                .setSmallcaseName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Name"))
                .setSource(SourceType.CREATED.name()).build();

        Gson g = new Gson();
        return g.toJson(root);
    }

    public static String createPayloadForPartialExit() {

        String did = (String) DataToShare.getValue("createdDID");
        String iscid = (String) DataToShare.getValue("iscid");

        Order order1 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1"))
                .setQuantity(1).setTransactionType(ScOrderLabel.sellLabel).build();
        Order order2 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2"))
                .setQuantity(1).setTransactionType(ScOrderLabel.sellLabel).build();
        ArrayList<Order> list = new ArrayList<>();
        list.add(order1);
        list.add(order2);

        Root root = Root.builder().setIscid(iscid).setClientType(SmallcaseResource.clientType)
                .setConsent(false).setScid(did)
                .setOrders(list)
                .setVariety(SmallcaseResource.varietyType)
                .setLabel(ScOrderLabel.partialExitLabel)
                .setSmallcaseName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Name"))
                .setSource(SourceType.CREATED.name()).build();

        Gson g = new Gson();
        return g.toJson(root);
    }

    public static String createPayloadForExit() {

        String did = (String) DataToShare.getValue("createdDID");
        String iscid = (String) DataToShare.getValue("iscid");

        Order order1 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid1"))
                .setQuantity(1).setTransactionType(ScOrderLabel.sellLabel).build();
        Order order2 = Order.builder().setSid(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "sid2"))
                .setQuantity(1).setTransactionType(ScOrderLabel.sellLabel).build();
        ArrayList<Order> list = new ArrayList<>();
        list.add(order1);
        list.add(order2);

        Root root = Root.builder().setIscid(iscid).setClientType(SmallcaseResource.clientType)
                .setConsent(false).setScid(did)
                .setOrders(list)
                .setVariety(SmallcaseResource.varietyType)
                .setLabel(ScOrderLabel.exitLabel)
                .setSmallcaseName(ReadJSON.readJsonAndGetAsString(IConst.CREATED_STOCK_PICKS, "Name"))
                .setSource(SourceType.CREATED.name()).build();

        Gson g = new Gson();
        return g.toJson(root);
    }
}
