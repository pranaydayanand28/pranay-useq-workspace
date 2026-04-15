package com.otpservice;

import java.util.Map;

import io.restassured.filter.log.RequestLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.asserts.ResponseAssert;
import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;

import commonutils.ConfigRead;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class DeleteClient extends OtpServiceBaseTest {

        private static final Logger logger = LoggerFactory.getLogger(DeleteClient.class.getName());

        @BeforeClass
        public void beforeClass() {
                // This function runs one before this class is executed, and makes sure that a
                // valid client is available in the DB
                // make sure that a client exists
                // - get the valid base config
                // - create the client

                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();

                given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload).log().all()
                                .when()
                                .post(ClientResource.client)
                                .then()
                                .log().all()
                                .extract()
                                .response();
        }

        @Test(testName = "OTP service client deletion success", description = "To validate client deletion on OTP service", priority = 1)
        @Parameters({"Flow"})
        public void deleteClientSuccess(String Flow) {
                Response deleteResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                ResponseAssert.assertThat(deleteResponse).returns_200_OK();
                writeRequestAndResponseInReport(writer.toString(), deleteResponse.prettyPrint() , Flow);
        }

        @Test(testName = "OTP service client deletion error", description = "To validate client deletion on OTP service when the client does not exist", priority = 2)
        @Parameters({"Flow"})
        public void deleteClientErrorDne(String Flow) {
                Response deleteResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                // TODO: should have a better way to not need to hardcode this full error
                String errorMessage = String.format("No deletion occured. No changes present for : %s",
                                ConfigRead.getPropertyValue("otp_service_qa_client"));
                logger.info(deleteResponse.asString());
                ResponseAssert.assertThat(deleteResponse).returns_404_NOTFOUND()
                                .returnsValidErrorMessage(errorMessage);
                writeRequestAndResponseInReport(writer.toString(), deleteResponse.prettyPrint() , Flow);
        }

        // TODO: add a test for prod environment, where client deletion should not be
        // possible

        @Test(testName = "Delete client and verify removal", description = "Delete client and verify it no longer exists", priority = 3)
        @Parameters({"Flow"})
        public void deleteClientAndVerifyRemoval(String Flow) {
                // First create a client
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload)
                                .when()
                                .post(ClientResource.client);

                // Delete the client
                Response deleteResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                ResponseAssert.assertThat(deleteResponse).returns_200_OK();

                // Try to get the deleted client
                Response getResponse = given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .get(ClientResource.client);

                ResponseAssert.assertThat(getResponse).returns_404_NOTFOUND();
                writeRequestAndResponseInReport(writer.toString(), deleteResponse.prettyPrint() + "\n\nGet after delete: " + getResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Delete client without clientId parameter", description = "To validate error when clientId is not provided", priority = 4)
        @Parameters({"Flow"})
        public void deleteClientWithoutClientId(String Flow) {
                Response deleteResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                // No clientId query parameter
                                .when()
                                .delete(ClientResource.client);

                logger.info(deleteResponse.asString());
                ResponseAssert.assertThat(deleteResponse).returns_404_NOTFOUND();
                writeRequestAndResponseInReport(writer.toString(), deleteResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Delete client with empty clientId", description = "To validate error when clientId is empty", priority = 5)
        @Parameters({"Flow"})
        public void deleteClientEmptyClientId(String Flow) {
                Response deleteResponse = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId", "")
                                .when()
                                .delete(ClientResource.client);

                logger.info(deleteResponse.asString());
                ResponseAssert.assertThat(deleteResponse).returns_404_NOTFOUND();
                writeRequestAndResponseInReport(writer.toString(), deleteResponse.prettyPrint(), Flow);
        }

        @Test(testName = "Delete already deleted client", description = "To validate idempotency - delete same client multiple times", priority = 6)
        @Parameters({"Flow"})
        public void deleteClientIdempotency(String Flow) {
                // Create a client first
                Map<String, Object> clientCreatePayload = Common.getValidMinimalClientConfig();
                given()
                                .spec(RequestSpec.otpServiceClient())
                                .body(clientCreatePayload)
                                .when()
                                .post(ClientResource.client);

                // Delete the client first time
                Response firstDelete = given()
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                ResponseAssert.assertThat(firstDelete).returns_200_OK();

                // Delete the client second time
                Response secondDelete = given().filter(new RequestLoggingFilter(captor))
                                .spec(RequestSpec.otpServiceClient())
                                .queryParam("clientId",
                                                ConfigRead.getPropertyValue("otp_service_qa_client"))
                                .when()
                                .delete(ClientResource.client);

                logger.info(secondDelete.asString());
                ResponseAssert.assertThat(secondDelete).returns_404_NOTFOUND();
                writeRequestAndResponseInReport(writer.toString(), secondDelete.prettyPrint(), Flow);
        }
}
