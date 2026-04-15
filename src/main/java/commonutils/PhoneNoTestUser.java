package commonutils;

import com.smallcase.resource.SmallcaseResource;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class PhoneNoTestUser {
    /**
     * This method used to generate random phone no run time
     */
    public static String generatePhoneNumber() {

        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6;
        String middleDigits = String.format("%09d", random.nextInt(1000000000));
        String phoneNumber = String.valueOf(firstDigit) + middleDigits;


        int otpValue = 1000 + random.nextInt(9000);
        String phoneOtp = String.valueOf(otpValue);



        Map<String, String> testUser = new HashMap<>();
        testUser.put("phone", phoneNumber);
        testUser.put("phoneCountryCode",  ConfigRead.getPropertyValue("internalphoneCountryCode"));
        testUser.put("otp", phoneOtp);
        DataToShare.setValue("phoneotp", phoneOtp);

        /**Here setting phone no as test user by using internal API  */

        String baseUrl = ConfigRead.getPropertyValue("Internal_url");
        if (baseUrl.equals("https://otp.stag.smallcase.com")) {
            RestAssured.baseURI = ConfigRead.getPropertyValue("Internal_url");
            String clientSecret = System.getenv("stageSecret");
            RestAssured.baseURI = ConfigRead.getPropertyValue("Internal_url");
            Response phoneTestUser = given()
                    .header("x-internal-admin-id", ConfigRead.getPropertyValue("internalClientId"))
                    .header("x-internal-admin-secret", clientSecret)
                    .header("Content-Type", "application/json")
                    .queryParam("clientId", ConfigRead.getPropertyValue("client_id"))
                    .header("x-service-test-automation", "true").body(testUser).log().all()
                    .when()
                    .post(SmallcaseResource.phoneTestuser)
                    .then()
                    .extract()
                    .response();

            System.out.println("response test user " + phoneTestUser.asString());
        } else {
            String clientSecret = System.getenv("prodSecret");
            RestAssured.baseURI = ConfigRead.getPropertyValue("Internal_url");
            Response phoneTestUser = given()
                    .header("x-internal-admin-id", ConfigRead.getPropertyValue("internalClientId"))
                    .header("x-internal-admin-secret", clientSecret)
                    .header("Content-Type", "application/json")
                    .queryParam("clientId", ConfigRead.getPropertyValue("client_id"))
                    .header("x-service-test-automation", "true").body(testUser).log().all()
                    .when()
                    .post(SmallcaseResource.phoneTestuser)
                    .then()
                    .extract()
                    .response();

            System.out.println("response test user " + phoneTestUser.asString());

        }

        return phoneNumber;
    }

    public static void main(String[] args) {
        String phoneNumber = generatePhoneNumber();

    }

}

