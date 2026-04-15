package com.multiassetsearch;

import com.asserts.ResponseAssert;
import com.CommonBaseTest;
import com.google.gson.Gson;
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

import java.util.*;

import static io.restassured.RestAssured.given;

public class StockConfigAPI extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(StockConfigAPI.class.getName());

    @Test(testName = "Stock config API", description = "Verify Stock config API for filterConfig and sortConfig lists", priority = 1)
    @Parameters({"Flow"})
    public void stockConfigAPI(String Flow) {
        logger.info("Stock config API started with Flow: " + Flow);
        Response stockConfigFilter = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configStock)
                .then()
                .extract().response();

        ResponseAssert.assertThat(stockConfigFilter)
                .returns_200_OK()
                .hasMandatoryObjectsPresent("quickFilters", "mandatoryFilters", "defaultSort", "sortConfig", "filterConfig");

        String jsonResponse = stockConfigFilter.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        Map<String, Object> stockFilters = multiAssetUtils.processFilterConfigs(jsonPath);
        Map<String, Object> stockSorts = multiAssetUtils.processSortConfigs(jsonPath);
        String stockFiltersJson = new Gson().toJson(stockFilters);
        DataToShare.setValue("stockFilters", stockFiltersJson);
        String stockSortsJson = new Gson().toJson(stockSorts);
        DataToShare.setValue("stockSorts", stockSortsJson);
        writeRequestAndResponseInReport(writer.toString(), stockConfigFilter.asString(), Flow);
        LogStatus.info("stockFilters - JSON");
        formatAPIAndLogInReport(stockFiltersJson);
        LogStatus.info("stockSorts - JSON");
        formatAPIAndLogInReport(stockSortsJson);
        List<Map<String, Object>> stockMandatoryFilters = jsonPath.getList("data.mandatoryFilters");
        String stockMandatoryFiltersJson = new Gson().toJson(stockMandatoryFilters);
        DataToShare.setValue("stockMandatoryFilters", stockMandatoryFiltersJson);
        LogStatus.info("stockMandatoryFilters - JSON");
        formatAPIAndLogInReport(stockMandatoryFiltersJson);
    }

    @Test(testName = "Verify Quick Filters for Stock", description = "Verify that quick filters contain valid optionIds for Stock", priority = 2)
    @Parameters({"Flow"})
    public void verifyQuickFilters(String Flow) {
        Response quickFiltersResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configStock)
                .then()
                .extract().response();

        String quickFiltersJson = quickFiltersResponse.asString();
        JsonPath quickFiltersPath = new JsonPath(quickFiltersJson);
        List<Map<String, Object>> quickFilters = quickFiltersPath.getList("data.quickFilters");

        String stockFiltersJson = (String) DataToShare.getValue("stockFilters");
        Map<String, Object> stockFilters = new Gson().fromJson(stockFiltersJson, Map.class);

        Map<String, Object> remapped_stockFilters = multiAssetUtils.remapFilterConfig(stockFilters);
        // formatAPIAndLogInReport(new Gson().toJson(remapped_stockFilters));
        Set<String> filterIds = remapped_stockFilters.keySet();
        String filterIdsJson = new Gson().toJson(filterIds);
        writeRequestAndResponseInReport(writer.toString(), quickFiltersResponse.asString(), Flow);
        LogStatus.info("Possible stockFilters keys are: " );
        formatAPIAndLogInReport(filterIdsJson);

        for (Map<String, Object> quickFilter : quickFilters) {
            String optionId = (String) quickFilter.get("optionId");
            if (!filterIds.contains(optionId)) {
                throw new AssertionError("Option ID: " + optionId + " not found in stockFilters keys.");
            } else {
                LogStatus.info("Quick filter optionId: " + optionId + " was found in stockFilters keys");
            }
        }
    }

    @Test(testName = "Verify Default Sort for Stock", description = "Verify that valid default sort is present in stockSorts", priority = 3)
    @Parameters({"Flow"})
    public void verifyDefaultSort(String Flow) {
        Response stockConfigResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configStock)
                .then()
                .extract().response();

        String jsonResponse = stockConfigResponse.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        String defaultSort = jsonPath.getString("data.defaultSort");

        String stockSortsJson = (String) DataToShare.getValue("stockSorts");
        Map<String, Object> stockSorts = new Gson().fromJson(stockSortsJson, Map.class);
        Map<String, Object> remapped_stockSorts = multiAssetUtils.remapFilterConfig(stockSorts);
        // formatAPIAndLogInReport(new Gson().toJson(remapped_stockSorts));
        Set<String> filterIds = remapped_stockSorts.keySet();
        String filterIdsJson = new Gson().toJson(filterIds);
        writeRequestAndResponseInReport(writer.toString(), stockConfigResponse.asString(), Flow);
        LogStatus.info("Possible stockSorts keys are: " );
        formatAPIAndLogInReport(filterIdsJson);

        if (!filterIds.contains(defaultSort)) {
            throw new AssertionError("Option ID: " + defaultSort + " not found in stockSorts keys.");
        } else {
            LogStatus.info("Default sort " + defaultSort + " was found in stockSorts keys");
        }
    }
}