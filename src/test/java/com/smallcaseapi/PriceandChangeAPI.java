package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.enums.QueryParameters;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class PriceandChangeAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PriceandChangeAPI.class.getName());

    @Test(testName = "To validate Market price and change API", description = "Market price API : to validate Market price and change API upon valid set of inputs")
    @Parameters({"broker"})
    public void marketPrice_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for PriceandChangeAPI - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam(QueryParameters.stocks.name(), QueryParameters.getStock_name())
				.when()
                .get(SmallcaseResource.priceandchange);

        logger.info("Creating request for PriceandChangeAPI - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();
    }

    @Test(testName = "To validate schema Market price and change API", description = "Market price API schema validation: to validate schema for Market price and change API upon valid set of inputs")
    @Parameters({"broker"})
    public void checkSchema(String broker) throws IOException {

        logger.info("Creating request for PriceandChangeAPI - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.stocks.name(), QueryParameters.getStock_name())
				.when()
                .get(SmallcaseResource.priceandchange);

        logger.info("Creating request for PriceandChangeAPI - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.PRICEANDCHANGE_SCHEMA);
    }

    @Test(testName = "To validate Market price and change API when no stock is provided in query", description = "Market price API : To validate Market price and change API when no stock is provided in query")
    @Parameters({"broker"})
    public void marketPrice_shouldReturn400(String broker) throws IOException {

        logger.info("Creating request for PriceandChangeAPI - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.when()
                .get(SmallcaseResource.priceandchange);

        logger.info("Creating request for PriceandChangeAPI - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_400_BADREQUEST().returnsValidErrorMessage("Stocks cannot be empty");
    }

    @Test(testName = "To validate Market price and change API when wrong stock is provided in query", description = "Market price API : To validate Market price and change API when wrong stock is provided in query")
    @Parameters({"broker"})
    public void marketPriceForWrongStock_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for PriceandChangeAPI - start " + (broker));
        Response response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam(QueryParameters.stocks.name(), "wrong")
                .when()
                .get(SmallcaseResource.priceandchange);

        logger.info("Creating request for PriceandChangeAPI - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();
        ;
    }
}
