package com.smallcase.resource;

import commonutils.ConfigRead;

public class ApiConstants {
    public final static int success = 200;
    public final static int created = 201;
    public final static int updated = 204;
    public final static int badRequest = 400;
    public final static int unAuthorized = 401;
    public final static int forbidden = 403;
    public final static int notFound = 404;
    public final static int serverError = 500;
    public final static int GATEWAY_TIMEOUT = 504;
    public final static String cookieSetMessage = "Cookie set on "+ ConfigRead.getPropertyValue("smallcaseapi_url").substring(8);
    public final static String invalidTokenMessage = "invalid request token";
    public final static String serverErrorMessage = "Please mail us at tech@smallcase.com. Mistake is on our side!";
    public final static String accessTokenError = "access token error";
    public final static String unAuthorizedMessage = "Invalid token";
    public final static Long API_LOAD_TIME = 3000L;
    public final static String BlogProxyAPI_SLUG_ERROR = "one of slug or type is required";
    public final static String inValidSamJWT = "Unable to verify jwt";
    public final static String inValidCSRF = "Invalid csrf token provided";
    public final static String inValidAssetType = "\"query.assetType\" must be one of [smallcase, mutualFund, stock]";
    public final static String inValidOrderListingAssetType = "\"query.assetType\" must be one of [smallcase, mutualFund, stock, mfSmallcase]";
    public final static String invalidPageNo = "\"query.pageNo\" must be a number";
    public final static String pageNoZero = "\"query.pageNo\" must be greater than 0";
    public final static String invalidPageSize = "\"query.pageSize\" must be a number";
    public final static String pageSizeZero = "\"query.pageSize\" must be greater than 0";
    public final static String noAssetType = "\"query.assetType\" is required";
    public final static String noPageNo = "\"query.pageNo\" is required";
    public final static String  noPageSize = "\"query.pageSize\" is required";
    public final static String noAssetId = "\"query.id\" is required";
}
