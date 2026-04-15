package com.smallcaseapi.payload;

public class RemoveWatchlistV2
{
    public static String RemoveSCIDFromWatchlist()
    {
        return "{\"assetId\":\"SCAW_0001\",\"assetType\":\"smallcase\"}";
    }

    public static String RemoveStockFromWatchlist()
    {
        return "{\"assetId\":\"RELI\",\"assetType\":\"stock\"}";
    }

    public static String RemoveMFfromWatchlist()
    {
        return "{\"assetId\":\"M_DSPXA\",\"assetType\":\"mutualFund\"}";
    }
}
