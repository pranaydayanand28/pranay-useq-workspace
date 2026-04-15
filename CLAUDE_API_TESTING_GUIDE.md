# Claude API Testing Framework Guide

> This document is written for Claude. It describes the exact architecture, patterns, and step-by-step process for adding API tests to this framework. Follow every instruction precisely — do not deviate from existing conventions.

---

## 1. Technology Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 1.8 | Language |
| Maven | 3.6.3 | Build tool |
| TestNG | 7.5 | Test runner |
| REST Assured | 5.0.1 | HTTP client for API calls |
| ExtentReports | 5.0.9 | HTML test reports |
| AssertJ | 3.22.0 | Fluent assertions |
| SLF4J + Logback | 1.7.36 | Logging |
| Gson + Jackson | 2.9.0 / 2.13.3 | JSON processing |
| Apache POI | 5.2.2 | Excel test data |

---

## 2. Framework Layer Map

```
sc-qa-restassured/
├── config/                          → TestNG XML suite files (test execution config)
├── src/main/java/
│   ├── com/allconfig/               → Environment .properties files (URLs, keys)
│   ├── com/smallcase/resource/      → API endpoints, request/response specs, constants
│   ├── com/otpService/resource/     → OTP service request specs & endpoints
│   └── commonutils/                 → Shared utilities (config, data, excel, json, etc.)
│       └── resource/
│           ├── reports/             → ExtentReports setup + LogStatus logger
│           ├── resource/            → TestNG listeners, retry analyzer
│           └── testData/            → JSON schemas, Excel data files, JSON test data
└── src/test/java/
    ├── com/asserts/ResponseAssert.java  → Fluent assertion builder (extend AbstractAssert)
    ├── com/smallcaseapi/BaseTest.java   → Base class for all Smallcase API tests
    ├── com/integration/
    │   ├── IntegrationBaseTest.java     → Base class for integration/broker API tests
    │   └── [YourNewTest].java           → Integration test classes go here
    └── com/smallcaseapi/
        ├── payload/                     → Payload builder classes
        └── [YourNewTest].java           → Smallcase API test classes go here
```

---

## 3. Configuration System

### How it works

`ConfigRead.getPropertyValue(String key)` reads from one of four property files based on the `-Denv` JVM system property:

| `-Denv` value | Property file |
|---------------|---------------|
| `staging` (default) | `src/main/java/com/allconfig/configAPI.properties` |
| `production` | `src/main/java/com/allconfig/configAPIPROD.properties` |
| `development` | `src/main/java/com/allconfig/configAPIDEV.properties` |
| `preview` | `src/main/java/com/allconfig/configAPIPreview.properties` |

### Adding a new URL/property

Add the key-value pair to **all four** property files. Example:
```properties
# configAPI.properties (staging)
broker_url=https://scb.stag.smallcase.com

# configAPIPROD.properties (production)
broker_url=https://scb.prod.smallcase.com
```

Access in code:
```java
ConfigRead.getPropertyValue("broker_url")
```

### Available properties (staging values)
```properties
auth_url=https://auth-stag.smallcase.com
smallcaseapi_url=https://api-stag.smallcase.com
gatewayapi_url=https://gatewayapi-stag.smallcase.com
otp_url=https://otp-stag.smallcase.com
broker_url=https://scb.stag.smallcase.com
smallboard_url=https://smallboard-be.stag.smallcase.com
nexum-api=https://api-stag.nexum.smallcase.com
client_id=smallcase-platform
phoneCountryCode=+91
```

---

## 4. API Endpoint Registry — SmallcaseResource.java

**File:** `src/main/java/com/smallcase/resource/SmallcaseResource.java`

All API paths are static String fields. **Always add new endpoints here.** Never hardcode path strings in test classes.

### Exact pattern to follow
```java
public static String endpointName = "/api/path/here";
```

### Existing examples
```java
public static String brokerLogIn = "/auth/BrokerLogin";
public static String dashboard = "/smallcases/dashboard";
public static String removeWatchlist = "/user/sc/watchlist/remove";
public static String amoActiveHours = "/api/v1/misc/amoActiveHours";
```

---

## 5. Request Specifications — RequestSpec.java

**File:** `src/main/java/com/smallcase/resource/RequestSpec.java`

Each method returns a `RequestSpecification` built with `RequestSpecBuilder`. Use the right spec for the auth type.

### Available spec methods and when to use them

| Method | Use case |
|--------|----------|
| `requestSpecification(String broker)` | Broker-authenticated Smallcase API calls |
| `requestSpecification()` | Smallcase API call without broker |
| `requestSpecificationForUnauthorized()` | Test 401 — passes empty JWT/CSRF |
| `requestSpecificationForNonLoggedInUser(String broker)` | Test non-logged-in state |
| `requestSpecificationWithoutBrokerNameInHeaderAndLoggedOutState()` | Open/public APIs |
| `samFlowRequestSpec()` | SAM user flow |
| `getRequestSpec(String auth)` | GET with sam/smallcase/gateway auth |
| `postRequestSpec(String auth)` | POST with sam/smallcase/gateway auth |
| `lamfFlowRequestSpec()` | LAMF/nexum-api flow |
| `smallboardSpec()` | Smallboard API |
| `checkSessionSpec()` | Broker session API (uses x-domain-token from env var) |

### Creating a new spec (only when none of the above fit)

Follow this exact pattern:
```java
public static RequestSpecification myNewSpec() {
    return new RequestSpecBuilder()
            .setBaseUri(ConfigRead.getPropertyValue("some_url"))
            .addHeader("Content-Type", "application/json")
            .addHeader("some-header", someValue)
            .build();
}
```

---

## 6. Response Assertions — ResponseAssert.java

**File:** `src/test/java/com/asserts/ResponseAssert.java`

Extends `AbstractAssert<ResponseAssert, Response>`. Use fluent chaining.

### Entry point
```java
ResponseAssert.assertThat(response)
```

### Status code methods
```java
.returns_200_OK()
.returns_201_CREATED()
.returns_204_NOCONTENT()
.returns_400_BADREQUEST()
.returns_401_UNAUTHORIZED()
.returns_403_FORBIDDEN()
.returns_404_NOTFOUND()
.returns_500_INTERNAL_SERVER_ERROR()
```

### Content/structure methods
```java
.hasHeaderApplicationJSON()           // Verifies Content-Type: application/json
.isWithinAcceptedTimeLimit()          // Verifies response time < 30 seconds
.hasMandatoryObjectsPresent("key1", "key2")  // Verifies keys exist in response.data
.hasValidSchema(IConst.SCHEMA_PATH)   // Validates JSON schema
.returnsValidErrorMessage(ApiConstants.unAuthorizedMessage)  // Checks errors[] array
.verifyResponseData("key", expectedValue)   // Verifies data.key == expectedValue
.verifyEquality(actual, expected)     // Generic equality check
```

---

## 7. Reporting and Logging

### Report logger — LogStatus.java

**File:** `src/main/java/resource/reports/LogStatus.java`

```java
LogStatus.pass("message")
LogStatus.fail("message")
LogStatus.info("message")
LogStatus.info("message", "category/broker")
LogStatus.error("message")
LogStatus.warning("message")
```

### Logging pattern in tests

Every test method MUST follow this pattern:
```java
// 1. Logger declaration (class level)
private static final Logger logger = LoggerFactory.getLogger(YourClass.class);

// 2. Before API call
logger.info("Creating request for [API name] - start " + broker);

// 3. API call with filter
Response response = given()
    .filter(new RequestLoggingFilter(captor))
    .spec(RequestSpec.someSpec())
    ...

// 4. After API call
logger.info("Creating request for [API name] - end " + broker);

// 5. Write to Extent Report (REQUIRED in every test)
writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

// 6. Assert
logger.info("Asserting response " + broker);
ResponseAssert.assertThat(response)...
```

---

## 8. Base Test Classes

### BaseTest — for Smallcase API tests
**File:** `src/test/java/com/smallcaseapi/BaseTest.java`

Handles:
- `@BeforeSuite`: Initializes ExtentReport, flushes global data map, sets base URI
- `@AfterSuite`: Flushes report and data map
- `@BeforeMethod`: Initializes `writer` (StringWriter) and `captor` (PrintStream) for request logging
- `writeRequestAndResponseInReport(request, response, tag)` — write req/res to report
- `formatAPIAndLogInReport(content)` — formats HTML for report
- `fetchLeprechaun()` — generates test user token

**Fields available to subclasses:**
```java
static protected StringWriter writer;
static protected PrintStream captor;
```

### IntegrationBaseTest — for broker/integration API tests
**File:** `src/test/java/com/integration/IntegrationBaseTest.java`

Extends `BaseTest`. Adds:
- `@BeforeMethod setupIntegration()`: Sets `broker_url` as base URI, loads x-domain-token from env vars
- `setBroker(String name)` / `getBroker()` — set/get broker context
- `getXDomainToken()` — returns token loaded from `STAGE_DOMAINTOKEN` or `PROD_DOMAINTOKEN` env var
- `logWithBroker(message, broker)` / `logWithCurrentBroker(message)` — broker-scoped logging
- `writeRequestAndResponseInReportWithBroker(request, response, description, broker)` — enhanced reporting

---

## 9. Global Data Sharing — DataToShare.java

**File:** `src/main/java/commonutils/DataToShare.java`

A `LinkedHashMap<String, Object>` singleton for sharing state between test classes (e.g., tokens set in login tests, read in subsequent tests).

```java
DataToShare.setValue("JWT" + broker, jwtToken);   // Store
DataToShare.getValue("JWT" + broker);              // Retrieve (returns Object, cast as needed)
DataToShare.flushMapData();                        // Clear all (called in BeforeSuite/AfterSuite)
DataToShare.putIfAbsent("key", value);             // Set only if not already set
```

### Pre-defined token keys
```
"JWT{broker}"       e.g. "JWTgroww"
"CSRF{broker}"      e.g. "CSRFgroww"
"samJwt"
"samCsrf"
"smallcaseJwt" / "smallcaseCsrf"
"gatewayJwt" / "gatewayCsrf"
"lspJwt" / "lspCsrf"
```

---

## 10. Writing a New Test — Step-by-Step

### Step 1: Add the endpoint to SmallcaseResource.java

```java
// src/main/java/com/smallcase/resource/SmallcaseResource.java
public static String yourNewEndpoint = "/api/v1/your/endpoint";
```

### Step 2: Add URL properties if needed

If the API uses a new base URL, add it to all four `.properties` files in `src/main/java/com/allconfig/`.

### Step 3: Add a request spec if needed

If no existing spec in `RequestSpec.java` fits, add a new method there following the pattern in Section 5. If an existing spec fits, skip this step.

### Step 4: Create the test class

**For standard Smallcase API tests** → place in `src/test/java/com/smallcaseapi/`  
**For broker/integration API tests** → place in `src/test/java/com/integration/`

#### Standard test class template (extends BaseTest)

```java
package com.smallcaseapi;

import com.asserts.ResponseAssert;
import com.smallcase.resource.ApiConstants;
import com.smallcase.resource.RequestSpec;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class YourNewApiTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(YourNewApiTest.class);

    @Test(testName = "Validate [API Name] returns 200", description = "[API Name] : validate upon valid inputs")
    @Parameters({"broker"})
    public void yourApiName_shouldReturn200(String broker) throws IOException {

        logger.info("Creating request for [API Name] API - start " + broker);
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecification(broker))
                .when()
                .get(SmallcaseResource.yourNewEndpoint);  // or .post() / .put() etc.

        logger.info("Creating request for [API Name] API - end " + broker);
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + broker);
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        logger.info("Test passed for " + broker);
    }

    @Test(testName = "Validate [API Name] returns 401 when no token", description = "[API Name] : unauthorized access")
    @Parameters({"broker"})
    public void yourApiName_shouldReturn401(String broker) throws IOException {

        logger.info("Creating request for [API Name] API with no token - start " + broker);
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .spec(RequestSpec.requestSpecificationForUnauthorized())
                .when()
                .get(SmallcaseResource.yourNewEndpoint);

        logger.info("Creating request for [API Name] API - end with no token " + broker);
        writeRequestAndResponseInReport(writer.toString(), response.prettyPrint(), broker);

        logger.info("Asserting response " + broker);
        ResponseAssert.assertThat(response)
                .returns_401_UNAUTHORIZED()
                .returnsValidErrorMessage(ApiConstants.unAuthorizedMessage);

        logger.info("Test passed for " + broker);
    }
}
```

#### Integration test class template (extends IntegrationBaseTest, uses DataProvider)

```java
package com.integration;

import com.asserts.ResponseAssert;
import com.smallcase.resource.SmallcaseResource;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class YourIntegrationTest extends IntegrationBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(YourIntegrationTest.class);

    @DataProvider(name = "broker")
    public Object[][] broker() {
        String[] brokers = {"groww", "kite", "hdfc", "iifl"};
        Object[][] data = new Object[brokers.length][1];
        for (int i = 0; i < brokers.length; i++) {
            data[i][0] = brokers[i];
        }
        return data;
    }

    @Test(dataProvider = "broker",
            testName = "Validate [API Name]",
            description = "[API Name] : validate upon valid inputs")
    public void yourApiName_shouldReturn200(String broker) {

        setBroker(broker);
        logWithCurrentBroker("Starting [API Name] test");

        // Build payload using Map
        Map<String, Object> payload = new HashMap<>();
        payload.put("brokerName", broker);
        payload.put("options", new HashMap<>());

        // API call
        Response response = given()
                .filter(new RequestLoggingFilter(captor))
                .header("Content-Type", "application/json")
                .header("x-domain-token", getXDomainToken())
                .body(payload)
                .when()
                .post(SmallcaseResource.yourNewEndpoint);

        logWithCurrentBroker("Response: " + response.getStatusCode());

        // Assertions
        ResponseAssert.assertThat(response)
                .returns_200_OK()
                .hasHeaderApplicationJSON()
                .isWithinAcceptedTimeLimit();

        // Write to report
        writeRequestAndResponseInReportWithBroker(writer.toString(), response.prettyPrint(), "Your Test", broker);

        logWithCurrentBroker("Test completed successfully");
    }
}
```

### Step 5: Create a payload class (if test needs a request body, and payload is complex or reused)

Place in `src/test/java/com/smallcaseapi/payload/`.

```java
package com.smallcaseapi.payload;

import org.json.simple.JSONObject;

public class YourPayload {

    public static String validPayload() {
        return "{ \"key\": \"value\" }";
    }

    public static JSONObject invalidPayload() {
        JSONObject json = new JSONObject();
        json.put("wrong_key", "value");
        return json;
    }
}
```

For simple one-off payloads (especially in integration tests), use a `Map<String, Object>` directly in the test method — no separate payload class needed.

### Step 6: Add the test to a TestNG XML suite

TestNG XMLs are in `config/`. Add your class to the relevant suite or create a new one.

#### Adding to an existing suite
```xml
<!-- config/build_sanity.xml -->
<classes>
    <class name="com.smallcaseapi.YourNewApiTest"/>
</classes>
```

#### With a broker parameter
```xml
<test name="Test on Groww">
    <parameter name="broker" value="groww"/>
    <classes>
        <class name="com.smallcaseapi.YourNewApiTest"/>
    </classes>
</test>
```

#### Creating a new suite file
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="YourSuite" verbose="1">
    <listeners>
        <listener class-name="resource.resource.ListenerClass"/>
    </listeners>
    <test name="Your Test - Staging">
        <parameter name="broker" value="groww"/>
        <classes>
            <class name="com.smallcaseapi.YourNewApiTest"/>
        </classes>
    </test>
</suite>
```

---

## 11. Payload Patterns

### Pattern 1: Inline string (simple payloads)
```java
.body("{ \"scid\": \"" + scid + "\" }")
```

### Pattern 2: JSONObject (when passing an object)
```java
JSONObject json = new JSONObject();
json.put("key", "value");
.body(json)
```

### Pattern 3: HashMap (preferred for integration tests with nested payloads)
```java
Map<String, Object> payload = new HashMap<>();
payload.put("brokerName", broker);
payload.put("options", new HashMap<>());
Map<String, String> context = new HashMap<>();
context.put("requestId", "abc123");
payload.put("context", context);
.body(payload)
```

### Pattern 4: Separate payload class (for reused payloads)
```java
// In test:
.body(YourPayload.validPayload())

// Payload class:
public static String validPayload() {
    return "{ \"key\": \"value\" }";
}
```

---

## 12. Naming Conventions

### Test methods
```
{apiName}_shouldReturn{StatusCode}
```
Examples:
- `removeWatchlist_shouldReturn200`
- `removeWatchlist_shouldReturn400`
- `removeWatchlist_shouldReturn401`
- `testAmoActiveHours` (for integration tests using DataProvider)

### Test `testName` and `description` annotations
```java
@Test(testName = "To validate [API name] API", 
      description = "[API name] API : [what scenario is being tested]")
```

### Logger declaration
```java
private static final Logger logger = LoggerFactory.getLogger(YourClass.class);
```

### Endpoint variables in SmallcaseResource
```java
public static String camelCaseEndpointName = "/path/to/endpoint";
```

### Config property keys
```
lowercase_with_underscores
```
Examples: `broker_url`, `auth_url`, `client_id`

---

## 13. ApiConstants.java — Error Messages

**File:** `src/main/java/com/smallcase/resource/ApiConstants.java`

Use constants from this class in assertions instead of hardcoded strings:
```java
ApiConstants.unAuthorizedMessage
```

---

## 14. Test Data Files

| File | Location | Usage |
|------|----------|-------|
| JSON Schemas | `src/main/java/resource/testData/JSONSchemas/` | `ResponseAssert.hasValidSchema(path)` |
| Excel data | `src/main/java/resource/testData/*.xlsx` | `ExcelRead.getPayload(path, sheet)` |
| JSON configs | `src/main/java/resource/testData/*.json` | `ReadJSON.readJsonAndGetAsString(path, key)` |

Path constants are in `commonutils.IConst`.

---

## 15. Checklist for Adding a New API Test

- [ ] Add endpoint path to `SmallcaseResource.java`
- [ ] Add base URL to all 4 `.properties` files (if new URL)
- [ ] Add request spec to `RequestSpec.java` (if no existing spec fits)
- [ ] Create test class in correct package (`com.smallcaseapi` or `com.integration`)
- [ ] Extend the correct base class (`BaseTest` or `IntegrationBaseTest`)
- [ ] Add `private static final Logger logger = LoggerFactory.getLogger(YourClass.class);`
- [ ] Use `filter(new RequestLoggingFilter(captor))` on every API call
- [ ] Call `writeRequestAndResponseInReport(...)` in every test method
- [ ] Use `ResponseAssert.assertThat(response)` for fluent assertions
- [ ] Use `@Parameters({"broker"})` for parametrized tests OR `@DataProvider` for integration tests
- [ ] Create payload class in `payload/` if body is complex or reused (otherwise inline Map or string is fine)
- [ ] Add test class to appropriate TestNG XML in `config/`

---

## 16. Environment Variables Used in Tests

| Variable | Where used |
|----------|-----------|
| `STAGE_DOMAINTOKEN` | `IntegrationBaseTest.getXDomainTokenFromSecrets()` — staging broker API token |
| `PROD_DOMAINTOKEN` | `IntegrationBaseTest.getXDomainTokenFromSecrets()` — production broker API token |
| `collectionSBStageAuth` | `RequestSpec.smallboardSpec()` — staging smallboard auth |
| `collectionSBProdAuth` | `RequestSpec.smallboardSpec()` — production smallboard auth |
| `pushRebalance_StageJwt` | `RequestSpec.pushRebalanceSpec()` |
| `pushRebalance_ProdJwt` | `RequestSpec.pushRebalanceSpec()` |
| `scb_stage_domain_token` | `RequestSpec.checkSessionSpec()` |
| `scb_prod_domain_token` | `RequestSpec.checkSessionSpec()` |
