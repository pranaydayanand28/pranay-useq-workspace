package com.smallcaseapi;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import com.asserts.ResponseAssert;
import io.restassured.filter.log.RequestLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.response.Response;

public class NotificationAPI extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(NotificationAPI.class.getName());

	@Test(testName = "To validate Notifications API", description = "Notifications API : to validate Notifications API upon valid set of inputs")
	@Parameters({"broker"})
	public void notifacationAPI(String broker) throws IOException {

		logger.info("Creating request for Notification API - start " + (broker));
		Response response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker)).when().get(SmallcaseResource.notification);

		logger.info("Creating request for Notification API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
	}

}
