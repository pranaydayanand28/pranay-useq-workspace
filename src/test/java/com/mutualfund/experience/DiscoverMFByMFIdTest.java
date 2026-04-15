package com.mutualfund.experience;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.MFResource;
import io.restassured.filter.log.RequestLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DiscoverMFByMFIdTest extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DiscoverMFByMFIdTest.class.getName());

    @Test(testName = "Discover MF by Search IDs", description = "Discover MF using search IDs in sequential order")
    @Parameters({"PhoneNo", "Flow"})
    public void discoverMFBySearchIds(String PhoneNo, String Flow) {
        logger.info("Starting MF discovery by MF ID method:");

        String[] mfIds = MFResource.mfIDs;

        for (String mfID : mfIds) {
            String trimmedMfID = mfID.trim();
            resetCaptorAndWriter();
            logger.info("Processing MF ID: " + trimmedMfID);

            Response response_mfByID = RestAssured.given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .when()
                    .get(MFResource.mfDetails + "/" + trimmedMfID)
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(response_mfByID)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .hasMandatoryObjectsPresent("mfId", "slug", "meta", "ratios", "labels", "transactionalInfo", "volatility", "platformData")
                    .isWithinAcceptedTimeLimit();

            writeRequestAndResponseInReport(writer.toString(), response_mfByID.prettyPrint(), Flow);
        }
    }
}
