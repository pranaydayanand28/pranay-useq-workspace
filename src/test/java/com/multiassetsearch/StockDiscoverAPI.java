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

import static io.restassured.RestAssured.given;
import java.util.concurrent.atomic.AtomicBoolean;

public class StockDiscoverAPI extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(StockDiscoverAPI.class.getName());

    @Test(testName = "Verify Stock Discover API without query params", description = "Verify the Stock Discover API without query params", priority = 1)
    @Parameters({"Flow"})
    public void verifyStockDiscoverWithoutFilters(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        Response stockDiscover = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.discoverStock)
                .then()
                .extract().response();

        ResponseAssert.assertThat(stockDiscover)
                .returns_200_OK()
                .hasMandatoryObjectsPresent("items", "pagination")
                .itemsArrayIsNotEmpty(testFailed);

        writeRequestAndResponseInReport(writer.toString(), stockDiscover.asString(), Flow);

        if (!testFailed.get()) {
            Assert.fail("Above test failed due to empty items array.");
        }
    }

    @Test(testName = "Verify Stock Discover API with query params", description = "Verify the Stock Discover API with query params using popular keywords and random pagination", priority = 2)
    @Parameters({"Flow"})
    public void verifyStockDiscoverWithQueryParams(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        List<Object> popularKeywords = (List<Object>) DataToShare.getValue("popularKeyword");
        if (popularKeywords != null && !popularKeywords.isEmpty()) {
            for (int i = 0; i < popularKeywords.size(); i++) {
                String searchText = popularKeywords.get(i).toString();
                int pageSize = 1 + (int) (Math.random() * 5);
                int pageNo = 1 + (int) (Math.random() * 3);

                Response stockDiscover = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParam("pageSize", pageSize)
                        .queryParam("pageNo", pageNo)
                        .queryParam("searchText", searchText)
                        .when()
                        .get(SearchResources.discoverStock)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(stockDiscover)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("items", "pagination")
                        .itemsArrayIsNotEmpty(testFailed);

                LogStatus.info("Stock Discover API with searchText: " + searchText + ", pageNo: " + pageNo + ", pageSize: " + pageSize );
                writeRequestAndResponseInReport(writer.toString(), stockDiscover.asString(), Flow);
                resetCaptorAndWriter();
            }

            if (!testFailed.get()) {
                Assert.fail("At least one iteration failed due to empty items array.");
            }
        }
    }

    @Test(testName = "Verify Stock Discover API with stock filters", description = "Verify the Stock Discover API with stock filters", priority = 3)
    @Parameters({"Flow"})
    public void verifyStockDiscoverWithStockFilters(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        String stockFiltersJson = (String) DataToShare.getValue("stockFilters");
        Map<String, Object> stockFilters = new Gson().fromJson(stockFiltersJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedFilters = multiAssetUtils.remapFilterConfig(stockFilters);
        for (Map.Entry<String, Object> entry : remappedFilters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List<?> && !((List<?>) value).isEmpty()) {
                Object filterValue = ((List<?>) value).get(0);

                if (filterValue == null) continue;
                Response stockDiscover = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParam("filters", filterValue)
                        .when()
                        .get(SearchResources.discoverStock)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(stockDiscover)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("items", "pagination")
                        .itemsArrayIsNotEmpty(testFailed);

                LogStatus.info("Filter key: " + key + " | Filter value: " + filterValue );
                writeRequestAndResponseInReport(writer.toString(), stockDiscover.asString(), Flow);
            }
            resetCaptorAndWriter();
        }

        if (!testFailed.get()) {
            Assert.fail("At least one iteration failed due to empty items array.");
        }
    }

    @Test(testName = "Verify Stock Discover API with stock sorts", description = "Verify the Stock Discover API with stock sorts", priority = 4)
    @Parameters({"Flow"})
    public void verifyStockDiscoverWithStockSorts(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        AtomicBoolean testFailed_sorted = new AtomicBoolean(true);
        String stockSortsJson = (String) DataToShare.getValue("stockSorts");
        Map<String, Object> stockSorts = new Gson().fromJson(stockSortsJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedSorts = multiAssetUtils.remapFilterConfig(stockSorts);
        for (Map.Entry<String, Object> entry : remappedSorts.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof List<?> && !((List<?>) value).isEmpty()) {
                Object sortValue = ((List<?>) value).get(0);
                if (sortValue == null) continue;
                Response stockDiscover = given()
                        .filter(new RequestLoggingFilter(captor))
                        .spec(SearchRequestSpec.searchSpec())
                        .queryParam("sort", sortValue)
                        .when()
                        .get(SearchResources.discoverStock)
                        .then()
                        .extract().response();

                ResponseAssert.assertThat(stockDiscover)
                        .returns_200_OK()
                        .hasMandatoryObjectsPresent("items", "pagination")
                        .itemsArrayIsNotEmpty(testFailed)
                        .multiAsset_isSortedResponse((Map<String, Object>) sortValue, testFailed_sorted);

                LogStatus.info("Sort key: " + key + " | Sort value: " + sortValue);
                writeRequestAndResponseInReport(writer.toString(), stockDiscover.asString(), Flow);
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

    @Test(testName = "Verify Stock Discover API with random pagination, multiple filters, and a sort filter, and captures stockIncludeId and stockExcludeId", description = "Verify Stock Discover API with random pagination, multiple filters, and a sort filter, and captures stockIncludeId and stockExcludeId", priority = 5)
    @Parameters({"Flow"})
    public void verifyStockDiscoverWithRandomParams(String Flow) {
        AtomicBoolean testFailed = new AtomicBoolean(true);
        AtomicBoolean testFailed_sorted = new AtomicBoolean(true);
        String stockFiltersJson = (String) DataToShare.getValue("stockFilters");
        Map<String, Object> stockFilters = new Gson().fromJson(stockFiltersJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedFilters = multiAssetUtils.remapFilterConfig(stockFilters);
        String stockSortsJson = (String) DataToShare.getValue("stockSorts");
        Map<String, Object> stockSorts = new Gson().fromJson(stockSortsJson, new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Object> remappedSorts = multiAssetUtils.remapFilterConfig(stockSorts);
        List<String> allStockIncludeIds = new ArrayList<>();
        List<String> allStockExcludeIds = new ArrayList<>();
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

            Response stockDiscover = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(SearchRequestSpec.searchSpec())
                    .queryParam("pageSize", pageSize)
                    .queryParam("pageNo", pageNo)
                    .queryParam("filters", selectedFilters)
                    .queryParam("sort", sortValue)
                    .when()
                    .get(SearchResources.discoverStock)
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(stockDiscover)
                    .returns_200_OK()
                    .hasMandatoryObjectsPresent("items", "pagination")
                    .itemsArrayIsNotEmpty(testFailed)
                    .multiAsset_isSortedResponse((Map<String, Object>) sortValue, testFailed_sorted);

            LogStatus.info(String.format("Run %d:, PageNo: %d, PageSize: %d, Filter: %s, Sort: %s", run + 1, pageNo, pageSize, selectedFilters_result, sortValue));
            writeRequestAndResponseInReport(writer.toString(), stockDiscover.asString(), Flow);
            resetCaptorAndWriter();

            String jsonResponse = stockDiscover.asString();
            JsonPath jsonPath = new JsonPath(jsonResponse);
            List<String> stockIncludeIds = jsonPath.getList("data.items.sid");
            allStockIncludeIds.addAll(stockIncludeIds);
            allStockExcludeIds.addAll(stockIncludeIds);
        }

        List<String> uniqueStockIncludeIds = new ArrayList<>(new HashSet<>(allStockIncludeIds));
        List<String> uniqueStockExcludeIds = new ArrayList<>(new HashSet<>(allStockExcludeIds));
        if (uniqueStockIncludeIds.size() > 5) {
            uniqueStockIncludeIds = uniqueStockIncludeIds.subList(0, 5);
        }
        DataToShare.setValue("stockIncludeIds", uniqueStockIncludeIds);
        DataToShare.setValue("stockExcludeIds", uniqueStockExcludeIds);
        LogStatus.info("Captured stockInclude Ids: " + String.join(", ", uniqueStockIncludeIds));
        LogStatus.info("Captured stockExclude Ids: " + String.join(", ", uniqueStockExcludeIds));

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