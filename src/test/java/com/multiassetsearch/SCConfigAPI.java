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

public class SCConfigAPI extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SCConfigAPI.class.getName());

    @Test(testName = "Smallcase config API", description = "Verify Smallcase config API for filterConfig and sortConfig lists", priority = 1)
    @Parameters({"Flow"})
    public void smallcaseConfigAPI(String Flow) {
        logger.info("Smallcase config API started with Flow: " + Flow);
        Response smallcaseConfigFilter = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configSmallcase)
                .then()
                .extract().response();

        ResponseAssert.assertThat(smallcaseConfigFilter)
                .returns_200_OK()
                .hasMandatoryObjectsPresent("quickFilters", "mandatoryFilters", "defaultSort", "sortConfig", "filterConfig");

        String jsonResponse = smallcaseConfigFilter.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        Map<String, Object> smallcaseFilters = multiAssetUtils.processFilterConfigs(jsonPath);
        Map<String, Object> smallcaseSorts = multiAssetUtils.processSortConfigs(jsonPath);
        String smallcaseFiltersJson = new Gson().toJson(smallcaseFilters);
        DataToShare.setValue("smallcaseFilters", smallcaseFiltersJson);
        String smallcaseSortsJson = new Gson().toJson(smallcaseSorts);
        DataToShare.setValue("smallcaseSorts", smallcaseSortsJson);
        writeRequestAndResponseInReport(writer.toString(), smallcaseConfigFilter.asString(), Flow);
        LogStatus.info("smallcaseFilters - JSON");
        formatAPIAndLogInReport(smallcaseFiltersJson);
        LogStatus.info("smallcaseSorts - JSON");
        formatAPIAndLogInReport(smallcaseSortsJson);
        List<Map<String, Object>> smallcaseMandatoryFilters = jsonPath.getList("data.mandatoryFilters");
        String smallcaseMandatoryFiltersJson = new Gson().toJson(smallcaseMandatoryFilters);
        DataToShare.setValue("smallcaseMandatoryFilters", smallcaseMandatoryFiltersJson);
        LogStatus.info("smallcaseMandatoryFilters - JSON");
        formatAPIAndLogInReport(smallcaseMandatoryFiltersJson);
    }

    @Test(testName = "Verify Quick Filters for Smallcase", description = "Verify that quick filters contain valid optionIds for Smallcase", priority = 2)
    @Parameters({"Flow"})
    public void verifyQuickFiltersForSmallcase(String Flow) {
        Response quickFiltersResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configSmallcase)
                .then()
                .extract().response();

        String quickFiltersJson = quickFiltersResponse.asString();
        JsonPath quickFiltersPath = new JsonPath(quickFiltersJson);
        List<Map<String, Object>> quickFilters = quickFiltersPath.getList("data.quickFilters");

        String smallcaseFiltersJson = (String) DataToShare.getValue("smallcaseFilters");
        Map<String, Object> smallcaseFilters = new Gson().fromJson(smallcaseFiltersJson, Map.class);

        Map<String, Object> remapped_SmallcaseFilters = multiAssetUtils.remapFilterConfig(smallcaseFilters);
        // formatAPIAndLogInReport(new Gson().toJson(remapped_SmallcaseFilters));
        Set<String> filterIds = remapped_SmallcaseFilters.keySet();
        String filterIdsJson = new Gson().toJson(filterIds);
        writeRequestAndResponseInReport(writer.toString(), quickFiltersResponse.asString(), Flow);
        LogStatus.info("Possible smallcaseFilters keys are: ");
        formatAPIAndLogInReport(filterIdsJson);

        for (Map<String, Object> quickFilter : quickFilters) {
            String optionId = (String) quickFilter.get("optionId");
            if (!filterIds.contains(optionId)) {
                throw new AssertionError("Option ID: " + optionId + " not found in smallcaseFilters keys.");
            } else {
                LogStatus.info("Quick filter optionId: " + optionId + " was found in smallcaseFilters keys");
            }
        }
    }

    @Test(testName = "Verify Default Sort for Smallcase", description = "Verify that valid default sort is present in smallcaseSorts", priority = 3)
    @Parameters({"Flow"})
    public void verifyDefaultSortForSmallcase(String Flow) {
        Response smallcaseConfigResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configSmallcase)
                .then()
                .extract().response();

        String jsonResponse = smallcaseConfigResponse.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        String defaultSort = jsonPath.getString("data.defaultSort");

        String smallcaseSortsJson = (String) DataToShare.getValue("smallcaseSorts");
        Map<String, Object> smallcaseSorts = new Gson().fromJson(smallcaseSortsJson, Map.class);
        Map<String, Object> remappedSmallcaseSorts = multiAssetUtils.remapFilterConfig(smallcaseSorts);
        // formatAPIAndLogInReport(new Gson().toJson(remappedSmallcaseSorts));
        Set<String> sortIds = remappedSmallcaseSorts.keySet();
        String sortIdsJson = new Gson().toJson(sortIds);
        writeRequestAndResponseInReport(writer.toString(), smallcaseConfigResponse.asString(), Flow);
        LogStatus.info("Possible smallcaseSorts keys are: ");
        formatAPIAndLogInReport(sortIdsJson);

        if (!sortIds.contains(defaultSort)) {
            throw new AssertionError("Default sort option ID: " + defaultSort + " not found in smallcaseSorts keys.");
        } else {
            LogStatus.info("Default sort " + defaultSort + " was found in smallcaseSorts keys");
        }
    }
}