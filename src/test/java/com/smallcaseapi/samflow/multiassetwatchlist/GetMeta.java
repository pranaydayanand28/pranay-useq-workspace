package com.smallcaseapi.samflow.multiassetwatchlist;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetMeta extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(GetMeta.class.getName());
    @Test(testName = "Get Meta", description = "Get Meta")
    @Parameters({"PhoneNo", "Flow"})

    public  void GetMeta(String PhoneNo, String Flow)
    {
        logger.info("Get Meta" + PhoneNo);
        Response GetMeta = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .when()
                .get(SmallcaseResource.GetMeta)
                .then()
                .extract().response();

        ResponseAssert.assertThat(GetMeta)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .hasMandatoryObjectsPresent("watchlistCount")
                .hasMandatoryObjectsPresent("watchlistAssetIds")
                .hasMandatoryObjectsPresent("investedAssetIds");

        writeRequestAndResponseInReport(writer.toString(), GetMeta.prettyPrint(),Flow);

    }
}
