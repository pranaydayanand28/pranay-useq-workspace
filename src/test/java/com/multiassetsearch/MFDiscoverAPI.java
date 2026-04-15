package com.multiassetsearch;

import com.asserts.ResponseAssert;
import com.CommonBaseTest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smallcase.resource.SearchRequestSpec;
import com.smallcase.resource.SearchResources;
import commonutils.DataToShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import resource.reports.LogStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.restassured.RestAssured.given;

public class MFDiscoverAPI extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MFDiscoverAPI.class.getName());

    @Test(testName = "Verify Mutual Fund Discover API without query params", description = "Verify the Mutual Fund Discover API without query params", priority = 1)
    @Parameters({"Flow"})
    public void verifyMutualFundDiscoverWithoutFilters(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        Response mutualFundDiscover = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.discoverMutualfund)
                .then()
                .extract().response();

        ResponseAssert.assertThat(mutualFundDiscover)
                .returns_200_OK()
                .hasMandatoryObjectsPresent("items", "pagination")
                .itemsArrayIsNotEmpty(testFailed);

        writeRequestAndResponseInReport(writer.toString(), mutualFundDiscover.asString(), Flow);

        if (!testFailed.get()) {
            Assert.fail("Above test failed due to empty items array.");
        }
    }

    @Test(testName = "Verify Mutual Fund Discover API with query params", description = "Verify the Mutual Fund Discover API with query params using popular keywords and random pagination", priority = 2)
    @Parameters({"Flow"})
    public void verifyMutualFundDiscoverWithQueryParams(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        List<Object> popularKeywords = (List<Object>) DataToShare.getValue("popularKeyword");
        if (popularKeywords != null && !popularKeywords.isEmpty()) {
            for (int i = 0; i < popularKeywords.size(); i++) {
                String searchText = popularKeywords.get(i).toString();
                int pageSize = 1 + (int) (Math.random() * 5);
                int pageNo = 1 + (int) (Math.random() * 3);

                Response mutualFundDiscover = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParam("pageSize", pageSize)
                        .queryParam("pageNo", pageNo)
                        .queryParam("searchText", searchText)
                        .when()
                        .get(SearchResources.discoverMutualfund)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(mutualFundDiscover)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("items", "pagination")
                        .itemsArrayIsNotEmpty(testFailed);

                LogStatus.info("Mutual Fund Discover API with searchText: " + searchText + ", pageNo: " + pageNo + ", pageSize: " + pageSize);
                writeRequestAndResponseInReport(writer.toString(), mutualFundDiscover.asString(), Flow);
                resetCaptorAndWriter();
            }

            if (!testFailed.get()) {
                Assert.fail("At least one iteration failed due to empty items array.");
            }
        }
    }

    @Test(testName = "Verify Mutual Fund Discover API with mutual fund filters", description = "Verify the Mutual Fund Discover API with mutual fund filters", priority = 3)
    @Parameters({"Flow"})
    public void verifyMutualFundDiscoverWithFilters(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        String mutualFundFiltersJson = (String) DataToShare.getValue("mfFilters");
        Map<String, Object> mutualFundFilters = new Gson().fromJson(mutualFundFiltersJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedFilters = multiAssetUtils.remapFilterConfig(mutualFundFilters);
        for (Map.Entry<String, Object> entry : remappedFilters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List<?> && !((List<?>) value).isEmpty()) {
                Object filterValue = ((List<?>) value).get(0);

                if (filterValue == null) continue;
                Response mutualFundDiscover = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParam("filters", filterValue)
                        .when()
                        .get(SearchResources.discoverMutualfund)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(mutualFundDiscover)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("items", "pagination")
                        .itemsArrayIsNotEmpty(testFailed);

                LogStatus.info("Filter key: " + key + " | Filter value: " + filterValue);
                writeRequestAndResponseInReport(writer.toString(), mutualFundDiscover.asString(), Flow);
            }
            resetCaptorAndWriter();
        }

        if (!testFailed.get()) {
            Assert.fail("At least one iteration failed due to empty items array.");
        }
    }

    @Test(testName = "Verify Mutual Fund Discover API with mutual fund sorts", description = "Verify the Mutual Fund Discover API with mutual fund sorts", priority = 4)
    @Parameters({"Flow"})
    public void verifyMutualFundDiscoverWithSorts(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        AtomicBoolean testFailed_sorted = new AtomicBoolean(true);
        String mutualFundSortsJson = (String) DataToShare.getValue("mfSorts");
        Map<String, Object> mutualFundSorts = new Gson().fromJson(mutualFundSortsJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedSorts = multiAssetUtils.remapFilterConfig(mutualFundSorts);
        for (Map.Entry<String, Object> entry : remappedSorts.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List<?> && !((List<?>) value).isEmpty()) {
                Object sortValue = ((List<?>) value).get(0);
                if (sortValue == null) continue;
                Response mutualFundDiscover = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParam("sort", sortValue)
                        .when()
                        .get(SearchResources.discoverMutualfund)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(mutualFundDiscover)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("items", "pagination")
                        .itemsArrayIsNotEmpty(testFailed)
                        .multiAsset_isSortedResponse((Map<String, Object>) sortValue, testFailed_sorted);

                LogStatus.info("Sort key: " + key + " | Sort value: " + sortValue);
                writeRequestAndResponseInReport(writer.toString(), mutualFundDiscover.asString(), Flow);
            }
            resetCaptorAndWriter();
        }

        if (!testFailed.get() || !testFailed_sorted.get()) {
            StringBuilder failMessage = new StringBuilder("Failed cases:");
            if (!testFailed.get()) {
                failMessage.append(" Items array is empty in one or more iteration.");
            }
            if (!testFailed_sorted.get()) {
                failMessage.append(" Items array is not sorted in one or more iteration.");
            }
            Assert.fail(failMessage.toString());
        }
    }

    @Test(testName = "Verify Mutual Fund Discover API with random pagination, multiple filters, and a sort filter, and captures mutualFundIncludeId and mutualFundExcludeId", description = "Verify the Mutual Fund Discover API with random pagination, multiple filters, and a sort filter, and captures mutualFundIncludeId and mutualFundExcludeId", priority = 5)
    @Parameters({"Flow"})
    public void verifyMutualFundDiscoverWithRandomParams(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        AtomicBoolean testFailed_sorted = new AtomicBoolean(true);
        String mutualFundFiltersJson = (String) DataToShare.getValue("mfFilters");
        Map<String, Object> mutualFundFilters = new Gson().fromJson(mutualFundFiltersJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedFilters = multiAssetUtils.remapFilterConfig(mutualFundFilters);
        String mutualFundSortsJson = (String) DataToShare.getValue("mfSorts");
        Map<String, Object> mutualFundSorts = new Gson().fromJson(mutualFundSortsJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedSorts = multiAssetUtils.remapFilterConfig(mutualFundSorts);
        List<String> allMutualFundIncludeIds = new ArrayList<>();
        List<String> allMutualFundExcludeIds = new ArrayList<>();
        for (int run = 0; run < 15; run++) {
            int pageSize = 1 + (int) (Math.random() * 5);
            int pageNo = 1 + (int) (Math.random() * 3);

            List<Object> selectedFilters = new ArrayList<>();
            if (!remappedFilters.isEmpty()) {
                int numFiltersToSelect = 1 + (int) (Math.random() * 3);
                List<String> filterKeys = new ArrayList<>(remappedFilters.keySet());
                Collections.shuffle(filterKeys);
                for (int i = 0; i < numFiltersToSelect && i < filterKeys.size(); i++) {
                    String randomKey = filterKeys.get(i);
                    List<?> filterList = (List<?>) remappedFilters.get(randomKey);
                    if (!filterList.isEmpty()) {
                        Object filterValue = filterList.get((int) (Math.random() * filterList.size()));
                        selectedFilters.add(filterValue);
                    }
                }
            }

            Object sortValue = null;
            if (!remappedSorts.isEmpty()) {
                String randomSortKey = (String) remappedSorts.keySet().toArray()[(int) (Math.random() * remappedSorts.size())];
                List<?> sortList = (List<?>) remappedSorts.get(randomSortKey);
                if (!sortList.isEmpty()) {
                    sortValue = sortList.get((int) (Math.random() * sortList.size()));
                }
            }

            StringBuilder selectedFilters_result = new StringBuilder();
            for (Object filter : selectedFilters)
                selectedFilters_result.append(filter.toString()).append(", ");

            if (selectedFilters.isEmpty() || sortValue == null) continue;

            Response mutualFundDiscover = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(SearchRequestSpec.searchSpec())
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNo", pageNo)
                    .queryParam("sort", sortValue)
                    .queryParam("filters", selectedFilters)
                    .when()
                    .get(SearchResources.discoverMutualfund)
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(mutualFundDiscover)
                    .returns_200_OK()
                    .hasMandatoryObjectsPresent("items", "pagination")
                    .itemsArrayIsNotEmpty(testFailed)
                    .multiAsset_isSortedResponse((Map<String, Object>) sortValue, testFailed_sorted);

            LogStatus.info(String.format("Run %d:, PageNo: %d, PageSize: %d, Filter: %s, Sort: %s", run + 1, pageNo, pageSize, selectedFilters_result, sortValue));
            writeRequestAndResponseInReport(writer.toString(), mutualFundDiscover.asString(), Flow);
            resetCaptorAndWriter();

            String jsonResponse = mutualFundDiscover.asString();
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<String> mfIncludeIds = jsonPath.getList("data.items.mfId");
            allMutualFundIncludeIds.addAll(mfIncludeIds);
            allMutualFundExcludeIds.addAll(mfIncludeIds);
        }

        List<String> uniqueMFIncludeIds = new ArrayList<>(new HashSet<>(allMutualFundIncludeIds));
        List<String> uniqueMFExcludeIds = new ArrayList<>(new HashSet<>(allMutualFundExcludeIds));
        if (uniqueMFIncludeIds.size() > 5) {
            uniqueMFIncludeIds = uniqueMFIncludeIds.subList(0, 5);
        }
        LogStatus.info("Mutual Fund Include IDs: " + uniqueMFIncludeIds);
        LogStatus.info("Mutual Fund Exclude IDs: " + uniqueMFExcludeIds);
        DataToShare.setValue("mfIncludeIds", uniqueMFIncludeIds);
        DataToShare.setValue("mfExcludeIds", uniqueMFExcludeIds);

        if (!testFailed.get() || !testFailed_sorted.get()) {
            StringBuilder failMessage = new StringBuilder("Failed cases:");
            if (!testFailed.get()) {
                failMessage.append(" Items array is empty in one or more iteration.");
            }
            if (!testFailed_sorted.get()) {
                failMessage.append(" Items array is not sorted in one or more iteration.");
            }
            Assert.fail(failMessage.toString());
        }
    }
}