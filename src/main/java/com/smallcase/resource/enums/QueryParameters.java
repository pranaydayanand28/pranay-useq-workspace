package com.smallcase.resource.enums;

import commonutils.IConst;
import commonutils.ReadJSON;

//Keep the variable name same as enum value in json Data file too (***important***)
public enum QueryParameters {
    sortBy("Discover"),
    performSearch("Discover"),
    searchString("Discover"),
    includeBlocked("Discover"),
    text("Search"),
    Private("Discover"),
    Public("Discover"),
    type("BlogProxy"),
    buyAmount("fundsAPI"),
    sellAmount("fundsAPI"),
    stocksCount("fundsAPI"),
    variety("fundsAPI"),
    stocks("priceAndChangeAPI");

    //Initialize values here frr discover API query params and search API query param
    private static final String sort_by = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.sortBy.name());
    private static final String search_string = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.searchString.name());
    private static final boolean perform_search = ReadJSON.readJsonAndGetAsBoolean(IConst.QUERY_PARAMS_FILE, QueryParameters.performSearch.name());
    private static final boolean include_blocked = ReadJSON.readJsonAndGetAsBoolean(IConst.QUERY_PARAMS_FILE, QueryParameters.includeBlocked.name());
    private static final boolean isPrivate = ReadJSON.readJsonAndGetAsBoolean(IConst.QUERY_PARAMS_FILE, QueryParameters.Private.name());
    private static final boolean isPublic = ReadJSON.readJsonAndGetAsBoolean(IConst.QUERY_PARAMS_FILE, QueryParameters.Public.name());
    private static final String type_blogProxy = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.type.name());
    private static final String buy_amount = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.buyAmount.name());
    private static final String sell_amount = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.sellAmount.name());
    private static final String stock_count = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.stocksCount.name());
    private static final String variety_order = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.variety.name());
    private static final String stocks_name = ReadJSON.readJsonAndGetAsString(IConst.QUERY_PARAMS_FILE, QueryParameters.stocks.name());


    QueryParameters(String APIParamBelongsTo) {}


    public static String getSort_by_value() {
        return sort_by;
    }

    public static String getSearch_string_value() {
        return search_string;
    }

    public static boolean isPerformSearch() {
        return perform_search;
    }

    public static boolean isBlockedIncluded() {
        return include_blocked;
    }

    public static boolean isPrivate() {
        return isPrivate;
    }

    public static boolean isPublic() {
        return isPublic;
    }

    public static String getTypeForBlogProxy(){
        return type_blogProxy;
    }

    public static String getBuy_amount(){
        return  buy_amount;
    }

    public static String getSell_amount(){
        return  sell_amount;
    }

    public static String getStock_count(){
        return  stock_count;
    }

    public static String getVariety_order(){
        return  variety_order;
    }
    public static String getStock_name(){
        return  stocks_name;
    }
}
