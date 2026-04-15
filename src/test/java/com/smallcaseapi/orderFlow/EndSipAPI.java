package com.smallcaseapi.orderFlow;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import com.smallcaseapi.BaseTest;
import commonutils.AssertActions;
import io.restassured.filter.log.RequestLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.payload.EndSipPayload;
import io.restassured.response.Response;

public class EndSipAPI extends BaseTest {

	static Response response;
	private static final Logger logger = LoggerFactory.getLogger(EndSipAPI.class.getName());

	@Test(priority = 4, testName = "To validate end SIP API", description = "End SIP : to validate end SIP API upon valid set of inputs")
	@Parameters({"broker"})
	public void endSIP(String broker) throws IOException, InterruptedException {

		logger.info("Creating request for END SIP API - start");

		response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.body(EndSipPayload.endSip())
				.when()
				.post(SmallcaseResource.endSip)
				.then()
				.extract()
				.response();

		logger.info("Creating request for END SIP API - end");
		logger.info("Writing Request and Response to Extent Report");

		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response");
		AssertActions.verifyStatusCode(response);
	}
}
