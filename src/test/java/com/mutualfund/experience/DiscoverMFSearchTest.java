package com.mutualfund.experience;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.MFResource;
import commonutils.ConfigRead;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DiscoverMFSearchTest extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DiscoverMFSearchTest.class.getName());

    @Test(testName = "Discover MF by Search IDs", description = "Discover MF using search IDs in sequential order")
    @Parameters({"PhoneNo","Flow"})
    public void discoverMFBySearchIds(String PhoneNo , String Flow) {
        logger.info("Starting MF search by search ID method:");
        RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
        String[] mfIds = MFResource.mfIDs;
        for (int i = 0; i < mfIds.length; i++) {
            mfIds[i] = mfIds[i].trim();
        }

        RequestSpecification requestSpec = RestAssured.given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec());
        for (String mfId : mfIds) requestSpec.queryParam("searchIds[]", mfId.trim());

        Response response_searchID = requestSpec
                .when()
                .get(MFResource.mfDiscover)
                .then()
                .extract().response();

        ResponseAssert.assertThat(response_searchID)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .hasRequiredKeysInsideEveryArrayItems("mfId", "slug", "meta", "ratios", "labels", "transactionalInfo", "volatility", "platformData")
                .verifySequentialOrderOfIds(mfIds, "mfId");

        writeRequestAndResponseInReport(writer.toString(), response_searchID.prettyPrint(), Flow);
    }

    @Test(testName = "Discover MF by Search Text", description = "Discover MF using search text.")
    @Parameters({"PhoneNo","Flow"})
    public void discoverMFBySearchText(String PhoneNo , String Flow) {
        String[] searchTexts = MFResource.searchQueryTexts;
        for (int i = 0; i < searchTexts.length; i++) {
            searchTexts[i] = searchTexts[i].trim();
        }


        logger.info("Starting MF search by search text method:");

        for (String searchText : searchTexts) {
            resetCaptorAndWriter();
            int pageSize = (int) (Math.random() * 20) + 1;
            int pageNumber = (int) (Math.random() * 6) + 1;

            Response response_searchText = RestAssured.given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .queryParam("searchText", searchText.trim())
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNumber", pageNumber)
                    .when()
                    .get(MFResource.mfDiscover)
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(response_searchText)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .hasRequiredKeysInsideEveryArrayItems("mfId", "slug", "meta", "ratios", "labels", "transactionalInfo", "volatility", "platformData");

            writeRequestAndResponseInReport(writer.toString(), response_searchText.prettyPrint(), Flow);
        }
    }

    @Test(testName = "Exceeding MF-Ids Limit", description = "Discover MF using multiple search IDs exceeding the limit.")
    @Parameters({"PhoneNo","Flow"})
    public void discoverMFByMultipleSearchIdsExceedingLimit(String PhoneNo , String Flow) {
        logger.info("Starting MF search by multiple search IDs (Exceeding Limit) method:");

        String[] mfIds = MFResource.mfIDs;
        for (int i = 0; i < mfIds.length; i++) {
            mfIds[i] = mfIds[i].trim();
        }

        // Create an array of 21 MF IDs by duplicating the provided mfIDs
        String[] searchIds = new String[21];
        for (int i = 0; i < searchIds.length; i++) {
            searchIds[i] = mfIds[i % mfIds.length];
        }

        RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
        Response response_searchID = RestAssured.given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("searchIds", (Object[]) searchIds)
                .when()
                .get(MFResource.mfDiscover)
                .then()
                .extract().response();

        // Assert that the response returns a 400 Bad Request and contains the expected error message
        ResponseAssert.assertThat(response_searchID)
                .returns_400_BADREQUEST()
                .returnsValidErrorMessage("\"query.searchIds\" must contain less than or equal to 20 items");

        writeRequestAndResponseInReport(writer.toString(), response_searchID.prettyPrint(), Flow);
    }

    @Test(testName = "Maximum Values of PageSize and PageNumber", description = "Verify that the maximum value of pageSize is 20 and pageNumber is 6.")
    @Parameters({"PhoneNo","Flow"})
    public void verifyMaximumPageSizeAndPageNumber(String PhoneNo , String Flow) {
        logger.info("Starting test to verify maximum pageSize and pageNumber:");

        String[] searchTexts = MFResource.searchQueryTexts;
        for (int i = 0; i < searchTexts.length; i++) {
            searchTexts[i] = searchTexts[i].trim();
        }

        String searchTextFirst = searchTexts[0];

        int pageSize1 = 21;
        int pageNumber1 = 1;

        Response response1 = RestAssured.given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("searchText", searchTextFirst)
                .queryParam("pageSize", pageSize1)
                .queryParam("pageNumber", pageNumber1)
                .when()
                .get(MFResource.mfDiscover)
                .then()
                .extract().response();

        ResponseAssert.assertThat(response1)
                .returns_400_BADREQUEST()
                .returnsValidErrorMessage("\"query.pageSize\" must be less than or equal to 20");

        writeRequestAndResponseInReport(writer.toString(), response1.prettyPrint(), "");

        // Test for pageNumber exceeding the limit
        int pageSize2 = 1;
        int pageNumber2 = 7;

        Response response2 = RestAssured.given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("searchText", searchTextFirst)
                .queryParam("pageSize", pageSize2)
                .queryParam("pageNumber", pageNumber2)
                .when()
                .get(MFResource.mfDiscover)
                .then()
                .extract().response();

        ResponseAssert.assertThat(response2)
                .returns_400_BADREQUEST()
                .returnsValidErrorMessage("\"query.pageNumber\" must be less than or equal to 6");

        writeRequestAndResponseInReport(writer.toString(), response2.prettyPrint(), Flow);

    }

}
