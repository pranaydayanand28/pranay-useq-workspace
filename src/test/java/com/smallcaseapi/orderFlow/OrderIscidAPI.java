package com.smallcaseapi.orderFlow;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import commonutils.DataToShare;
import commonutils.JsonPathFinder;
import commonutils.AssertActions;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class OrderIscidAPI extends BaseTest {

    static Response response;
    static String iscid;
    private static final Logger logger = LoggerFactory.getLogger(OrderIscidAPI.class.getName());

    @Test(priority = 9, testName = "To validate verify Order ISCID API", description = "verify Order ISCID API : to validate verify Order ISCID API upon valid set of inputs")
    @Parameters({"broker"})
    public static void orderIscid(String broker) throws IOException {


        logger.info("Creating request for Verify Order API - start");

        iscid = (String) DataToShare.getValue("iscid");
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .queryParam("iscid", iscid)
                .when()
                .get(SmallcaseResource.orderIscid)
                .then()
                .extract().response();

        logger.info("Creating request for Verify Order API - end");
        logger.info("Writing Request and Response to Extent Report");

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response");
        AssertActions.verifyStatusCode(response);

        /**
         * Fetching data and batch array size , in order to check mandatory assertion.
         */

        int dataSz = JsonPathFinder.getJsPath(response).get("data.size()");

        for (int i = 0; i < dataSz; i++) {

            int batchSize = JsonPathFinder.getJsPath(response).get("data[" + i + "].batches.size()");
            for (int j = i; j < batchSize; j++) {

                ValidatableResponse respo = given().spec(RequestSpec.requestSpecification(broker)).queryParam("iscid", iscid)
                        .when().get(SmallcaseResource.orderIscid).then().assertThat()
                        .body("data[" + i + "].batches[" + j + "]", hasKey("batchId"))
                        .body("data[" + i + "].batches[" + j + "]", hasKey("originalLabel"))
                        .body("data[" + i + "].batches[" + j + "]", hasKey("previousBatchId"))
                        .body("data[" + i + "].batches[" + j + "]", hasKey("status"));

            }
        }

    }

}
