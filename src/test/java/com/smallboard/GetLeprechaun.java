package com.smallboard;

import com.smallcase.resource.SmallcaseResource;
import commonutils.ConfigRead;
import commonutils.JsonPathFinder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static io.restassured.RestAssured.given;

public class GetLeprechaun {

    static String leprechaun;
    static Response response;
    private static final Logger logger = LoggerFactory.getLogger(GetLeprechaun.class.getName());

    public static String newLeprechaun() {

        logger.info("Creating new leprechaun");
        RestAssured.baseURI = ConfigRead.getPropertyValue("smallcaseapi_url");
        String baseUrl = ConfigRead.getPropertyValue("smallcaseapi_url");

        if(baseUrl.equals("https://api-stag.smallcase.com")) {
            /*stageAuthToken taking authorization token from jenkins for internal API*/
            String bearerToken = System.getenv("stageAuthToken");
            response = given().auth().preemptive().oauth2(bearerToken).header("Content-Type", ContentType.JSON).log().all().when().post(SmallcaseResource.getLeprechaun).then().extract().response();
            System.out.println(response.asString());
        }
        else {
            /*prodAuthToken taking authorization token from jenkins for internal API*/

            String bearerToken = System.getenv("prodAuthToken");
            response = given().auth().preemptive().oauth2(bearerToken).header("Content-Type", ContentType.JSON).log().all().when().post(SmallcaseResource.getLeprechaun).then().extract().response();
            System.out.println(response.asString());
        }
        if (response.getStatusCode() == 200)
            leprechaun = JsonPathFinder.getJsPath(response).get("data.userId");

        else newLeprechaun();
        return leprechaun;
    }
}
