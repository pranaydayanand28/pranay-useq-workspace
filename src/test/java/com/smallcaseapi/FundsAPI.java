package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.enums.QueryParameters;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.ScOrderLabel;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.ApiConstants;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class FundsAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(FundsAPI.class.getName());

    @Test(priority = 0, testName = "To validate Funds API with no query params", description = "Funds API : to validate funds API with no query params")
    @Parameters({"broker"})
    @SneakyThrows
    public void fundsAPI_shouldReturn200(String broker) {

        /*IMPORTANT** This variable will be used in the tests where dataprovider has to be used, since both dp and parameters won't work together*/
        System.setProperty("brokerNameForDP", broker);

        logger.info("Creating request for funds API with no query params - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.funds);

        logger.info("Creating request for funds API with no query params - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        logger.info("Test passed for " + broker);
    }

    @Test(priority = 1, testName = "To validate Funds API with valid query params with purchase amount less than available amount", description = "Funds API : To validate Funds API with valid query params with purchase amount less than available amount")
    @Parameters({"broker"})
    @SneakyThrows
    public void fundsAPI_withSufficientAmount_shouldReturn200_sufficientAmountAsTrue(String broker) {

        logger.info("Creating request for funds API with purchase amount less than available amount - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.buyAmount.name(), QueryParameters.getBuy_amount())
                .queryParam(QueryParameters.sellAmount.name(), QueryParameters.getSell_amount())
                .queryParam(QueryParameters.stocksCount.name(), QueryParameters.getStock_count())
                .queryParam(QueryParameters.variety.name(), QueryParameters.getVariety_order())
                .when().get(SmallcaseResource.funds);

        logger.info("Creating request for funds API with purchase amount less than available amount - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .verifyResponseData("sufficientFunds", true);

        try {
            Number net = JsonPathFinder.getJsPath(response).get("data.net");
            Number requiredFunds = JsonPathFinder.getJsPath(response).get("data.requiredFunds");
            assertThat(requiredFunds.intValue()).isLessThan(net.intValue());
        } catch (Exception e) {
            logger.error("Unable to compare net and required, check for errors in response body " + Arrays.toString(e.getStackTrace()));
        }
        logger.info("Test passed for " + broker);
    }

    @Test(priority = 2, testName = "To validate Funds API with valid query params with purchase amount greater than available amount", description = "Funds API : To validate Funds API with valid query params with purchase amount greater than available amount")
    @Parameters({"broker"})
    @SneakyThrows
    public void fundsAPI_withoutSufficientAmount_shouldReturn200_sufficientAmountAsFalse(String broker) {

        logger.info("Creating request for funds API with purchase amount greater than available amount - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.buyAmount.name(), "100800000.9")
                .queryParam(QueryParameters.sellAmount.name(), QueryParameters.getSell_amount())
                .queryParam(QueryParameters.stocksCount.name(), QueryParameters.getStock_count())
                .queryParam(QueryParameters.variety.name(), QueryParameters.getVariety_order())
                .when().get(SmallcaseResource.funds);

        logger.info("Creating request for funds API with purchase amount greater than available amount - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .verifyResponseData("sufficientFunds", false);

        try {
            Number net = JsonPathFinder.getJsPath(response).get("data.net");
            Number requiredFunds = JsonPathFinder.getJsPath(response).get("data.requiredFunds");
            assertThat(net.intValue()).isLessThan(requiredFunds.intValue());
        } catch (Exception e) {
            logger.error("Unable to compare net and required, check for errors in response body " + Arrays.toString(e.getStackTrace()));
        }

        logger.info("Test passed for " + broker);
    }

    @Test(priority = 3, testName = "To validate Funds API with invalid query params and invalid token", description = "Funds API : To validate Funds API with invalid query params and invalid token")
    @Parameters({"broker"})
    @SneakyThrows
    public void fundsAPI_shouldReturn401(String broker) {

        logger.info("Creating request for funds API with purchase amount greater than available amount - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .queryParam("WrongKey", "WrongValue")
                .when().get(SmallcaseResource.funds);

        logger.info("Creating request for funds API with purchase amount greater than available amount - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .returnsValidErrorMessage(ApiConstants.unAuthorizedMessage);

        logger.info("Test passed for " + broker);
    }

    @Test(priority = 4, testName = "To validate schema for Funds API", description = "Funds API schema validation : To validate schema for Funds API")
    @Parameters({"broker"})
    @SneakyThrows
    public void checkSchema(String broker) {

        logger.info("Creating request for funds API with purchase amount less than available amount - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.buyAmount.name(), QueryParameters.getBuy_amount())
                .queryParam(QueryParameters.sellAmount.name(), QueryParameters.getSell_amount())
                .queryParam(QueryParameters.stocksCount.name(), QueryParameters.getStock_count())
                .queryParam(QueryParameters.variety.name(), QueryParameters.getVariety_order())
                .when().get(SmallcaseResource.funds);

        logger.info("Creating request for funds API with purchase amount less than available amount - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasValidSchema(IConst.FUNDS_SCHEMA);

        logger.info("Test passed for " + broker);
    }

    @Test(priority = 5, dataProvider = "LABEL", testName = "To validate buffer in required funds for Funds API", description = "Funds API required funds buffer : To validate buffer in required funds for Funds API")
    @SneakyThrows
    public void fundsAPI_forRequiredFundsBuffer_shouldReturn200(String broker, String label) {

        logger.info("Creating request for funds API with purchase amount less than available amount - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.buyAmount.name(), QueryParameters.getBuy_amount())
                .queryParam(QueryParameters.stocksCount.name(), QueryParameters.getStock_count())
                .queryParam(QueryParameters.variety.name(), QueryParameters.getVariety_order())
                .queryParam("label", label)
                .when().get(SmallcaseResource.funds);

        logger.info("Creating request for funds API with purchase amount less than available amount - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Initializing asserts for buffer logic");
        ResponseAssert.assertThat(response)
                .returns_200_OK();
        AssertionsForBufferLogic(response, broker, label);

        logger.info("Test passed for " + broker);
    }

    /*  Helper method, since dataprovider and parameters can't be used together
    @Parameters({"broker"})
    public void getBrokerNameParameter(String broker){
        System.setProperty("broker", broker);
    }*/

    /*Data provider for FUNDS API*/
    @DataProvider(name = "LABEL", parallel = false)
    public Object[][] labelForBuffer() {
        return new Object[][]{
                {System.getProperty("brokerNameForDP", "kite"), ScOrderLabel.buyLabel},
                {System.getProperty("brokerNameForDP", "kite"), ScOrderLabel.investMoreLabel},
                {System.getProperty("brokerNameForDP", "kite"), ScOrderLabel.manageLabel},
                {System.getProperty("brokerNameForDP", "kite"), ScOrderLabel.sipLabel},
                {System.getProperty("brokerNameForDP", "kite"), ScOrderLabel.rebalanceLabel}
        };
    }

    /*Abstracted logic and calculation for Funds API buffer*/
    public void AssertionsForBufferLogic(Response response, String broker, String label) {

        Float actualValue = 0.0f;
        if (JsonPathFinder.getJsPath(response).get("data.requiredFunds").getClass().getSimpleName().contains("Integer")
                || JsonPathFinder.getJsPath(response).get("data.requiredFunds").getClass().getSimpleName().contains("int")) {
            Integer i = JsonPathFinder.getJsPath(response).get("data.requiredFunds");
            actualValue = i.floatValue();
        } else if (JsonPathFinder.getJsPath(response).get("data.requiredFunds").getClass().getSimpleName().contains("Float")
                || JsonPathFinder.getJsPath(response).get("data.requiredFunds").getClass().getSimpleName().contains("float")) {
            actualValue = JsonPathFinder.getJsPath(response).get("data.requiredFunds");
        } else if (JsonPathFinder.getJsPath(response).get("data.requiredFunds").getClass().getSimpleName().contains("Double")
                || JsonPathFinder.getJsPath(response).get("data.requiredFunds").getClass().getSimpleName().contains("double")) {
            actualValue = JsonPathFinder.getJsPath(response).get("data.requiredFunds");
        }
        /*Following formula is used to calculate the required amount since for bank brokers, the required amount includes brokerage and other charges too
        fundsBuffer.buy * options.buyAmount + fundsBuffer.sell * options.sellAmount * allowedSellValue + fundsBuffer.minBrokerage * options.stocksCount*/
        if (broker.equals("hdfc")) {
            double expectedWithBrokerage = Double.parseDouble(QueryParameters.getBuy_amount())
                    + 0.03 * Double.parseDouble(QueryParameters.getBuy_amount())
                    + 32.5 * Integer.parseInt(QueryParameters.getStock_count());
            double expectedWithOutBrokerage = Double.parseDouble(QueryParameters.getBuy_amount());
            double expectedForBuy = expectedWithBrokerage + (expectedWithOutBrokerage * 0.005);
            double expectedForIMAndSIP = expectedWithBrokerage + (expectedWithOutBrokerage * 0.005);
            double actual = actualValue.doubleValue();

            logger.info("Asserting response " + (broker));
            switch (label) {
                case "BUY":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForBuy);
                    break;
                case "INVESTMORE":
                case "SIP":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForIMAndSIP);
                    break;
                default:
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedWithBrokerage);
            }
        } else if (broker.equals("axis")) {
            double expectedWithBrokerage = 1.03 * Double.parseDouble(QueryParameters.getBuy_amount()) + 5 * Integer.parseInt(QueryParameters.getStock_count());
            double expectedWithOutBrokerage = Double.parseDouble(QueryParameters.getBuy_amount());
            double expectedForBuy = expectedWithBrokerage + (expectedWithOutBrokerage * 0.005);
            double expectedForIMAndSIP = expectedWithBrokerage + (expectedWithOutBrokerage * 0.005);
            double actual = actualValue.doubleValue();

            logger.info("Asserting response " + (broker));
            switch (label) {
                case "BUY":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForBuy);
                    break;
                case "INVESTMORE":
                case "SIP":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForIMAndSIP);
                    break;
                default:
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedWithBrokerage);
            }
        } else if (broker.equals("kite")) {
            double expectedWithBrokerage = 1.01 * Double.parseDouble(QueryParameters.getBuy_amount());
            double expectedWithOutBrokerage = Double.parseDouble(QueryParameters.getBuy_amount());
            double expectedForBuy = expectedWithBrokerage + (expectedWithOutBrokerage * 0.005);
            double expectedForIMAndSIP = expectedWithBrokerage + (expectedWithOutBrokerage * 0.005);
            double actual = actualValue.doubleValue();

            logger.info("Asserting response " + (broker));
            switch (label) {
                case "BUY":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForBuy);
                    break;
                case "INVESTMORE":
                case "SIP":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForIMAndSIP);
                    break;
                default:
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedWithBrokerage);
            }
        }

        else {
            double expected = Double.parseDouble(QueryParameters.getBuy_amount());
            double actual = actualValue.doubleValue();
            double expectedForBuy = expected + (expected * 0.005);
            double expectedForIMAndSIP = expected + (expected * 0.005);

            logger.info("Asserting response " + (broker));
            switch (label) {
                case "BUY":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForBuy);
                    break;
                case "INVESTMORE":
                case "SIP":
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expectedForIMAndSIP);
                    break;
                default:
                    ResponseAssert.assertThat(response).verifyEquality((int) actual, (int) expected);
            }
        }
    }
}
