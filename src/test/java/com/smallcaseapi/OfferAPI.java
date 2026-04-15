package com.smallcaseapi;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import com.asserts.ResponseAssert;
import commonutils.IConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.response.Response;

public class OfferAPI extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(OfferAPI.class.getName());

	@Test(testName = "To validate Offers API", description = "Offers API : to validate offers API upon valid set of inputs")
	@Parameters({"broker"})
	public void offerAPI_shouldReturn200(String broker) throws IOException {

		logger.info("Creating request for Offer API - start " + (broker));
		Response response = given().spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.offers);

		logger.info("Creating request for Offer API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
				.hasHeaderApplicationJSON()
				.isWithinAcceptedTimeLimit();
	}

	@Test(testName = "To validate schema for Offers API", description = "Offers API schema validation: to validate schema for offers API upon valid set of inputs")
	@Parameters({"broker"})
	public void checkSchema(String broker) throws IOException {

		logger.info("Creating request for Offer API - start " + (broker));
		Response response = given().spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.offers);

		logger.info("Creating request for Offer API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
				.hasValidSchema(IConst.OFFERS_SCHEMA);
	}
}
