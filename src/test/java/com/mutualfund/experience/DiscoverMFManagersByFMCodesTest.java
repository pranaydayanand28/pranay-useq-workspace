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

public class DiscoverMFManagersByFMCodesTest extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DiscoverMFManagersByFMCodesTest.class.getName());

    @Test(testName = "Discover MF Managers by FMCodes", description = "Discover MF Managers using FMCodes in sequential order.")
    @Parameters({"PhoneNo","Flow"})
    public void discoverMFManagersByFMCodes(String PhoneNo , String Flow) {
        logger.info("Starting MF Managers discovery by FMCodes method:");

        String[] fmCodes = MFResource.fmCodes;
        for (int i = 0; i < fmCodes.length; i++) {
            fmCodes[i] = fmCodes[i].trim();
        }

        Response response_mfManagersByFMCodes = RestAssured.given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.samFlowRequestSpec())
                .queryParam("fmCodes[]", (Object[]) fmCodes)
                .when()
                .get(MFResource.mfDiscover + "/managers")
                .then()
                .extract().response();

        ResponseAssert.assertThat(response_mfManagersByFMCodes)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit()
                .hasRequiredKeysInsideEveryArrayItems("fmCode", "name", "imgUrl")
                .verifySequentialOrderOfIds(fmCodes, "fmCode");

        writeRequestAndResponseInReport(writer.toString(), response_mfManagersByFMCodes.prettyPrint(), Flow);
    }

}
