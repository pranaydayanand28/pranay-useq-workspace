package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import commonutils.JsonPathFinder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CollectionAPI extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CollectionAPI.class.getName());

    @Test(testName = "To validate Collection API", description = "Collection API : to validate collection API upon valid set of inputs")
    @Parameters({"broker"})
    public void collectionAPI_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for Collection API - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.collection);

        logger.info("Checking schema for Collection API - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();

        int dataArrSize = JsonPathFinder.getJsPath(response).get("data.size()");
        for (int i = 0; i < dataArrSize; i++) {
            assertThat(response.jsonPath().getMap("data[" + i + "]")).containsKeys("cid", "type", "groups");
        }
        logger.info("Test passed for " + broker);

    }

    @Test(testName = "To validate schema for Collection API", description = "Collection API schema validation: to validate schema for collection API upon valid set of inputs")
    @Parameters({"broker"})
    public void checkSchema(String broker) throws IOException {

        logger.info("Checking schema for Collection - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.collection);

        logger.info("Checking schema for Collection - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.COLLECTION_SCHEMA);
        logger.info("Test passed for " + broker);
    }


    @Test(testName = "To validate Collection API without login", description = "Collection API : to validate collection API upon valid set of inputs when the user has not logged in")
    @Parameters({"broker"})
    public void collectionAPI_WITHOUTLOGIN_shouldReturn200(String broker) {

        logger.info("Creating request for Collection API without login - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .when()
                .get(SmallcaseResource.collection);

        logger.info("Creating request for Collection API without login - end " + (broker));
        logger.info("Writing Request and Response to Extent Report " + (broker));

        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
        logger.info("Test passed for " + broker);
    }

    //This method will return all the collections the user can see
    public String fetchCollections(String broker) throws IOException {

        List<String> collection_IDS = new ArrayList<>();
        String Id = "";
        int numberOfCollection = 0;
        logger.info("Creating request for Collection APIs fetchCollections method - start " + (broker));
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.collection);

        logger.info("Checking schema for Collection APIs fetchCollections method - end " + (broker));

        if (response.getStatusCode() == HttpStatus.SC_OK) {
            try {
                numberOfCollection = JsonPathFinder.getJsPath(response).get("data.size()");
                for (int i = 0; i < numberOfCollection; i++) {
                    collection_IDS.add(JsonPathFinder.getJsPath(response).get("data[" + i + "]._id"));
                }
            } catch (Exception e) {
                logger.error("Could not get collection IDS due to error " + Arrays.toString(e.getStackTrace()));
            }
        }
        if(collection_IDS.size() == 0){
            Id = null;
        }
        else if(collection_IDS.size() == 1){
            Id = collection_IDS.get(0);
        }
        else {
            Id = collection_IDS.get(new Random().nextInt(numberOfCollection));
        }
        return Id;
    }
}
