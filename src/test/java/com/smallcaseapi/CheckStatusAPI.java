package com.smallcaseapi;

import static io.restassured.RestAssured.given;
import com.asserts.ResponseAssert;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.response.Response;

public class CheckStatusAPI extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(CheckStatusAPI.class.getName());

	@Test(testName = "To validate Check market Status API", description = "Check Market Status API : to validate Check market Status API upon valid set of inputs")
	@Parameters({"broker"})
	@SneakyThrows
	public void checkStatusAPI_shouldReturn200(String broker){

		logger.info("Creating request for checkStatusAPI API - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam("broker", broker)
				.when()
				.get(SmallcaseResource.checkStatus);

		logger.info("Creating request for checkStatusAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
						.returns_200_OK()
								.hasHeaderApplicationJSON()
										.isWithinAcceptedTimeLimit();

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate schema for checkStatus API", description = "checkStatus API schema validation : to validate schema for checkStatus API")
	@Parameters({"broker"})
	@SneakyThrows
	public void checkSchema(String broker){

		logger.info("Checking schema for checkStatusAPI - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam("broker", broker)
				.when()
				.get(SmallcaseResource.checkStatus);

		logger.info("Checking schema for checkStatusAPI - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
						.returns_200_OK()
								.hasValidSchema(IConst.CHECK_STATUS_SCHEMA);

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate Check market Status API when user is not logged in", description = "Check Market Status API : to validate Check market Status API upon valid set of inputs when user is not logged in")
	@Parameters({"broker"})
	public void checkStatusAPI_WITHOUTLOGIN_shouldReturn200(String broker){

		logger.info("Creating request for checkStatusAPI API without login - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor)).header("x-sc-broker", broker)
				.spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
				.queryParam("broker", broker)
				.when()
				.get(SmallcaseResource.checkStatus);

		logger.info("Creating request for checkStatusAPI API without login - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
				.hasHeaderApplicationJSON()
				.isWithinAcceptedTimeLimit();

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate Check market Status API when no broker is provided in query param and header", description = "Check Market Status API : to validate Check market Status API when no broker is provided in query param and header")
	@Parameters({"broker"})
	@SneakyThrows
	public void checkStatusAPI_shouldReturn400(String broker){

		logger.info("Creating request for checkStatusAPI API - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecificationWithoutBrokerNameInHeaderAndLoggedOutState())
				.when()
				.get(SmallcaseResource.checkStatus);

		logger.info("Creating request for checkStatusAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_401_UNAUTHORIZED();

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate Check market Status API when invalid broker is provided in query param and header", description = "Check Market Status API : to validate Check market Status API when invalid broker is provided in query param and header")
	@Parameters({"broker"})
	@SneakyThrows
	public void checkStatusAPIWithInvalidBroker_shouldReturn200(String broker){

		logger.info("Creating request for checkStatusAPI API - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecificationWithoutBrokerNameInHeaderAndLoggedOutState())
				.queryParam("broker", "Wrong")
				.when()
				.get(SmallcaseResource.checkStatus);

		logger.info("Creating request for checkStatusAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();


        logger.info("Test passed for " + broker);
	}

/* This method is used to check if market is open/ close during order placement */
	@SneakyThrows
	public boolean isMarketOpen(String broker){

		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam("broker", broker)
				.when()
				.get(SmallcaseResource.checkStatus);

		return response.jsonPath().get("data.marketOpen");
	}
}
