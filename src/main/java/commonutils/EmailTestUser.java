package commonutils;

import com.smallcase.resource.SmallcaseResource;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class EmailTestUser {


    private static final String[] domains = {"gmail.com", "yahoo.com"};
    private static final String[] prefixes = {"BEAutomationuser", "BETestuser"};

    /**This method is used to generate random email */
    public static String generateRandomEmail() {

        Random random = new Random();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String domain = domains[random.nextInt(domains.length)];
        String randomNumber = Integer.toString(random.nextInt(100));
        String email = prefix + randomNumber + "@" + domain;

        int otpValue = 1000 + random.nextInt(9000);
        String emailOtp = String.valueOf(otpValue);


        Map<String, String> testUser = new HashMap<>();
        testUser.put("email", email);

        testUser.put("otp", emailOtp);
        DataToShare.setValue("emailOtp", emailOtp);

        /**Here setting email as test user by using internal test user API*/

        String baseUrl = ConfigRead.getPropertyValue("Internal_url");
        if (baseUrl.equals("https://otp.stag.smallcase.com")) {
            RestAssured.baseURI = ConfigRead.getPropertyValue("Internal_url");
            /*Fetching secret from jenkin  */
            String clientSecret = System.getenv("stageSecret");
            Response emailTestUser = given()
                    .header("x-internal-admin-id", ConfigRead.getPropertyValue("internalClientId"))
                    .header("x-internal-admin-secret", clientSecret)
                    .header("Content-Type", "application/json")
                    .queryParam("clientId",ConfigRead.getPropertyValue("Email_client_id"))
                    .header("x-service-test-automation", "true").body(testUser).log().all()
                    .when()
                    .post(SmallcaseResource.phoneTestuser)
                    .then()
                    .extract()
                    .response();
        } else {
            RestAssured.baseURI = ConfigRead.getPropertyValue("Internal_url");
            String clientSecret = System.getenv("prodSecret");
            Response emailTestUser = given()
                    .header("x-internal-admin-id", ConfigRead.getPropertyValue("internalClientId"))
                    .header("x-internal-admin-secret", clientSecret)
                    .header("Content-Type", "application/json")
                    .queryParam("clientId", ConfigRead.getPropertyValue("Email_client_id"))
                    .header("x-service-test-automation", "true").body(testUser).log().all()
                    .when()
                    .post(SmallcaseResource.phoneTestuser)
                    .then()
                    .extract()
                    .response();
        }
        return email;
    }

    public static void main(String[] args) {
        String randomEmail = generateRandomEmail();

    }
}

