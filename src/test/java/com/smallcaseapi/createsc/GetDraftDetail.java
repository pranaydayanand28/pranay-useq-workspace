package com.smallcaseapi.createsc;

import static io.restassured.RestAssured.given;
import com.asserts.ResponseAssert;
import com.smallcaseapi.BaseTest;
import commonutils.DataToShare;
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

public class GetDraftDetail extends BaseTest {

	static Response response;
	private static final Logger logger = LoggerFactory.getLogger(DraftSaveAPI.class.getName());

	@Test(testName = "To validate get draft API", description = "Get Draft API : to validate get draft API upon valid set of inputs")
	@Parameters({"broker"})
	@SneakyThrows
	public static void getDraftDetail_shouldReturn200(String broker){

		String did = (String) DataToShare.getValue("createdDID");

		response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam("did", did)
				.when()
				.get(SmallcaseResource.getDraftDetail);

		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
		logger.info("Test passed for "+ broker);
	}

	@Test(testName = "To validate get draft API with wrong draft ID", description = "Get Draft API : to validate get draft API upon invalid draft ID")
	@Parameters({"broker"})
	@SneakyThrows
	public static void getDraftDetailWithWrongDraftId_shouldReturn500(String broker){

		response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam("did", "abcdefgh12345678")
				.when()
				.get(SmallcaseResource.getDraftDetail);

		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		ResponseAssert.assertThat(response).returns_500_INTERNAL_SERVER_ERROR();
		logger.info("Test passed for "+ broker);
	}

	@Test(testName = "To validate get draft API schema", description = "Get Draft API schema validation : to validate schema for get draft API upon valid set of inputs")
	@Parameters({"broker"})
	@SneakyThrows
	public static void checkSchema(String broker){

		String did = (String) DataToShare.getValue("createdDID");

		response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.queryParam("did", did)
				.when()
				.get(SmallcaseResource.getDraftDetail);

		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.GET_DRAFT_SCHEMA);
		logger.info("Test passed for "+ broker);
	}

	@Test(testName = "To validate get draft API with no draft ID", description = "Get Draft API : to validate get draft API when no draft ID is given")
	@Parameters({"broker"})
	@SneakyThrows
	public static void getDraftDetail_shouldReturn400(String broker){

		response = given().filter(new RequestLoggingFilter(captor))
				.spec(RequestSpec.requestSpecification(broker))
				.when()
				.get(SmallcaseResource.getDraftDetail);

		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		ResponseAssert.assertThat(response).returns_400_BADREQUEST().returnsValidErrorMessage("\"did\" is required");
		logger.info("Test passed for "+ broker);
	}

}
