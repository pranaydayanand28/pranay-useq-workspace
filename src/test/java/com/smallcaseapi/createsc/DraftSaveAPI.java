package com.smallcaseapi.createsc;

import static io.restassured.RestAssured.given;
import com.asserts.ResponseAssert;
import com.smallcaseapi.BaseTest;
import commonutils.*;
import io.restassured.filter.log.RequestLoggingFilter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.response.Response;

public class DraftSaveAPI extends BaseTest {

	static Response response;
	private static final Logger logger = LoggerFactory.getLogger(DraftSaveAPI.class.getName());

	@Test(testName = "To validate Add to DraftSaveAPI API", description = "DraftSaveAPI API : to validate add to DraftSaveAPI API upon valid set of inputs")
	@Parameters({"broker"})
	@SneakyThrows
	public void save(String broker){
		response = given().log().all().filter(new RequestLoggingFilter(captor)).spec(RequestSpec.requestSpecification(broker))
				.body(StockInfoAPI.stockInfo(broker)).when().post(SmallcaseResource.draftSave);

		writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

		String did = JsonPathFinder.getJsPath(response).get("data.did");
		DataToShare.setValue("createdDID", did);

		ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
		logger.info("Test passed for "+ broker);
	}

}
