package com.smallcaseapi.payload;

public class AddToWatchlistV2
{
    public static String AddSCIDToWatchlist()
    {
       return "{\"assetId\":\"SCAW_0001\",\"assetType\":\"smallcase\",\"initialIndex\":100}";
    }

    public static String AddInvalidSCIDToWatchlist()
    {
        return "{\"assetId\":\"SCAW_000\",\"assetType\":\"smallcase\",\"initialIndex\":100}";
    }

    public static String AddStockToWatchlist()
    {
        return "{\"assetId\":\"RELI\",\"assetType\":\"stock\",\"initialIndex\":100}";
    }

    public static String AddMFToWatchlist()
    {
        return "{\"assetId\":\"M_DSPXA\",\"assetType\":\"mutualFund\",\"initialIndex\":100}";
    }
}
