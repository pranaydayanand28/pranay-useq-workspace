package com.otpservice;

import com.CommonBaseTest;
import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;
import commonutils.ListDatatoShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DeleteFilteredClients extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(DeleteFilteredClients.class);

    private void deleteClient(String clientId, String accountId) {
        String deleteUrl = ClientResource.filterClient + "?clientId=" + clientId + "&accountId=" + accountId;

        logger.info("Calling DELETE API for accountId: " + accountId);

        try{
        Response deleteResponse = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.otpServiceTestUsers()) // Use appropriate spec
                .log().all()
                .when()
                .delete(deleteUrl)
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", equalTo("OK"))
                .extract()
                .response();

        logger.info("Delete Response: " + deleteResponse.prettyPrint());
        System.out.print("Successfully deleted client:");
        } catch (Exception e) {
            logger.error("Failed to delete client with accountId: {}", accountId, e);
        }
    }

    // Method to delete all clients in the filtered list
    @Test(testName = "Delete Clients from OTP service", description = "To validate delete clients from OTP service")
    @Parameters({"clientId", "Flow"})
    public void deleteAllFilteredClients(String clientId, String Flow) {
        logger.info("Test started: Deleting clients from OTP service. Flow: {}", Flow);

        try {
            // Fetch the list of filtered clients
            List<List<Map<String, Object>>> filteredClientsList = ListDatatoShare.getAllLists("filteredClients");
            logger.debug("Filtered clients list retrieved: {}", filteredClientsList);
            System.out.print("filteredClientsList: " + filteredClientsList);

            // Iterate over each group of clients and delete them
            for (List<Map<String, Object>> clientGroup : filteredClientsList) {
                for (Map<String, Object> clientData : clientGroup) {
                    String accountId = (String) clientData.get("_id");

                    if (accountId != null && !accountId.isEmpty()) {
                        deleteClient(clientId, accountId);
                    } else {
                        logger.warn("Account ID is null or empty for one of the clients. Skipping...");
                    }
                }
            }
            logger.info("Client deletion process completed.");
            writeRequestAndResponseInReport(writer.toString(), "Successfully deleted all clients.", Flow);
        } catch (Exception e) {
            logger.error("An error occurred while deleting clients: ", e);
            throw e;
        }
    }
}