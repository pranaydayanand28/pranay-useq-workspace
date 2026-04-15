package com.smallcaseapi.orderFlow;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import java.io.IOException;
import commonutils.DataToShare;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.response.Response;

public class SipUserActionAPI {
	

	public static void sipUserAction(String broker) throws IOException {

		/**
		 * sip user action Api call it will get executed on iscid label
		 */

		String iscid = (String)DataToShare.getValue("iscid");
		Response response = given().spec(RequestSpec.requestSpecification(broker)).queryParam("iscid", iscid).when()
				.get(SmallcaseResource.userSipAction).then().assertThat().statusCode(200)
				.body("data.amount", notNullValue()).body("data.scheduledDate", notNullValue())
				.body("data.weightConfig", notNullValue()).extract().response();

		System.out.println("user sip action  respo" + response.asString());

	}
}
