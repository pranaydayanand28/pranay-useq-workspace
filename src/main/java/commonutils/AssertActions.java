package commonutils;

import io.restassured.response.Response;
import com.smallcase.resource.ApiConstants;
import org.testng.Assert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AssertActions {

   /*
    Custom assert to verify that the api success code returned is of 200 series
    Single method to verify generic success response criteria
    Single method to verify generic failure response criteria
    Custom functions to assert response body in different data types
    */

    public static void verifyStatusCode(Response response){
        assertTrue(String.valueOf(response.getStatusCode()).startsWith("20"), "Status code returned is " + response.getStatusCode());
    }

    public static void verifySuccess(Response response, Boolean status, String[] errors){
        Assert.assertEquals(response.getStatusCode(), ApiConstants.success);
        Assert.assertTrue(status);
        Assert.assertNull(errors);
    }

    public static void verifyBadRequests(Response response, Boolean status, Object data){
        Assert.assertEquals(response.getStatusCode(), ApiConstants.badRequest);
        Assert.assertFalse(status);
        Assert.assertNull(data);
    }

    public static void verifyUnauthorizedRequests(Response response, Boolean status){
        Assert.assertEquals(response.getStatusCode(), ApiConstants.unAuthorized);
        Assert.assertFalse(status);
    }


    public static void verifyResponseBody(String actual, String expected, String description){
        assertEquals(actual, expected, description);
    }

    public static void verifyResponseBody(float actual, float expected, String description) {
        assertEquals(actual, expected, description);
    }

    public static void verifyResponseBody(int actual, int expected, String description) {
        assertEquals(actual, expected, description);
    }

    public static void verifyResponseBody(double actual, double expected, String description) {
        assertEquals(actual, expected, description);
    }

    public static void verifyResponseBody(boolean actual, boolean expected, String description) {
        assertEquals(actual, expected, description);
    }


    public static void verifyResponseError(String[] data) {   //validate error received in-case of wrong leprechaun value
        for (String i : data) {
            assertEquals(ApiConstants.accessTokenError, i);
        }
    }

    public static void verifyNonNULLState(Response response, String jsonPath){
        Assert.assertNotNull(JsonPathFinder.getJsPath(response).get(jsonPath));
    }

}
