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

public class SCIncludeExclude extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SCIncludeExclude.class.getName());
    @Test(testName = "Smallcase Discover API with ExcludeIds", description = "Verify Smallcase Discover API with ExcludeIds")
    @Parameters({"Flow"})
    public void smallcaseDiscoverAPIWithExcludeId(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        List<String> excludeIds = (List<String>) DataToShare.getValue("scExcludeIds");

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
                        .get(SearchResources.discoverSmallcase)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(paginationResponse)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("pagination", "items")
                        .itemsArrayIsNotEmpty(testFailed);

                List<String> itemSids = paginationResponse.jsonPath().getList("data.items.scid");

                for (String scid : excludeIds) {
                    if (itemSids.contains(scid)) {
                        excludeIdStatusMap.put(scid, 0); // Found
                    }
                }

                hasNextPage = paginationResponse.jsonPath().getBoolean("data.pagination.hasNext");
                currentPage++;
                resetCaptorAndWriter();
            }

            for (Map.Entry<String, Integer> entry : excludeIdStatusMap.entrySet()) {
                String status = (entry.getValue() == 1) ? "Not Found" : "Found";
                LogStatus.info("Exclude Smallcase-ID: " + entry.getKey() + " - Status: " + status);
            }

            List<String> foundIds = excludeIdStatusMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!foundIds.isEmpty()) {
                String foundIdsString = String.join(", ", foundIds);
                LogStatus.fail("The following excluded IDs were found despite being excluded: " + foundIdsString);
            }else{
                LogStatus.pass("All Ids passed the test.");
            }

        } else {
            Assert.fail("No exclude IDs found to apply in the Smallcase Discover API request.");
        }
    }

    @Test(testName = "Smallcase Discover API with IncludeIds", description = "Verify Smallcase Discover API with IncludeIds")
    @Parameters({"Flow"})
    public void smallcaseDiscoverAPIWithIncludeId(String Flow) {
        List<String> includeIds = (List<String>) DataToShare.getValue("scIncludeIds");
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
                        .get(SearchResources.discoverSmallcase)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(paginationResponse)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("pagination", "items")
                        .itemsArrayIsNotEmpty(testFailed);

                List<String> itemSids = paginationResponse.jsonPath().getList("data.items.scid");

                for (String scid : includeIds) {
                    if (itemSids.contains(scid)) {
                        includeIdStatusMap.put(scid, 0);
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
                LogStatus.info("Include Smallcase-ID: " + entry.getKey() + " - Status: " + status);
            }

            List<String> notFoundIds = includeIdStatusMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!notFoundIds.isEmpty()) {
                String notFoundIdsString = String.join(", ", notFoundIds);
                LogStatus.fail("The following included IDs were not found despite being included: " + notFoundIdsString);
            }else{
                LogStatus.pass("All Ids passed the test.");
            }

        } else {
            Assert.fail("No include IDs found to apply in the Smallcase Discover API request.");
        }
    }

    @Test(testName = "Smallcase Discover API with Invalid IncludeId", description = "Verify Smallcase Discover API with Invalid IncludeId")
    @Parameters({"Flow"})
    public void smallcaseDiscoverAPIWithInvalidIncludeId(String Flow) {
        String invalidIncludeId = "invalid_id_case";
        Response paginationResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .queryParams("includeIds", invalidIncludeId)
                .get(SearchResources.discoverSmallcase)
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