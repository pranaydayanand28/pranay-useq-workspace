package com.multiassetsearch;

import com.asserts.ResponseAssert;
import com.CommonBaseTest;
import com.smallcase.resource.SearchRequestSpec;
import com.smallcase.resource.SearchResources;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import resource.reports.LogStatus;

import java.util.List;

import static io.restassured.RestAssured.given;

public class SearchAPI extends CommonBaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(SearchAPI.class.getName());

    @Test(testName = "Search Asset API", description = "Verify Search Asset API")
    @Parameters({"Flow"})
    public void searchAsset(String Flow)
    {
        List<Object> popularKeywords = (List<Object>) DataToShare.getValue("popularKeyword");

        if (popularKeywords != null && !popularKeywords.isEmpty()) {
            for (int i = 0; i < popularKeywords.size(); i++) {
                String keyword = popularKeywords.get(i).toString();

                logger.info("Search Asset API started for keyword: " + keyword + " - Flow: " + Flow);

                Response searchAssetAPI = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParams("searchText", keyword)
                        .when()
                        .get(SearchResources.searchAsset)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(searchAssetAPI)
                        .returns_200_OK()
                        .hasRequiredKeysInsideEveryArrayItems("type");

                LogStatus.pass("SearchText = " + keyword);
                writeRequestAndResponseInReport(writer.toString(), searchAssetAPI.asString(), Flow);

                resetCaptorAndWriter();
            }
        } else {
            LogStatus.fail("No popular keywords found in DataToShare.");
        }
    }
}