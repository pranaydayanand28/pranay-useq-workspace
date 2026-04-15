package com.multiassetsearch;

import com.asserts.ResponseAssert;
import com.CommonBaseTest;
import com.smallcase.resource.SearchRequestSpec;
import com.smallcase.resource.SearchResources;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import resource.reports.LogStatus;

import java.util.List;

import static io.restassured.RestAssured.given;

public class TrendingAPI  extends CommonBaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(TrendingAPI.class.getName());

    @Test(testName = "Trending API", description = "Verify Trending API")
    @Parameters({"Flow"})
    public void trendingAPI(String Flow)
    {
        logger.info("Trending API started " + Flow);
        Response trendingAPI = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.trendingAsset)
                .then()
                .extract().response();

        ResponseAssert.assertThat(trendingAPI)
                .returns_200_OK()
                .hasRequiredKeysInsideEveryArrayItems("id", "searchResultCategory", "popularKeyword");

        String jsonResponse = trendingAPI.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        List<Object> arrPopular = jsonPath.getList("data.popularKeyword");
        DataToShare.setValue("popularKeyword", arrPopular);
        writeRequestAndResponseInReport(writer.toString(), jsonResponse, Flow);
        String popularKeywords = "";
        for (int i = 0; i < arrPopular.size(); i++) popularKeywords += arrPopular.get(i).toString() + ", ";
        LogStatus.info("popularKeywords: " + popularKeywords);
    }
}