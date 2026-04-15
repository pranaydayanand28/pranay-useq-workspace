package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

public class Highlighted extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(Highlighted.class.getName());

    @Test(testName = "To validate Highlighted API with valid set of inputs", description = "Highlighted API : to validate Highlighted API upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public void HighlightedAPI_shouldReturn200(String broker){
        logger.info("Creating request for Highlighted - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.highlighted);

        logger.info("Creating request for Highlighted - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();
    }

    @Test(testName = "To validate schema for Highlighted API with valid set of inputs", description = "Highlighted API schema validation : to validate Highlighted API schema upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public void checkSchema(String broker){
        logger.info("Creating request for InvestmentInsightAPI - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.highlighted);

        logger.info("Creating request for InvestmentInsightAPI - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasValidSchema(IConst.HIGHLIGHTED_SCHEMA);
    }

    //This method is used to fetch the response and compare it with certain APIs since this API get called inside multiple APIs as internal service
    @SneakyThrows
    public static List<HashMap<String, String>> getHighlightedResponseAsArray(String broker){
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.highlighted);

        return JsonPathFinder.getJsPath(response).get("data");
    }

    //This method is being used to compare if the investment insights has the same scids as highlighted if a user is new and without any investments, drafts or watchlist
    @SneakyThrows
    public static List<String> getHighlightedSCIDSAsArray(String broker){
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.highlighted);

        ArrayList<String> listOfSCIDS = new ArrayList<>();
        ArrayList<HashMap<String, String>> list = JsonPathFinder.getJsPath(response).get("data");
        for(HashMap<String, String> map : list){
            listOfSCIDS.add(map.get("scid"));
        }
        return listOfSCIDS;
    }
}
