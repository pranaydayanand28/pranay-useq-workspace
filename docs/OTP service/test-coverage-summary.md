# OTP Service Test Coverage Summary

## Overview
This document summarizes the test cases added for OTP Service client management API testing.

---

## Test Files

### 1. CreateClient.java

**Test Cases:**
- OTP service client creation
- OTP service client creation template validation
- Create client with WhatsApp delivery
- Create client with Email delivery
- Create client with multiple delivery methods
- Create client with rate limits
- Create client with phone country code whitelist
- Create client with phone country code blacklist
- Create client with invalid verification method
- Create client with invalid delivery method
- Create client WhatsApp without config
- Create client Email without config
- Create client missing required fields
- Create client with rate limit missing default
- Create client with empty delivery methods

**Test Setup Nuances:**
- Uses `@BeforeMethod` to delete client before each test (to avoid 409 conflicts in parallel execution)
- Uses `@AfterMethod` to clean up client after each test
- Both setup/teardown methods have try-catch blocks to handle cases where client doesn't exist
- Uses `Common.getValidMinimalClientConfig()` and other helper methods from `Common.java` for test data
- Tests validate response status codes, headers, mandatory fields, and error messages

---

### 2. DeleteClient.java

**Test Cases:**
- OTP service client deletion success
- OTP service client deletion error
- Delete client and verify removal
- Delete client without clientId parameter
- Delete client with empty clientId
- Delete already deleted client

**Test Setup Nuances:**
- Uses `@BeforeClass` to create a client once before the class runs (ensures client exists for deletion tests)
- Tests are ordered with `priority` attribute (1-6)
- First test deletes the client, second test verifies 404 when deleting non-existent client
- Third test creates, deletes, and verifies removal by attempting GET
- Tests validate idempotency of delete operation
- Validates error messages for missing/invalid clientId parameters

---

### 3. GetClient.java

**Test Cases:**
- Get existing client
- Get client with SMS delivery
- Get client with multiple delivery methods
- Get client with rate limits
- Get client with feature flags
- Get non-existent client
- Get client without clientId parameter
- Get client with empty clientId
- Get client with malformed clientId

**Test Setup Nuances:**
- Uses `@BeforeMethod` to create a client before each test
- Uses `@AfterMethod` to delete client after each test
- Some tests delete and recreate client with specific configurations (e.g., multiple delivery methods, rate limits)
- Validates response structure including presence of `clientId`, `clientSecret`, `config`, and specific config sections
- Verifies presence of `smsCommsConfig`, `whatsappCommsConfig`, `emailCommsConfig` based on delivery methods
- Validates presence of rate limit configs (`countryCodeRateLimit`, `countryNameRateLimit`, `emailDomainRateLimit`)
- Validates presence of `flags` in config

---

### 4. GetClientConfig.java

**Test Cases:**
- Get public client config
- Get config for non-existent client
- Get config without clientId parameter
- Get config with empty clientId
- Get config with malformed clientId

**Test Setup Nuances:**
- Uses `@BeforeMethod` to create a client before each test
- Uses `@AfterMethod` to delete client after each test
- Uses `RequestSpec.otpServiceAuth()` instead of `otpServiceClient()` for authentication
- Explicitly sets `x-client-internal-secret` header to empty string (public endpoint)
- Endpoint is `/client/config` (different from `/client`)
- Validates response contains `flags` and specifically `enableClientSecureTokenForAllRequests` flag
- Tests public endpoint accessibility without internal secret

---

### 5. UpdateClient.java

**Test Cases:**
- Update client with minimal config changes
- Update client OTP configuration
- Update client delivery methods - add WhatsApp
- Update client SMS template
- Update client rate limits
- Update client feature flags
- Update client verification methods
- Update client missing required fields
- Update client with invalid verification method
- Update client with invalid delivery method
- Update client SMS without template
- Update client WhatsApp without config
- Update client Email without config
- Update non-existent client
- Update client rate limit missing default

**Test Setup Nuances:**
- Uses `@BeforeMethod` to create a client before each test
- Uses `@AfterMethod` to delete client after each test
- Uses PATCH method for updates
- Many tests perform GET after UPDATE to verify changes were persisted
- Validates specific config values after update (e.g., `otpDigits`, `otpSendLimit`, `otpVerifyLimit`, `otpTtl`)
- Tests update of various config sections: OTP settings, delivery methods, templates, rate limits, feature flags
- Tests validation errors for missing required fields, invalid values, and missing configs for delivery methods
- Tests special case: updating verification methods requires captcha config for each device type and verification method combination
- Tests rate limit validation: when `enforceCountryCodeRateLimitForAllRequests` is true, `countryCodeRateLimit` must have a "default" entry

---

## Common Test Infrastructure

### Common.java Helper Methods
- `getValidMinimalClientConfig()` - Creates minimal valid client config with SMS delivery
- `getClientConfigWithWhatsapp()` - Creates client config with WhatsApp delivery
- `getClientConfigWithEmail()` - Creates client config with Email delivery
- `getClientConfigWithMultipleDeliveryMethods()` - Creates client config with SMS, WhatsApp, and Email
- `getClientConfigWithRateLimits()` - Creates client config with country code, country name, and email domain rate limits
- `getClientConfigWithWhitelists()` - Creates client config with phone country code whitelist
- `getClientConfigWithBlacklists()` - Creates client config with phone country code blacklist
- `getMinimalUpdatePayload()` - Creates minimal update payload with different OTP settings
- `getValidUpdatePayload()` - Creates valid update payload (same as minimal config)

### Test Base Class
- All test classes extend `OtpServiceBaseTest`
- Uses `RequestLoggingFilter` for request/response logging
- Uses `ResponseAssert` for response validation
- Uses `writeRequestAndResponseInReport()` for test reporting
- Uses `commonutils.ConfigRead.getPropertyValue("otp_service_qa_client")` for client ID

### Request Specifications
- `RequestSpec.otpServiceClient()` - For client management endpoints (create, update, delete, get)
- `RequestSpec.otpServiceAuth()` - For authentication/public endpoints (get config)

### Response Assertions
- Status code validation (200, 400, 404)
- Content-Type header validation (application/json)
- Response time validation
- Mandatory fields validation
- Error message validation
- Response structure validation

---

## Test Coverage Summary

**Total Test Cases:** 50

**By File:**
- CreateClient.java: 15 test cases
- DeleteClient.java: 6 test cases
- GetClient.java: 9 test cases
- GetClientConfig.java: 5 test cases
- UpdateClient.java: 15 test cases

**By Type:**
- Positive Tests: ~30
- Negative Tests: ~20

**Coverage Areas:**
- Client CRUD operations (Create, Read, Update, Delete)
- Multiple delivery methods (SMS, WhatsApp, Email)
- Rate limiting configurations
- Feature flags
- Validation and error handling
- Edge cases (missing fields, invalid values, malformed inputs)
- Public vs internal endpoints

---

## Additional Notes

1. **Test Isolation:** Each test class uses setup/teardown methods to ensure test isolation and avoid conflicts in parallel execution.

2. **Client ID Management:** All tests use a common client ID from configuration (`otp_service_qa_client`), which is created/deleted as needed.

3. **Test Data:** Test data is centralized in `Common.java` to ensure consistency and maintainability.

4. **Error Validation:** Negative tests validate both status codes and specific error messages to ensure proper error handling.

5. **Response Validation:** Positive tests validate response structure, mandatory fields, and specific config values to ensure API correctness.

6. **Public Endpoint:** `GetClientConfig` tests a public endpoint that doesn't require internal secret, demonstrating different authentication requirements.

7. **Update Verification:** Update tests often perform GET operations after UPDATE to verify persistence of changes.

8. **Special Validations:** Tests cover special business rules like:
   - SMS template is always required when SMS delivery is enabled
   - WhatsApp/Email require their respective configs when enabled
   - Rate limit maps must have a "default" entry when country code rate limiting is enforced
   - Verification method updates require captcha config for all device type and verification method combinations

