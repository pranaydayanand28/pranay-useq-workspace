package com.otpservice;

import com.CommonBaseTest;
import com.asserts.ResponseAssert;
import com.otpService.resource.ClientResource;
import com.otpService.resource.RequestSpec;

import commonutils.ListDatatoShare;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FilterClient extends CommonBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(FilterClient.class.getName());
    private static final String[] prefixes = {"BEAutomationuser", "BETestuser"};

    @Test(testName = "Filter Clients from OTP service", description = "To validate fetching clients from OTP service")
    @Parameters({"clientId", "Flow"})
    public void filterClient(String clientId, String Flow) {
        logger.info("Calling OTP service to filter clients");
        logger.info("Parameters: clientId = {}, Flow = {}", clientId, Flow);

        try {
            Response clientFilterResponse = given()
                    .filter(new RequestLoggingFilter(captor))
                    .spec(RequestSpec.otpServiceTestUsers())
                    .log().all()
                    .queryParam("clientId", clientId)
                    .when()
                    .get(ClientResource.filterClient)
                    .then()
                    .log().all()
                    .extract()
                    .response();

            logger.info("Response: " + clientFilterResponse.prettyPrint());
            System.out.println(clientFilterResponse.asString());

            // Assert response
            ResponseAssert.assertThat(clientFilterResponse)
                    .returns_200_OK()
                    .hasHeaderApplicationJSON()
                    .isWithinAcceptedTimeLimit();

            clientFilterResponse.then()
                    .assertThat()
                    .body("success", equalTo(true))
                    .body("data", not(emptyArray()));

            List<Map<String, Object>> clients = clientFilterResponse.jsonPath().getList("data");
            System.out.println("clients response " + clients);

            for (Map<String, Object> clientData : clients) {
                String email = (String) clientData.get("email");
                String accountId = (String) clientData.get("_id");
                String otp = (String) clientData.get("otp");
                String clientIdFromResponse = (String) clientData.get("clientId");

                logger.info("Processing client with account ID: {}", accountId);

                if (email != null && !email.isEmpty()) {
                    logger.info("Client email: {}", email);
                    boolean emailMatched = false;

                    for (String prefix : prefixes) {
                        if (email.startsWith(prefix)) {
                            emailMatched = true;
                            logger.info("Email '{}' starts with prefix '{}', performing operations.", email, prefix);

                            // Create a mutable list with the clientData map
                            List<Map<String, Object>> clientDataList = new ArrayList<>();
                            clientDataList.add(clientData);
                            ListDatatoShare.addList("filteredClients", clientDataList);

                            logger.info("Stored client data for account ID: {}", accountId);
                            logger.info("Client ID: {}, OTP: {}", clientIdFromResponse, otp);

                            break;
                        }
                    }

                    if (!emailMatched) {
                        logger.info("No matching prefixes found for email '{}'.", email);
                    }
                } else {
                    logger.warn("Email is null or empty for client with account ID: {}", accountId);
                }
            }
            writeRequestAndResponseInReport(writer.toString(), clientFilterResponse.prettyPrint(), Flow);
        } catch (Exception e) {
            logger.error("An error occurred while filtering clients: ", e);
            throw e;
        }
    }
}