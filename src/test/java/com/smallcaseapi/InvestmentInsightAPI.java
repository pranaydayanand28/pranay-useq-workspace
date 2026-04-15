package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class InvestmentInsightAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentInsightAPI.class.getName());

    @Test(testName = "To validate Investment Insights API", description = "Investment Insights API : to validate Investment Insights API upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public void investmentInsights_shouldReturn200(String broker) {

        logger.info("Creating request for InvestmentInsightAPI - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.investmentInsight);

        logger.info("Creating request for InvestmentInsightAPI - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();
    }

    @Test(testName = "To validate Investment Insights API when no user is logged in", description = "Investment Insights API : to validate Investment Insights API when no user is logged in")
    @Parameters({"broker"})
    public void investmentInsights_WITHOUTLOGIN_shouldReturn401(String broker) {

        logger.info("Creating request for InvestmentInsightAPI without login - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .when()
                .get(SmallcaseResource.investmentInsight);

        logger.info("Creating request for InvestmentInsightAPI without login- end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();
    }

    @Test(testName = "To validate schema for Investment Insights API", description = "Investment Insights API schema validation: to validate schema for Investment Insights API upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public void checkSchema(String broker) {

        logger.info("Checking schema for Investment Insights - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.investmentInsight);

        logger.info("Checking schema for Investment Insights - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.INVESTMENT_INSIGHTS_SCHEMA);
        logger.info("Test passed for " + broker);
    }

    //This method fetches the list of SCIDS in response
    @SneakyThrows
    public List<String> getInvestmentInsightsSCIDDataAsArray(String broker) {
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.investmentInsight);

        ArrayList<String> listOfSCIDS = new ArrayList<>();
        try {
            ArrayList<HashMap<String, String>> list = JsonPathFinder.getJsPath(response).get("data");
            for (HashMap<String, String> map : list) {
                listOfSCIDS.add(map.get("scid"));
            }
        }
        catch (Exception e){
            logger.info(Arrays.toString(e.getStackTrace()));
        }
        return listOfSCIDS;
    }

    /*As per investment insights logic, if user is new and has not invested yet, the highlighted smallcases for that publisher/ broker type is shown.
    However, if the user is invested or has created some drafts or made some watchlist, insight starts throwing invested, drafted or watchlist smallcases too in the insights section
    Hence this logic basically checks if scids in investment insights are either from highlighted list or from the user journey*/
    @Test(testName = "To validate response body for Investment Insights API", description = "Investment Insights API response body validation: to validate that the API shows correct set of smallcases in insights for Investment Insights API upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public void validateResponseData(String broker) {

        if (GetUser.getUserSmallcaseDetails(broker, "invested").size() > 0) {
            Assertions.assertThat(Stream.concat(Highlighted.getHighlightedSCIDSAsArray(broker).stream(), GetUser.getUserSmallcaseSCIDDetails(broker, "invested").stream())
                    .collect(Collectors.toList())).containsAll(getInvestmentInsightsSCIDDataAsArray(broker));
        }
        else if (GetUser.getUserSmallcaseDetails(broker, "watchlist").size() > 0) {
            Assertions.assertThat(Stream.concat(Highlighted.getHighlightedSCIDSAsArray(broker).stream(), GetUser.getUserSmallcaseSCIDDetails(broker, "watchlist").stream())
                    .collect(Collectors.toList())).containsAll(getInvestmentInsightsSCIDDataAsArray(broker));
        }
        else if (GetUser.getUserSmallcaseDetails(broker, "draft").size() > 0) {
            Assertions.assertThat(Stream.concat(Highlighted.getHighlightedSCIDSAsArray(broker).stream(), GetUser.getUserSmallcaseSCIDDetails(broker, "draft").stream())
                    .collect(Collectors.toList())).containsAll(getInvestmentInsightsSCIDDataAsArray(broker));
        }
        else {
            Assertions.assertThat(Highlighted.getHighlightedSCIDSAsArray(broker)).containsAll(getInvestmentInsightsSCIDDataAsArray(broker));
        }
    }
}
