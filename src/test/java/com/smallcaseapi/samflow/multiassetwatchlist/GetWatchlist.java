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

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetWatchlist extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(GetWatchlist.class.getName());


    @Test(testName = "Get Watchlist", description = "Get Watchlist v2")
    @Parameters({"PhoneNo", "Flow"})
    public void getWatchlistV2(String PhoneNo, String Flow) {

        Map<String, Object> queryParams1 = new HashMap<>();
        queryParams1.put("projections[0]", "smallcase");
        queryParams1.put("projections[1]", "mutualFund");
        queryParams1.put("projections[2]", "stock");

        logger.info("Get Watchlist" + PhoneNo);
        Response GetWatchlist = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParams(queryParams1).log().all()
                .when()
                .get(SmallcaseResource.GetWatchlistV2)
                .then()
                .log().all().extract().response();

        System.out.println("Watchlist Request Body = "+GetWatchlist.asString());

        ResponseAssert.assertThat(GetWatchlist)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .hasMandatoryObjectsPresent("smallcase")
                .hasMandatoryObjectsPresent("mutualFund")
                .hasMandatoryObjectsPresent("stock");

        writeRequestAndResponseInReport(writer.toString(), GetWatchlist.prettyPrint(), Flow);
    }
}
