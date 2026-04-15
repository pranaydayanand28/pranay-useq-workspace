package com.smallcaseapi.createsc;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class GetDrafts extends BaseTest {

    static Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetDrafts.class.getName());

    @Test(testName = "To validate get drafts API", description = "Get Drafts API : to validate get drafts API upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public static void getDraftDetail_shouldReturn200(String broker){

        response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getDrafts);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
        logger.info("Test passed for "+ broker);
    }

    @Test(testName = "To validate get drafts API schema", description = "Get Draft API schema validation : to validate schema for get drafts API upon valid set of inputs")
    @Parameters({"broker"})
    @SneakyThrows
    public static void checkSchema(String broker){

        response = given().filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getDrafts);

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.GET_DRAFTS_SCHEMA);
        logger.info("Test passed for "+ broker);
    }
}
