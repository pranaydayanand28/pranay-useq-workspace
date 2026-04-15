package com.smallcaseapi.orderFlow;

import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import com.smallcaseapi.BaseTest;

import static io.restassured.RestAssured.*;
import com.smallcaseapi.payload.StartSipPayload;
import commonutils.AssertActions;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;

public class StartSIP extends BaseTest {

    static Response response;
    private static final Logger logger = LoggerFactory.getLogger(StartSIP.class.getName());

    @Test(priority = 2,testName = "To validate start SIP API", description = "Start SIP : To validate if able to start SIP with valid inputs")
    @Parameters({"broker"})
    public void startSIP(String broker) throws IOException, InterruptedException {

        logger.info("Creating request for Start SIP API - start");

        response = given()
                .filter(new RequestLoggingFilter(captor))
                        .spec(RequestSpec.requestSpecification(broker))
                                .body(StartSipPayload.startSIP(broker))
                                        .when().post(SmallcaseResource.startSIP).then().extract().response();


        logger.info("Creating request for Start SIP API - end");
        logger.info("Writing Request and Response to Extent Report");

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response");
        AssertActions.verifyStatusCode(response);

        SipUserActionAPI.sipUserAction(broker);
    }
}
