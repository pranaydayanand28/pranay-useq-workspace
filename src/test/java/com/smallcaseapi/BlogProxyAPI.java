package com.smallcaseapi;

import static io.restassured.RestAssured.given;
import com.asserts.ResponseAssert;
import com.smallcase.resource.enums.QueryParameters;
import com.smallcase.resource.ApiConstants;
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

public class BlogProxyAPI extends BaseTest {

	private static final Logger logger = LoggerFactory.getLogger(BlogProxyAPI.class);

	@Test(testName = "To validate Blog proxy API", description = "Blog Proxy API : to validate blog proxy API upon valid set of inputs")
	@Parameters({"broker"})
	@SneakyThrows
	public void blogProxyAPI_shouldReturn200(String broker){

		logger.info("Creating request for blogProxyAPI API - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam(QueryParameters.type.name(), QueryParameters.getTypeForBlogProxy())
				.when()
				.get(SmallcaseResource.blogProxy);

		logger.info("Checking schema for blogProxyAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
				.hasHeaderApplicationJSON()
				.isWithinAcceptedTimeLimit();

		logger.info("Test passed for " + broker);
	}

	@Test(enabled = false, testName = "To validate schema for Blog Proxy API", description = "Blog Proxy API schema validation : to validate schema for blogProxy API")
	@Parameters({"broker"})
	@SneakyThrows
	public void checkSchema(String broker){

		logger.info("Checking schema for blogProxyAPI - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam(QueryParameters.type.name(), QueryParameters.getTypeForBlogProxy())
				.when()
				.get(SmallcaseResource.blogProxy);

		logger.info("Checking schema for blogProxyAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
				.hasValidSchema(IConst.BLOG_PROXY_SCHEMA);

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate Blog proxy API when user is not logged in", description = "Blog Proxy API : to validate blog proxy API when the user is not logged in")
	@Parameters({"broker"})
	public void blogProxyAPI_WITHOUTLOGIN_shouldReturn200(String broker){

		logger.info("Creating request for blogProxyAPI API without login - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
				.queryParam(QueryParameters.type.name(), QueryParameters.getTypeForBlogProxy())
				.when()
				.get(SmallcaseResource.blogProxy);

		logger.info("Creating request for blogProxyAPI API without login - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_200_OK()
				.hasHeaderApplicationJSON()
				.isWithinAcceptedTimeLimit();

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate Blog proxy API when no slug/ type is given in query param", description = "Blog Proxy API : to validate blog proxy API upon no slug/ type in query params")
	@Parameters({"broker"})
	@SneakyThrows
	public void blogProxyAPI_shouldReturn400(String broker){

		logger.info("Creating request for blogProxyAPI API - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.when()
				.get(SmallcaseResource.blogProxy);

		logger.info("Checking schema for blogProxyAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_400_BADREQUEST()
				.returnsValidErrorMessage(ApiConstants.BlogProxyAPI_SLUG_ERROR);

		logger.info("Test passed for " + broker);
	}

	@Test(testName = "To validate Blog proxy API when invalid slug/ type is given in query param", description = "Blog Proxy API : to validate blog proxy API when invalid slug/ type in given in query params")
	@Parameters({"broker"})
	@SneakyThrows
	public void blogProxyAPI_shouldReturn404(String broker){

		logger.info("Creating request for blogProxyAPI API - start " + (broker));
		Response response = given()
				.filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam(QueryParameters.type.name(), "Invalid Value")
				.when()
				.get(SmallcaseResource.blogProxy);

		logger.info("Checking schema for blogProxyAPI API - end " + (broker));
		logger.info("Writing Request and Response to Extent Report " + (broker));
		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		logger.info("Asserting response " + (broker));
		ResponseAssert.assertThat(response)
				.returns_404_NOTFOUND();

		logger.info("Test passed for " + broker);
	}
}
