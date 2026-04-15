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
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import resource.reports.LogStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class MFIncludeExclude extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MFIncludeExclude.class.getName());
    @Test(testName = "Mutual Fund Discover API with ExcludeIds", description = "Verify Mutual Fund Discover API with ExcludeIds")
    @Parameters({"Flow"})
    public void mutualFundDiscoverAPIWithExcludeId(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        List<String> excludeIds = (List<String>) DataToShare.getValue("mfExcludeIds");

        if (excludeIds != null && !excludeIds.isEmpty()) {
            Map<String, Integer> excludeIdStatusMap = new HashMap<>();
            for (String id : excludeIds) excludeIdStatusMap.put(id, 1); // 1 means not found

            int currentPage = 1;
            boolean hasNextPage = true;

            while (hasNextPage) {
                Response paginationResponse = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .when()
                        .queryParams("excludeIds", excludeIds, "pageNo", currentPage, "pageSize", 20)
                        .get(SearchResources.discoverMutualfund)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(paginationResponse)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("pagination", "items")
                        .itemsArrayIsNotEmpty(testFailed);

                List<String> itemSids = paginationResponse.jsonPath().getList("data.items.mfId");

                for (String mf_id : excludeIds) {
                    if (itemSids.contains(mf_id)) {
                        excludeIdStatusMap.put(mf_id, 0);
                    }
                }

                hasNextPage = paginationResponse.jsonPath().getBoolean("data.pagination.hasNext");
                currentPage++;
                resetCaptorAndWriter();
            }

            for (Map.Entry<String, Integer> entry : excludeIdStatusMap.entrySet()) {
                String status = (entry.getValue() == 1) ? "Not Found" : "Found";
                LogStatus.info("Exclude MF-ID: " + entry.getKey() + " - Status: " + status);
            }

            List<String> foundIds = excludeIdStatusMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!foundIds.isEmpty()) {
                String foundIdsString = String.join(", ", foundIds);
                LogStatus.fail("The following excluded MF IDs were found despite being excluded: " + foundIdsString);
            }else{
                LogStatus.pass("All Ids passed the test.");
            }

        } else {
            Assert.fail("No exclude MF IDs found to apply in the MF Discover API request.");
        }
    }

    @Test(testName = "Mutual Fund Discover API with IncludeIds", description = "Verify Mutual Fund Discover API with IncludeIds")
    @Parameters({"Flow"})
    public void mutualFundDiscoverAPIWithIncludeId(String Flow) {
        List<String> includeIds = (List<String>) DataToShare.getValue("mfIncludeIds");
        AtomicBoolean testFailed = new AtomicBoolean(true);
        if (includeIds != null && !includeIds.isEmpty()) {
            Map<String, Integer> includeIdStatusMap = new HashMap<>();
            for (String id : includeIds) includeIdStatusMap.put(id, 1); // 1 means not found
            int currentPage = 1;
            boolean hasNextPage = true;
            while (hasNextPage) {
                Response paginationResponse = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .when()
                        .queryParams("includeIds", includeIds, "pageNo", currentPage, "pageSize", 20)
                        .get(SearchResources.discoverMutualfund)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(paginationResponse)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("pagination", "items")
                        .itemsArrayIsNotEmpty(testFailed);

                List<String> itemSids = paginationResponse.jsonPath().getList("data.items.mfId");

                for (String mf_id : includeIds) {
                    if (itemSids.contains(mf_id)) {
                        includeIdStatusMap.put(mf_id, 0);
                    }
                }

                boolean allFound = includeIdStatusMap.values().stream().allMatch(status -> status == 0);
                if (allFound) { // Early break will always happen in 1st run only, as per the API contract
                    writeRequestAndResponseInReport(writer.toString(), paginationResponse.asString(), Flow);
                    break;
                }
                hasNextPage = paginationResponse.jsonPath().getBoolean("data.pagination.hasNext");
                currentPage++;
                resetCaptorAndWriter();
            }

            for (Map.Entry<String, Integer> entry : includeIdStatusMap.entrySet()) {
                String status = (entry.getValue() == 1) ? "Not Found" : "Found";
                LogStatus.info("Include MF-ID: " + entry.getKey() + " - Status: " + status);
            }

            List<String> notFoundIds = includeIdStatusMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!notFoundIds.isEmpty()) {
                String notFoundIdsString = String.join(", ", notFoundIds);
                LogStatus.fail("The following included MF IDs were not found despite being included: " + notFoundIdsString);
            }else{
                LogStatus.pass("All Ids passed the test.");
            }

        } else {
            Assert.fail("No include MF IDs found to apply in the MF Discover API request.");
        }
    }

    @Test(testName = "Mutual Fund Discover API with Invalid IncludeId", description = "Verify Mutual Fund Discover API with Invalid IncludeId")
    @Parameters({"Flow"})
    public void mutualFundDiscoverAPIWithInvalidIncludeId(String Flow) {
        String invalidIncludeId = "invalid_id_case";
        Response paginationResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .queryParams("includeIds", invalidIncludeId)
                .get(SearchResources.discoverMutualfund)
                .then()
                .extract().response();

        ResponseAssert.assertThat(paginationResponse)
                .returns_200_OK()
                .hasMandatoryObjectsPresent("pagination", "items");

        List<String> itemSids = paginationResponse.jsonPath().getList("data.items");
        writeRequestAndResponseInReport(writer.toString(), paginationResponse.asString(), Flow);

        if (itemSids.isEmpty()) {
            LogStatus.pass("Items array is empty for invalid include ID: " + invalidIncludeId);
        } else {
            Assert.fail("Items array is not empty for invalid include ID: " + invalidIncludeId);
        }
    }
}