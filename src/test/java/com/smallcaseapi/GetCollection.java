package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import commonutils.IConst;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.IOException;
import static io.restassured.RestAssured.given;

public class GetCollection extends BaseTest {

    private static Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetCollection.class);
    private static CollectionAPI collectionAPI = new CollectionAPI();
    //private static String collectionID;

    @Test(testName = "To validate Get collection API for success response code", description = "Get Collection API : To validate Get collection API for success response code")
    @Parameters({"broker"})
    public static void getCollection_shouldReturn200(String broker) throws IOException {

        String collectionID = collectionAPI.fetchCollections(broker);
        if(collectionID == null){
            logger.info("Test being skipped, since the broker does not have any collections in it....");
            throw new SkipException("The broker does not have any collections in it....");
        }
        logger.info("Creating request for get Collection API for collection ID {" + collectionID + "} - start "+ (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .queryParam("collection", collectionID)
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getCollection);

        logger.info("Creating request for get Collection API for collection ID {" + collectionID + "} - end "+ (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate schema for get collection API", description = "Get Collection API schema validation : To validate schema for get collection API")
    @Parameters({"broker"})
    public static void checkSchema(String broker) throws IOException {

        String collectionID = collectionAPI.fetchCollections(broker);
        if(collectionID == null){
            logger.info("Test being skipped, since the broker does not have any collections in it....");
            throw new SkipException("The broker does not have any collections in it....");
        }
        logger.info("Checking schema for get Collection API for collection ID {" + collectionID + "} - start "+ (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .queryParam("collection", collectionID)
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getCollection);

        logger.info("Checking schema for get Collection API for collection ID {" + collectionID + "} - end "+ (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasValidSchema(IConst.GET_COLLECTION_SCHEMA);
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Get collection API with logged out user", description = "Get Collection API for logged out user: To validate Get collection API with logged out user")
    @Parameters({"broker"})
    public static void getCollection_WITHOUTLOGIN_shouldReturn200(String broker) throws IOException {

        String collectionID = collectionAPI.fetchCollections(broker);
        if(collectionID == null){
            logger.info("Test being skipped, since the broker does not have any collections in it....");
            throw new SkipException("The broker does not have any collections in it....");
        }
        logger.info("Creating request for get Collection API for collection ID {" + collectionID + "} in logged out state - start "+ (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .queryParam("collection", collectionID)
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser(broker))
                .when()
                .get(SmallcaseResource.getCollection);

        logger.info("Creating request for get Collection API for collection ID {" + collectionID + "} in logged out state - end "+ (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_200_OK().hasHeaderApplicationJSON().isWithinAcceptedTimeLimit();
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Get collection API when no query param is given", description = "Get Collection API with no query parameters: To validate Get collection API when no query param is given")
    @Parameters({"broker"})
    public static void getCollection_shouldReturn400(String broker) throws IOException {

        logger.info("Creating request for get Collection API with no collection ID as query param - start "+ (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getCollection);

        logger.info("Creating request for get Collection API with no collection ID as query param  - end "+ (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_400_BADREQUEST();
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Get collection API when invalid collection ID is provided", description = "Get Collection API with invalid ID : To validate Get collection API when invalid collection ID is provided")
    @Parameters({"broker"})
    public static void getCollection_shouldReturn500(String broker) throws IOException {

        logger.info("Creating request for get Collection API with invalid collection ID as query param {" + "123" + "} - start "+ (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .queryParam("collection", "123")
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.getCollection);

        logger.info("Creating request for get Collection API with invalid collection ID as query param {" + "123" + "} - end "+ (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_500_INTERNAL_SERVER_ERROR();
        logger.info("Test passed for " + broker);
    }

    @Test(testName = "To validate Get collection API when collection ID provided is of different publisher", description = "Get Collection API with invalid ID : To validate Get collection API when collection ID provided is of different publisher")
    @Parameters({"broker"})
    public static void getCollectionWithCollectionIDFromDifferentBroker_shouldReturn404(String broker) throws IOException {

        String collectionID = collectionAPI.fetchCollections(broker);
        if(collectionID == null){
            logger.info("Test being skipped, since the broker does not have any collections in it....");
            throw new SkipException("The broker does not have any collections in it....");
        }

        logger.info("Creating request for get Collection API with invalid collection ID as query param {" + collectionID + "} - start "+ (broker));
        response = given()
                .filter(new RequestLoggingFilter(captor))
                .queryParam("collection", collectionID)
                .spec(RequestSpec.requestSpecificationForNonLoggedInUser("InvalidPublisher"))
                .when()
                .get(SmallcaseResource.getCollection);

        logger.info("Creating request for get Collection API with invalid collection ID as query param {" + collectionID + "} - end "+ (broker));
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + (broker));
        ResponseAssert.assertThat(response).returns_404_NOTFOUND();
        logger.info("Test passed for " + broker);
    }
}
