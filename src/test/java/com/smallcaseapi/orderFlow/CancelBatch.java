package com.smallcaseapi.orderFlow;

import static io.restassured.RestAssured.*;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CancelBatch extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CancelBatch.class);

    public void archiveOrder(String broker) throws IOException {

        logger.info("Since the batch has tried repairing itself too many times, archiving this batch");

        Response response = given().spec(RequestSpec.requestSpecification(broker))
                .when().body(com.smallcaseapi.payload.CancelBatch.cancelBatchPayload())
                .post(SmallcaseResource.cancelBatch).then().extract().response();

        if (response.getStatusCode() == 200) {
            logger.info("Batch archived successfully");
        }
        else{
            logger.error("Some error occurred in archiving");
        }
    }

}
