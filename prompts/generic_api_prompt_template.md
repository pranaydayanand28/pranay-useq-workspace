# Generic API Automation Prompt Template

## Title
[Add your prompt title here - e.g., "Automate [API_NAME] API"]

## Description

You are a Backend QA Automation Engineer. You need to follow the instructions and automate the API.
This Framework has already folder structure and all setup done. You just need to follow instructions and make the changes.

### API Details
- **API Resource**: `[API_ENDPOINT_PATH]`
- **Method**: [GET/POST/PUT/DELETE/PATCH]
- **Description**: [Brief description of what this API does]

### Environment URLs
- **Staging URL**: `[STAGING_BASE_URL]`
- **Production URL**: `[PRODUCTION_BASE_URL]`

### Headers Required
```
[HEADER_NAME]: [HEADER_VALUE]
[ADDITIONAL_HEADERS_IF_NEEDED]
```

### Request Payload
```json
{
  "REQUEST_BODY_STRUCTURE": "AS_PER_API_SPECIFICATION"
}
```

### Response Structure
```json
{
  "RESPONSE_STRUCTURE": "AS_PER_API_SPECIFICATION"
}
```

### Test Parameters
- **Parameter Name**: [PARAMETER_NAME]
- **Possible Values**: [POSSIBLE_VALUES]
- **Source**: TestNG XML file

## Instructions

### Step 1 - Configuration Setup
- Create/update variable `[VARIABLE_NAME]` in properties file based on staging and production information
- Ensure proper environment-specific configurations

### Step 2 - Package and Class Structure
- **Choose appropriate package based on API type:**
  - **Smallcase API** → Create in `com.smallcaseapi` package
  - **MF (Mutual Fund) API** → Create in `com.mutualfund` package  
  - **Integration API** → Create in `com.integration` package
  - **OTP Service API** → Create in `com.otpservice` package
  - **Other APIs** → Create in appropriate package following existing structure
- Create Java class `[CLASS_NAME]` extending appropriate base class:
  - **Integration APIs** → Extend `IntegrationBaseTest`
  - **Other APIs** → Extend `CommonBaseTest` or appropriate base class
- Follow existing naming conventions

### Step 3 - API Resource Addition
- Add API resource in appropriate resource class following existing pattern:
  - **Smallcase APIs** → Add to `SmallcaseResource` class
  - **MF APIs** → Add to `MFResource` class
  - **Other APIs** → Add to appropriate resource class
- Use exact format as other resources (no modifications)

### Step 4 - Test Implementation
- Implement test method with `@Test` and `@Parameters` annotations
- Use proper TestNG parameterization
- Follow existing test structure and patterns for the specific package

### Step 5 - Request Specification
- Use existing request specifications from the framework:
  - **Smallcase APIs** → Use `RequestSpec` from `com.smallcase.resource`
  - **MF APIs** → Use appropriate request spec
  - **Integration APIs** → Use `RequestSpec` from `com.smallcase.resource`
- Apply proper headers and authentication as needed

### Step 6 - Payload Handling
- Create payload in Map format following existing patterns
- Use proper data types and structure
- Include all required fields and context
- Handle parameterization from TestNG XML

### Step 7 - Response Assertions
- Use existing response assertion utilities:
  - **ResponseAssert** class for basic assertions
  - **ResponseSpec** for response validation
  - Custom assertions as needed
- Add appropriate status code, header, and response time validations

### Step 8 - Logging and Reporting
- Add comprehensive logging using appropriate logging methods:
  - **Integration APIs** → Use `logWithCurrentBroker()` method
  - **Other APIs** → Use appropriate logging methods from base class
- Include request and response logging
- Add proper error handling and logging

### Step 9 - Parameterization
- Use TestNG `@Parameters` annotation for test parameters
- Ensure parameters are properly passed from XML file
- Handle parameter validation and error cases
- Create appropriate TestNG XML file in config folder

### Step 10 - Response Processing and Validation
- Process response data as needed
- Add business logic validation if required
- Format data for reporting
- Add data consistency checks if applicable

### Step 11 - Error Handling and Reporting
- Implement proper error handling
- Add detailed failure reporting
- Include request/response details in failure messages
- Ensure test fails appropriately on validation errors

## TestNG XML Configuration

### XML File Structure
```xml
<?xml version="1.0" encoding="UTF-8"?>
<suite name="[SUITE_NAME]">
    <test name="[TEST_NAME]">
        <parameter name="[PARAMETER_NAME]" value="[PARAMETER_VALUE]"/>
        <classes>
            <class name="com.[PACKAGE_NAME].[CLASS_NAME]"/>
        </classes>
    </test>
</suite>
```

### Parameter Values
- **Staging**: `[STAGING_VALUES]`
- **Production**: `[PRODUCTION_VALUES]`

## Assertions and Validations

### Basic Assertions (Use ResponseAssert)
- Status code validation (200 OK, 201 Created, etc.)
- Response structure validation
- Header validation
- Response time validation
- Content type validation

### Business Logic Validations
- [ADD_SPECIFIC_BUSINESS_VALIDATIONS]
- [ADD_DATA_CONSISTENCY_CHECKS]
- [ADD_ERROR_SCENARIO_VALIDATIONS]

## Reporting Requirements

### Report Content
- Request details (headers, payload, URL)
- Response details (status, body, headers)
- Validation results
- Error details (if any)
- Performance metrics

### Logging Requirements
- Test start/end logging
- Parameter logging
- Request/response logging
- Validation result logging
- Error logging

## Example Usage

### Test Method Structure
```java
@Test
@Parameters({"PARAMETER_NAME"})
public void testApiName(String parameterValue) {
    // Test implementation
}
```

### Payload Creation
```java
Map<String, Object> payload = new HashMap<>();
payload.put("FIELD1", VALUE1);
payload.put("FIELD2", VALUE2);
```

### Request Specification Usage
```java
// For Smallcase/Integration APIs
Response response = given()
    .spec(RequestSpec.getRequestSpec())
    .body(payload)
    .when()
    .post(API_RESOURCE);

// For MF APIs
Response response = given()
    .spec(MFRequestSpec.getRequestSpec())
    .body(payload)
    .when()
    .post(API_RESOURCE);
```

### Response Assertion Usage
```java
ResponseAssert.assertThat(response)
    .returns_200_OK()
    .hasHeaderApplicationJSON()
    .isWithinAcceptedTimeLimit();
```

## Notes
- Follow existing framework patterns and conventions
- Ensure proper error handling and logging
- Add comprehensive reporting
- Test both positive and negative scenarios
- Include proper cleanup if needed

## Checklist
- [ ] Configuration setup completed
- [ ] Appropriate package and class structure created
- [ ] API resource added to correct resource class
- [ ] Test method implemented with proper annotations
- [ ] Request specification applied correctly
- [ ] Payload handling implemented
- [ ] Response assertions added using ResponseAssert
- [ ] Logging and reporting added
- [ ] Parameterization working
- [ ] Response processing implemented
- [ ] Validation logic added (if needed)
- [ ] Error handling implemented
- [ ] TestNG XML configured
- [ ] All assertions working
- [ ] Reporting requirements met

---
*Template Version: 1.0*
*Created: [Date]*
*Author: [Your Name]*
*Last Updated: [Date]*
