package commonutils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class JsonPathFinder {
	

	/**
	 * This method will store json response and find out specific path .
	 */

	public static JsonPath getJsPath(Response r) {
		JsonPath path = new JsonPath(r.asString());
		return path;
	}
}
