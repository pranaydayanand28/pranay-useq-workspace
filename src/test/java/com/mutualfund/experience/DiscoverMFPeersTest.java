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
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;

public class DiscoverMFPeersTest extends CommonBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DiscoverMFPeersTest.class.getName());

    @Test(testName = "Discover MF Peers", description = "Discover MF Peers using an MF ID and get four kinds of peer labels")
    @Parameters({"PhoneNo", "Flow"})
    public void discoverMFPairs(String PhoneNo, String Flow) {
        logger.info("Starting MF peers discovery method:");

        String[] mfIds = MFResource.mfIDs;

        for (String mfID : mfIds) {
            String trimmedMfID = mfID.trim();
            resetCaptorAndWriter();
            logger.info("Processing MF ID: " + trimmedMfID);

            Response response_mfPeers = RestAssured.given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.samFlowRequestSpec())
                    .when()
                    .get(MFResource.mfDiscover + "/" + trimmedMfID + "/peer")
                    .then()
                    .extract().response();

            ResponseAssert.assertThat(response_mfPeers)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit()
                    .hasRequiredKeysInsideEveryArrayItems("mfId", "mutualfund", "label");

            List<String> expectedLabels = Arrays.asList("Best sharpe ratio", "Largest fund size", "Least Volatile", "Highest 1Y returns");
            List<String> actualLabels = response_mfPeers.jsonPath().getList("data.label");

            for (String label : expectedLabels) {
                Assertions.assertThat(actualLabels)
                        .as(label + " label is missing")
                        .contains(label);
            }

            writeRequestAndResponseInReport(writer.toString(), response_mfPeers.prettyPrint(), Flow);
        }
    }
}
