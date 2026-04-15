package com.mutualfund.experience;

import com.asserts.ResponseAssert;
import com.CommonBaseTest;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcase.resource.MFResource;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class SearchApiMutualFundsTest extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SearchApiMutualFundsTest.class.getName());

    @Test(testName = "Search API - 'mutualfunds' field", description = "Verification for 'mutualfunds' field inside the response.")
    @Parameters({"PhoneNo","Flow"})
    public void verifyMutualFundsFieldInResponse(String PhoneNo, String Flow) {
        logger.info("Starting MutualFunds discovery via search API:");

        String[] searchQueries = MFResource.searchQueryTexts;

        for (String searchText : searchQueries) {
            searchText = searchText.trim();  // Trim the query here
            resetCaptorAndWriter();
            logger.info("Processing search query: " + searchText);

            Response response = RestAssured.given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .queryParam("text", searchText)
                    .when()
                    .get(SmallcaseResource.search)
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(response)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .hasMandatoryObjectsPresent("mutualfunds");

            writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), Flow);
        }
    }
}
