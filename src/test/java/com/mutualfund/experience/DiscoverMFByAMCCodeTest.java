package com.mutualfund.experience;

import com.asserts.ResponseAssert;
import com.CommonBaseTest;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.MFResource;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class DiscoverMFByAMCCodeTest extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DiscoverMFByAMCCodeTest.class.getName());

    @Test(testName = "Discover MF by AMC Code", description = "Discover MF using AMC Code.")
    @Parameters({"PhoneNo","Flow"})
    public void discoverMFByAMCCode(String PhoneNo, String Flow) {
        logger.info("Starting MF discovery by AMC Code method:");

        String[] amcCodesArray = MFResource.amcCodes;

        for (String amcCode : amcCodesArray) {
            amcCode = amcCode.trim();  // Trim the code here
            resetCaptorAndWriter();
            logger.info("Processing AMC Code: " + amcCode);

            Response response_mfByAMCCode = RestAssured.given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .when()
                    .get(MFResource.mfDiscover + "/amc/" + amcCode)
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(response_mfByAMCCode)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .hasMandatoryObjectsPresent("amc", "description", "mfCount", "amcCode");

            writeRequestAndResponseInReport(writer.toString(), response_mfByAMCCode.prettyPrint(), Flow);
        }
    }
}