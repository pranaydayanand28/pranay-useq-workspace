package com.asserts;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import resource.reports.LogStatus;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class ResponseAssert extends AbstractAssert<ResponseAssert, Response> {

    SoftAssertions softAssertions = new SoftAssertions();

    protected ResponseAssert(Response response, Class<?> selfType) {
        super(response, selfType);
    }

    public static ResponseAssert assertThat(Response response) {
        return new ResponseAssert(response, ResponseAssert.class);
    }

    public ResponseAssert returns_200_OK() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 200, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        return this;
    }

    public ResponseAssert returns_201_CREATED() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 201, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);
        return this;
    }

    public ResponseAssert returns_204_NOCONTENT() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 204, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        return this;
    }

    public ResponseAssert returns_400_BADREQUEST() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 400, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        return this;
    }

    public ResponseAssert returns_401_UNAUTHORIZED() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 401, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        return this;
    }

    public ResponseAssert returns_403_FORBIDDEN() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 403, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_FORBIDDEN);
        return this;
    }

    public ResponseAssert returns_404_NOTFOUND() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 404, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        return this;
    }

    public ResponseAssert returns_500_INTERNAL_SERVER_ERROR() {
        Assertions.assertThat(actual.getStatusCode()).withFailMessage(() -> "The status code is not 500, API returned "+ actual.getStatusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return this;
    }

    public ResponseAssert hasHeaderApplicationJSON() {
        Assertions.assertThat(actual.header("Content-Type")).withFailMessage(() -> "Header with content type as JSON is not present").contains("application/json");
        return this;
    }

    public ResponseAssert isWithinAcceptedTimeLimit() {
        Assertions.assertThat(actual.getTimeIn(TimeUnit.MILLISECONDS)).withFailMessage(() -> "The API is taking longer than expected to load").isLessThan(30000);
        return this;
    }

    //this method will accept list of all the mandatory objects
    public ResponseAssert hasMandatoryObjectsPresent(String... keys) {
        Map<String, Object> dataMap = actual.jsonPath().getMap("data");
        for (String key : keys) {
            Assertions.assertThat(dataMap)
                    .withFailMessage(() -> "The API response does not have mandatory key " + key)
                    .containsKey(key);
        }
        return this;
    }

    //this method will accept the path of the schema
    public ResponseAssert hasValidSchema(String schema) {
//        Assertions.assertThatCode(()-> JsonSchemaValidator.matchesJsonSchema(new File(System.getProperty("user.dir") + schema)).matches(actual.body().asString()))
//                .withFailMessage(() -> "The Response does not match the schema defined for this API")
//                .doesNotThrowAnyException();
        actual.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema
                        (new File(System.getProperty("user.dir") + schema)));
        return this;
    }

    public ResponseAssert returnsValidErrorMessage(String errorMessage) {
        Assertions.assertThat(actual.jsonPath().getList("errors")).withFailMessage(() -> "The error message is not correct !!!").contains(errorMessage);
        return this;
    }

    public <T> ResponseAssert verifyResponseData(String key, T expectedValue){
        Assertions.assertThat(actual.jsonPath().getMap("data"))
                .withFailMessage(() -> "Value of "+ key + " in response body is not "+ expectedValue + " , response returned "+ actual.jsonPath().getMap("data").get(key))
                .extracting(key).isEqualTo(expectedValue);
        return this;
    }

    public ResponseAssert verifyResponseDataIsEmpty(String jsonpath){
        Assertions.assertThatCode(() -> actual.jsonPath().getList(jsonpath)).withFailMessage(() -> "The response path {" + jsonpath + "} is not empty").isNull();
        return this;
    }

    public ResponseAssert allSoftAsserts() {
        softAssertions.assertThat(actual.getTimeIn(TimeUnit.MILLISECONDS)).withFailMessage(() -> "The API is taking more than 1000 ms").isLessThan(1000);
        softAssertions.assertAll();
        return this;
    }

    public <T> ResponseAssert verifyEquality(T actualValue, T expectedValue){
        Assertions.assertThat(actualValue).withFailMessage(() -> "The actual and expected value does not match "+ "| Was supposed to return Expected : "+ expectedValue + " But returned Actual : "+ actualValue)
                .isEqualTo(expectedValue);
        return this;
    }

    public ResponseAssert hasRequiredKeysInsideEveryArrayItems(String... keys) {
        Object dataObject = actual.jsonPath().getList("data");
        Assertions.assertThat(dataObject).isInstanceOf(List.class);
        List<Map<String, Object>> dataArray = (List<Map<String, Object>>) dataObject;
        for (Map<String, Object> object : dataArray) {
            Assertions.assertThat(object).containsKeys(keys);
        }
        return this;
    }

    public ResponseAssert verifySequentialOrderOfIds(String[] Ids, String key) {
        List<Map<String, Object>> dataArray = actual.jsonPath().getList("data");
        for (int i = 0; i < Ids.length; i++) {
            String expectedId = Ids[i];
            String actualId = dataArray.get(i).get(key).toString();
            Assertions.assertThat(actualId).isEqualTo(expectedId);
        }
        return this;
    }

    public ResponseAssert itemsArrayIsNotEmpty(AtomicBoolean flag) {
        List<?> items = actual.jsonPath().getList("data.items");
        try {
            Assertions.assertThat(items).isNotEmpty();
        } catch (AssertionError e) {
            LogStatus.fail("data.items bucket found is EMPTY, for request below:");
            flag.set(false);
        }
        return this;
    }

    // Helps us to check whether the response items are sorted as per the key and operator (multiAsset-search)
    public void multiAsset_isSortedResponse(Map<String, Object> sortValue, AtomicBoolean flag) {
        String operator = (String) sortValue.get("operator");
        List<Map<String, Object>> overrideConfigs = (List<Map<String, Object>>) sortValue.get("overrideConfigs");
        String keyName;
        Boolean overrideKeys = (Boolean) sortValue.get("overrideKeys");
        if (overrideKeys != null && !overrideKeys) {
            keyName = "sortMetric";
        } else {
            keyName = (String) overrideConfigs.get(0).get("key");
        }
        List<Map<String, Object>> items = actual.jsonPath().getList("data.items");
        if (items.isEmpty()) {
            return; // Skip if items are empty as we handle this with help of itemsArrayIsNotEmpty
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        List<Comparable> actualSortMetrics = items.stream()
                .map(item -> {
                    Object value = item.get(keyName);
                    if (value instanceof Number) {
                        return new BigDecimal(value.toString());
                    } else if (value instanceof String) {
                        String stringValue = value.toString();
                        return LocalDateTime.parse(stringValue, formatter).format(formatter);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Comparable> expectedSortMetrics = new ArrayList<>(actualSortMetrics);
        if ("desc".equalsIgnoreCase(operator)) {
            Collections.sort(expectedSortMetrics, Comparator.<Comparable>reverseOrder());
        } else if ("asc".equalsIgnoreCase(operator)) {
            Collections.sort(expectedSortMetrics);
        } else {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
        String expectedMetricsStr = expectedSortMetrics.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
        String actualMetricsStr = actualSortMetrics.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
        try {
            Assertions.assertThat(actualSortMetrics).isEqualTo(expectedSortMetrics);
            LogStatus.pass("data.items bucket found is sorted, for request below | key: " + keyName + " | operator: " + operator);
            LogStatus.pass(expectedMetricsStr);
        } catch (AssertionError e) {
            LogStatus.fail("data.items bucket found is not sorted, for request below | key: " + keyName + " | operator: " + operator);
            LogStatus.fail("Expected: " + expectedMetricsStr);
            LogStatus.fail("Actual: " + actualMetricsStr);
            flag.set(false);
        }
    }

    //Validate response data array in the given path based on the key:(isEmpty, isNotEmpty)
    public ResponseAssert validateResponseDataArray(String key, String path) {
        List<Object> orders = actual.jsonPath().getList(path); // Extract the data array of 'path' from the response using JSONPath

        if (key.equalsIgnoreCase("isEmpty")) {
            Assertions.assertThat(orders).withFailMessage(() -> "The orders array is not empty").isEmpty(); // Assert that the 'orders' list is empty
        } else if (key.equalsIgnoreCase("isNotEmpty")) {
            int totalPages = actual.jsonPath().getInt("data.pagination.total.pages");
            int pageNo = actual.jsonPath().getInt("data.pagination.pageNo");
            if (pageNo <= totalPages) {
                Assertions.assertThat(orders).withFailMessage(() -> "The orders array is empty").isNotEmpty(); // Assert that the 'orders' list is not empty
            } else {
                Assertions.assertThat(orders).withFailMessage(() -> "The orders array is not empty").isEmpty(); // Assert that the 'orders' list is empty
            }
        } else {
            Assertions.fail(key+" doesn't match with isEmpty/isNotEmpty");
        }
        return this;
    }

    //Pagination validations for Default Page No(:1) and Page Size(:10)
    public ResponseAssert validatePagination(){ return validatePagination(1, 10); }

    //Pagination validations for non default Page No and Page Size
    public ResponseAssert validatePagination(int pageNo, int pageSize) {
        HashMap<String, Object> pagination = actual.jsonPath().get("data.pagination"); //Extracting pagination object from the response as a Hashmap
        HashMap<String, Object> total = (HashMap<String, Object>) pagination.get("total"); //Extracting total object from the pagination as another Hashmap

        int totalResults = (Integer)total.get("data"); //Get total number of results from pagination>total
        int totalPages = (Integer)total.get("pages"); //Get total number of pages from pagination>total
        int actualPageNo = (Integer)pagination.get("pageNo"); //Get page number from pagination
        int actualPageSize = (Integer)pagination.get("pageSize"); //Get Page Size from the pagination
        Boolean hasNext = (Boolean)pagination.get("hasNext"); //Get hasNext value from pagination
        String nextPage = (String)pagination.get("nextPage"); //Get NextPage value from pagination
        Boolean hasPrev = (Boolean)pagination.get("hasPrev"); //Get hasPrev value from pagination
        String prevPage = (String)pagination.get("prevPage"); //Get prevPage value from pagination

        Assertions.assertThat(actualPageNo).withFailMessage(() -> "Page number expected to be '"+pageNo+"' but found '"+actualPageNo+"'").isEqualTo(pageNo);
        Assertions.assertThat(actualPageSize).withFailMessage(() -> "Page size expected to be '"+pageSize+"' but found '"+actualPageSize+"'").isEqualTo(pageSize);

        if (totalResults <= pageSize) {
            Assertions.assertThat(totalPages).withFailMessage(() -> "Total number of pages expected to be '1' but had '" + totalPages + "'").isEqualTo(1);
            if (pageNo == 1) {
                Assertions.assertThat(hasNext).withFailMessage(() -> "hasNext expected to be 'false' but had '" + hasNext + "'").isFalse();
                Assertions.assertThat(nextPage).withFailMessage(() -> "Next page expected to be 'Empty/null' but had '" + nextPage + "'").isIn(new String[]{null, ""});
                Assertions.assertThat(hasPrev).withFailMessage(() -> "hasPrev expected to be 'false' but had '" + hasPrev + "'").isFalse();
                Assertions.assertThat(prevPage).withFailMessage(() -> "Previous page expected to be 'Empty/null' but had '" + prevPage + "'").isIn(new String[]{null, ""});
            } else {
                Assertions.assertThat(hasNext).withFailMessage(() -> "hasNext expected to be 'false' but had '"+hasNext+"'").isFalse();
                Assertions.assertThat(nextPage).withFailMessage(() -> "Next page expected to be 'Empty/null' but had '"+nextPage+"'").isIn(new String[] { null, "" });
                Assertions.assertThat(hasPrev).withFailMessage(() -> "hasPrev expected to be 'true' but had '"+hasPrev+"'").isTrue();
                Assertions.assertThat(prevPage).withFailMessage(() -> "Previous page expected to be 'pageNo="+(pageNo-1)+"&pageSize="+pageSize+"' but had '"+prevPage+"'").contains("pageNo="+(pageNo-1)+"&pageSize="+pageSize);
            }
        } else if (totalResults > pageSize) {
            Assertions.assertThat(totalPages).withFailMessage(() -> "Total number of pages expected to be '"+(int)Math.ceil((double)totalResults/pageSize)+"' but had '"+totalPages+"'").isEqualTo((int)Math.ceil((double)totalResults/pageSize));
            if (pageNo == 1) {
                Assertions.assertThat(hasNext).withFailMessage(() -> "hasNext expected to be 'true' but had '"+hasNext+"'").isTrue();
                Assertions.assertThat(nextPage).withFailMessage(() -> "Next page expected to be 'pageNo=2&pageSize="+pageSize+"' but had '"+nextPage+"'").contains("pageNo=2&pageSize="+pageSize);
                Assertions.assertThat(hasPrev).withFailMessage(() -> "hasPrev expected to be 'false' but had '"+hasPrev+"'").isFalse();
                Assertions.assertThat(prevPage).withFailMessage(() -> "Previous page expected to be 'Empty/null' but had '"+prevPage+"'").isIn(new String[] { null, "" });
            } else if (pageNo > 1 && pageNo < totalPages) {
                Assertions.assertThat(hasNext).withFailMessage(() -> "hasNext expected to be 'true' but had '"+hasNext+"'").isTrue();
                Assertions.assertThat(nextPage).withFailMessage(() -> "Next page expected to be 'pageNo="+(pageNo+1)+"&pageSize="+pageSize+"' but had '"+nextPage+"'").contains("pageNo="+(pageNo+1)+"&pageSize="+pageSize);
                Assertions.assertThat(hasPrev).withFailMessage(() -> "hasPrev expected to be 'true' but had '"+hasPrev+"'").isTrue();
                Assertions.assertThat(prevPage).withFailMessage(() -> "Previous page expected to be 'pageNo="+(pageNo-1)+"&pageSize="+pageSize+"' but had '"+prevPage+"'").contains("pageNo="+(pageNo-1)+"&pageSize="+pageSize);
            } else if (pageNo == totalPages) {
                Assertions.assertThat(hasNext).withFailMessage(() -> "hasNext expected to be 'false' but had '"+hasNext+"'").isFalse();
                Assertions.assertThat(nextPage).withFailMessage(() -> "Next page expected to be 'Empty/null' but had '"+nextPage+"'").isIn(new String[] { null, "" });
                Assertions.assertThat(hasPrev).withFailMessage(() -> "hasPrev expected to be 'true' but had '"+hasPrev+"'").isTrue();
                Assertions.assertThat(prevPage).withFailMessage(() -> "Previous page expected to be 'pageNo="+(pageNo-1)+"&pageSize="+pageSize+"' but had '"+prevPage+"'").contains("pageNo="+(pageNo-1)+"&pageSize="+pageSize);
            } else {
                Assertions.fail("'totalResults:"+totalResults+"' > 'pageSize:"+pageSize+"' but doesn't satisfies any of the internal conditions");
            }
        } else {
            Assertions.fail("Check 'totalResults:"+totalResults+"' and 'pageSize:"+pageSize+"' in the response");
        }
        return this;
    }
}
