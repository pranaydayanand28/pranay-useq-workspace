package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import commonutils.DataToShare;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import resource.reports.LogStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class GetUser extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GetUser.class.getName());

    private GetUser() {
    }

    /*This method will not act as a test, rather it will be called after every action inside Login(order etc.), to perform certain actions and fetch certains values */
    /*
    @param accepts broker name and the type of action item required to be fetched
    * */
    public static ArrayList<HashMap<String, String>> getActionItems(String broker, String whichActionItem) throws IOException {
        Response response = given().spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getUser);

        ArrayList<HashMap<String, String>> fix = JsonPathFinder.getJsPath(response).get("data.actions.fix");
        ArrayList<HashMap<String, String>> rebalance = JsonPathFinder.getJsPath(response).get("data.actions.rebalance");
        ArrayList<HashMap<String, String>> buy = JsonPathFinder.getJsPath(response).get("data.actions.buy");
        ArrayList<HashMap<String, String>> sip = JsonPathFinder.getJsPath(response).get("data.actions.sip");
        ArrayList<HashMap<String, String>> subscription = JsonPathFinder.getJsPath(response).get("data.actions.subscription");

        switch (whichActionItem) {
            case "fix":
                return fix;
            case "rebalance":
                return rebalance;
            case "buy":
                return buy;
            case "sip":
                return sip;
            case "subscription":
                return subscription;
            default:
                throw new IllegalArgumentException("no Data available to be returned");
        }
    }

    /*This method will not act as a test, rather it will be called after every action inside Login(order etc.), to perform certain actions and fetch certains values */
    /*
    @param accepts broker name and the type of action item required to be fetched
    * */
    public static ArrayList<HashMap<String, String>> getUserSmallcaseDetails(String broker, String whichUserData) throws IOException {
        Response response = given().spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getUser);

        ArrayList<HashMap<String, String>> draftSmallcases = JsonPathFinder.getJsPath(response).get("data.draftSmallcases");
        ArrayList<HashMap<String, String>> investedSmallcases = JsonPathFinder.getJsPath(response).get("data.investedSmallcases");
        ArrayList<HashMap<String, String>> exitedSmallcases = JsonPathFinder.getJsPath(response).get("data.exitedSmallcases");
        ArrayList<HashMap<String, String>> smallcaseWatchlist = JsonPathFinder.getJsPath(response).get("data.smallcaseWatchlist");

        switch (whichUserData) {
            case "draft":
                return draftSmallcases;
            case "invested":
                return investedSmallcases;
            case "exited":
                return exitedSmallcases;
            case "watchlist":
                return smallcaseWatchlist;
            default:
                throw new IllegalArgumentException("no Data available to be returned");
        }
    }

    public static void ifRepairRequired(String broker) throws IOException {
        Response response = given().spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getUser);
        ArrayList<Object> fixArray = JsonPathFinder.getJsPath(response).get("data.actions.fix");

        DataToShare.setValue("fixSize", fixArray.size());
        logger.info("Checked for fix array size......");
    }

    public static boolean ifRepairRequired(String broker, String iscid) throws IOException {
        LogStatus.info("---- Checking if repair is required on the given iscid ----");
        List<HashMap<String, String>> overAllList = getActionItems(broker, "fix");

        for (HashMap<String, String> strings : overAllList) {
            if (strings.containsValue(iscid)) {
                return true;
            }
        }
        return false;
    }

    @Test(priority = 121, testName = "Get User Details with valid Input", description = "GetUser : Should fetch all the user details from users collection")
    @Parameters({"broker"})
    public void getUserDetails_valid(String broker) throws IOException {

        logger.info("Creating request for getUserDetails_valid - start " + (broker));
        Response response =
                given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(RequestSpec.requestSpecification(broker))
                        .when()
                        .get(SmallcaseResource.getUser);

        logger.info("Creating request for getUserDetails_valid - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker); //to write response to Extent-Report

        /*asserting response data*/
        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        List<Object> investedSmallcaseList = JsonPathFinder.getJsPath(response).get("data.investedSmallcases");
        List<Object> exitedSmallcaseList = JsonPathFinder.getJsPath(response).get("data.exitedSmallcases");
        List<Object> toCheckIfInvested = Stream.concat(investedSmallcaseList.stream(), exitedSmallcaseList.stream()).collect(Collectors.toList());

        if (JsonPathFinder.getJsPath(response).get("data.flags.onboarding.invested"))
            assertThat(toCheckIfInvested.size()).isGreaterThan(0);
    }

    @Parameters({"broker"})
    @Test(priority = 123, testName = "Get User Details with Invalid Input", description = "GetUser : Should throw 401 unauthorized due to invalid input")
    public void getUserDetails_invalid(String broker) throws IOException {

        logger.info("Creating request for getUserDetails_invalid - start " + (broker));
        Response response =
                given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(RequestSpec.requestSpecificationForUnauthorized()).when()
                        .get(SmallcaseResource.getUser);

        logger.info("Creating request for getUserDetails_invalid - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker); //to write response to Extent-Report

        /*asserting response data*/
        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .returnsValidErrorMessage(ApiConstants.unAuthorizedMessage);
    }

    @Test(priority = 122, testName = "To check if the schema is correct", description = "GetUser : Should validate response JSON schema")
    @Parameters({"broker"})
    public void checkJSONSchema(String broker) throws IOException {

        logger.info("Checking schema for get user API - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getUser);

        logger.info("Checking schema for get user API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker); //to write response to Extent-Report

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasValidSchema(IConst.getUserResponseJSONSchema);
    }

    /*This method will not act as a test, rather it will be called after every action inside Login(order etc.), to perform certain actions and fetch list of SCIDS in any of the available arrays values */
    /*
    @param accepts broker name and the type of action item required to be fetched
    * */
    public static ArrayList<String> getUserSmallcaseSCIDDetails(String broker, String whichUserData) throws IOException {
        Response response = given().spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.getUser);
        ArrayList<String> SCIDS = new ArrayList<>();

        ArrayList<HashMap<String, String>> draftSmallcases = JsonPathFinder.getJsPath(response).get("data.draftSmallcases");
        ArrayList<HashMap<String, String>> investedSmallcases = JsonPathFinder.getJsPath(response).get("data.investedSmallcases");
        ArrayList<HashMap<String, String>> exitedSmallcases = JsonPathFinder.getJsPath(response).get("data.exitedSmallcases");
        ArrayList<HashMap<String, String>> smallcaseWatchlist = JsonPathFinder.getJsPath(response).get("data.smallcaseWatchlist");

        switch (whichUserData) {
            case "draft":
                for(HashMap<String, String> map : draftSmallcases){
                    SCIDS.add(map.get("scid"));
                }
                return SCIDS;
            case "invested":
                for(HashMap<String, String> map : investedSmallcases){
                    SCIDS.add(map.get("scid"));
                }
                return SCIDS;
            case "exited":
                for(HashMap<String, String> map : exitedSmallcases){
                    SCIDS.add(map.get("scid"));
                }
                return SCIDS;
            case "watchlist":
                for(HashMap<String, String> map : smallcaseWatchlist){
                    SCIDS.add(map.get("scid"));
                }
                return SCIDS;
            default:
                throw new IllegalArgumentException("no Data available to be returned");
        }
    }
}
