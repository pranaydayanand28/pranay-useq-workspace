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

public class MFConfigAPI extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(MFConfigAPI.class.getName());

    @Test(testName = "MF config API", description = "Verify MF config API for filterConfig and sortConfig lists", priority = 1)
    @Parameters({"Flow"})
    public void mfConfigAPI(String Flow) {
        logger.info("MF config API started with Flow: " + Flow);
        Response mfConfigFilter = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configMutualfund)
                .then()
                .extract().response();

        ResponseAssert.assertThat(mfConfigFilter)
                .returns_200_OK()
                .hasMandatoryObjectsPresent("quickFilters", "mandatoryFilters", "defaultSort", "sortConfig", "filterConfig");

        String jsonResponse = mfConfigFilter.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        Map<String, Object> mfFilters = multiAssetUtils.processFilterConfigs(jsonPath);
        Map<String, Object> mfSorts = multiAssetUtils.processSortConfigs(jsonPath);
        String mfFiltersJson = new Gson().toJson(mfFilters);
        DataToShare.setValue("mfFilters", mfFiltersJson);
        String mfSortsJson = new Gson().toJson(mfSorts);
        DataToShare.setValue("mfSorts", mfSortsJson);
        writeRequestAndResponseInReport(writer.toString(), mfConfigFilter.asString(), Flow);
        LogStatus.info("mfFilters - JSON");
        formatAPIAndLogInReport(mfFiltersJson);
        LogStatus.info("mfSorts - JSON");
        formatAPIAndLogInReport(mfSortsJson);
        List<Map<String, Object>> mfMandatoryFilters = jsonPath.getList("data.mandatoryFilters");
        String mfMandatoryFiltersJson = new Gson().toJson(mfMandatoryFilters);
        DataToShare.setValue("mfMandatoryFilters", mfMandatoryFiltersJson);
        LogStatus.info("mfMandatoryFilters - JSON");
        formatAPIAndLogInReport(mfMandatoryFiltersJson);
    }

    @Test(testName = "Verify Quick Filters for MF", description = "Verify that quick filters contain valid optionIds for MF", priority = 2)
    @Parameters({"Flow"})
    public void verifyQuickFiltersForMF(String Flow) {
        Response quickFiltersResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configMutualfund)
                .then()
                .extract().response();

        String quickFiltersJson = quickFiltersResponse.asString();
        JsonPath quickFiltersPath = new JsonPath(quickFiltersJson);
        List<Map<String, Object>> quickFilters = quickFiltersPath.getList("data.quickFilters");

        String mfFiltersJson = (String) DataToShare.getValue("mfFilters");
        Map<String, Object> mfFilters = new Gson().fromJson(mfFiltersJson, Map.class);

        Map<String, Object> remapped_mfFilters = multiAssetUtils.remapFilterConfig(mfFilters);
        // formatAPIAndLogInReport(new Gson().toJson(remapped_mfFilters));
        Set<String> filterIds = remapped_mfFilters.keySet();
        String filterIdsJson = new Gson().toJson(filterIds);
        writeRequestAndResponseInReport(writer.toString(), quickFiltersResponse.asString(), Flow);
        LogStatus.info("Possible mfFilters keys are: ");
        formatAPIAndLogInReport(filterIdsJson);

        for (Map<String, Object> quickFilter : quickFilters) {
            String optionId = (String) quickFilter.get("optionId");
            if (!filterIds.contains(optionId)) {
                throw new AssertionError("Option ID: " + optionId + " not found in mfFilters keys.");
            } else {
                LogStatus.info("Quick filter optionId: " + optionId + " was found in mfFilters keys");
            }
        }
    }

    @Test(testName = "Verify Default Sort for MF", description = "Verify that valid default sort is present in mfSorts", priority = 3)
    @Parameters({"Flow"})
    public void verifyDefaultSortForMF(String Flow) {
        Response mfConfigResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(SearchRequestSpec.searchSpec())
                .when()
                .get(SearchResources.configMutualfund)
                .then()
                .extract().response();

        String jsonResponse = mfConfigResponse.asString();
        JsonPath jsonPath = new JsonPath(jsonResponse);
        String defaultSort = jsonPath.getString("data.defaultSort");

        String mfSortsJson = (String) DataToShare.getValue("mfSorts");
        Map<String, Object> mfSorts = new Gson().fromJson(mfSortsJson, Map.class);
        Map<String, Object> remapped_mfSorts = multiAssetUtils.remapFilterConfig(mfSorts);
        // formatAPIAndLogInReport(new Gson().toJson(remapped_mfSorts));
        Set<String> filterIds = remapped_mfSorts.keySet();
        String filterIdsJson = new Gson().toJson(filterIds);
        writeRequestAndResponseInReport(writer.toString(), mfConfigResponse.asString(), Flow);
        LogStatus.info("Possible mfSorts keys are: ");
        formatAPIAndLogInReport(filterIdsJson);

        if (!filterIds.contains(defaultSort)) {
            throw new AssertionError("Option ID: " + defaultSort + " not found in mfSorts keys.");
        } else {
            LogStatus.info("Default sort " + defaultSort + " was found in mfSorts keys");
        }
    }
}