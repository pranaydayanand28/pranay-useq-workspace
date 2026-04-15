package com.smallcaseapi;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import com.asserts.ResponseAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.response.Response;

public class LogoutAPI extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(LogoutAPI.class);

	@AfterClass
	public void logoutAPI() throws IOException {

		Response response = given().spec(RequestSpec.requestSpecification()).when().post(SmallcaseResource.logout);

		ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
	}
}
