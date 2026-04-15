# JWE Token Authentication - Implementation Status

## Overview

This document covers the JWE (JSON Web Encryption) token authentication test implementation for the OTP service. All four phases are now complete. The implementation covers auth token generation via JWE tokens for phone and email, validation/error scenarios, and mutual exclusivity enforcement.

## Implementation Phases

| Phase      | Status          | Scope                                                                            | Completion Date |
|------------|-----------------|----------------------------------------------------------------------------------|-----------------|
| **Phase 1** | ✅ **Completed** | Basic JWE token tests — phone auth token generation + validation/error scenarios | 2025-12-15      |
| **Phase 2** | ✅ **Completed** | Dynamic JWE token generation utility                                             | 2025-12-15      |
| **Phase 3** | ✅ **Completed** | Complete OTP verification tests for JWE token based                              | 2026-02-19      |
| **Phase 4** | ✅ **Completed** | Advanced edge cases and validation                                               | 2026-02-24      |

---

## Phase 1: Basic JWE Token Tests ✅

### Status: **Completed**

### Overview
Phase 1 implemented JWE token authentication tests for the `/internal/auth/token` endpoint, covering phone-based auth token generation and all validation/error scenarios.

### Files Created/Modified

1. **Test Class:**
   - `src/test/java/com/otpservice/otpVerification/JweTokenAuth.java`

2. **TestNG Suite:**
   - `config/jwe_token_auth.xml` — Standalone suite for JWE token auth tests

3. **Updated Files:**
   - `config/otp_service_stage.xml` — Added JWE token tests to main suite

### Test Cases Implemented

**Success Scenarios:**

1. **testValidJweTokenPhone**
   - Validates successful auth token generation with JWE token for regular phone number
   - Generates phone via `PhoneNoTestUser.generatePhoneNumber()`, country code from config (`phoneCountryCode`)
   - Verifies 200 response with `authToken` and `recaptchaType` fields

2. **testValidJweTokenWithTestAccount**
   - Validates successful auth token generation with JWE token for test account
   - Uses dynamically generated phone number, country code from config (`internalphoneCountryCode`, i.e. `+SC`)
   - Verifies 200 response with `authToken` field

**Failure Scenarios:**

3. **testInvalidJweToken**
   - Validates 400 error for invalid/malformed JWE token
   - Uses hardcoded invalid token: `invalid.jwe.token.string.gibberish`
   - Verifies response contains `errors`

4. **testEmptyJweToken**
   - Validates 400 error for empty `jweToken` field
   - Sends `{"jweToken": ""}`
   - Verifies validation error response

5. **testMissingJweToken**
   - Validates 400 error for empty payload (no `jweToken`, `phone`, or `email`)
   - Sends `{}`
   - Verifies validation error response

6. **testPhoneJweTokenWithPhone**
   - Validates mutual exclusivity — `jweToken` (phone payload) + `phone` should be rejected
   - Verifies 400 validation error response

7. **testEmailJweTokenWithEmail**
   - Validates mutual exclusivity — `jweToken` (email payload) + `email` should be rejected
   - Verifies 400 validation error response

### Technical Implementation

- **Base Class:** Extends `OtpServiceBaseTest`
- **Package:** `com.otpservice.otpVerification`
- **Request Pattern:** RestAssured with `RequestSpec.secureAuthSamLogin()`
- **Endpoint:** `SmallcaseResource.secureAuth`
- **Assertions:** `ResponseAssert` for consistent validation
- **Logging:** Integrated with ExtentReports

### Execution

```bash
# Run JWE token auth tests only
mvn -Denv=staging -DsuiteXmlFiles=config/jwe_token_auth.xml clean test

# Run full OTP service suite
mvn -Denv=staging -DsuiteXmlFiles=config/otp_service_stage.xml clean test
```

### Required Environment Variables

| Variable | Purpose |
|----------|---------|
| `otpServiceClientSecret` | Used in `@BeforeSuite` to delete client state before suite |
| `jwe_encryption_public_key_stage` | RSA public key for JWE token encryption |

---

## Phase 2: Dynamic JWE Token Generation Utility ✅

### Status: **Completed**

### Overview
Phase 2 implemented `JweTokenUtil` — a utility class to dynamically generate JWE tokens using RSA public key encryption, eliminating hardcoded tokens.

### Files Created/Modified

1. **Utility Class:**
   - `src/main/java/commonutils/JweTokenUtil.java` (227 lines)

2. **Configuration Files Updated:**
   - `src/main/java/com/allconfig/configAPI.properties` — Added `jwe_encryption_public_key`
   - `src/main/java/com/allconfig/configAPIDEV.properties` — Added `jwe_encryption_public_key`
   - `src/main/java/com/allconfig/configAPIPreview.properties` — Added `jwe_encryption_public_key`
   - `src/main/java/com/allconfig/configAPIPROD.properties` — Added `jwe_encryption_public_key`

3. **Dependencies Added (`pom.xml`):**

```xml
<!-- JWE/JWS Support -->
<dependency>
    <groupId>com.nimbusds</groupId>
    <artifactId>nimbus-jose-jwt</artifactId>
    <version>9.37.3</version>
</dependency>

<!-- Bouncy Castle for RSA-OAEP crypto operations -->
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.70</version>
</dependency>
```

### Token Generation Methods

```java
// Phone authentication (added Phase 2)
String jweToken = JweTokenUtil.generateJweToken(phoneNumber, phoneCountryCode);

// Email authentication (added Phase 3)
String jweToken = JweTokenUtil.generateJweToken(email);

// Custom payload
Map<String, String> payload = new HashMap<>();
payload.put("phone", "1234567890");
payload.put("phoneCountryCode", "+91");
String jweToken = JweTokenUtil.generateJweTokenFromPayload(payload);
```

### Technical Specifications

**Encryption Algorithms:**
- **Key Encryption:** RSA-OAEP-256 (RSAES-OAEP with SHA-256)
- **Content Encryption:** A256GCM (AES-256-GCM)

**JWE Token Format:**
- **Structure:** `<header>.<encrypted_key>.<iv>.<ciphertext>.<tag>`
- **Header:**
  ```json
  { "alg": "RSA-OAEP-256", "enc": "A256GCM" }
  ```

**Supported Payload Formats:**
```json
{ "phone": "9999999999", "phoneCountryCode": "+91" }
```
```json
{ "email": "user@example.com" }
```

**Public Key Configuration:**
- Property: `jwe_encryption_public_key` (resolves to environment-specific env var name)
- Format: Base64-encoded RSA public key (PEM with `BEGIN/END PUBLIC KEY` headers, or raw X.509 DER)
- Key is cached after first load for performance

### Key Features

- ✅ No manual token replacement needed
- ✅ Supports dynamic phone numbers and emails
- ✅ Handles both PEM and X.509 DER key formats
- ✅ Public key cached after first load
- ✅ Clear error messages for missing/invalid keys

---

## Phase 3: Complete OTP Verification Tests for JWE Token Based ✅

### Status: **Completed** (2026-02-19)

### Overview
Phase 3 extended JWE token authentication to support email-based auth token generation, adding a new success test and the corresponding utility method in `JweTokenUtil`.

### Files Modified

1. **Test Class:**
   - `src/test/java/com/otpservice/otpVerification/JweTokenAuth.java` — Added `testValidJweTokenEmail`

2. **Utility Class:**
   - `src/main/java/commonutils/JweTokenUtil.java` — Added `generateJweToken(String email)`

### Test Case Added

**testValidJweTokenEmail**
- Validates successful auth token generation using a JWE token with email payload
- Generates email via `EmailTestUser.generateRandomEmail()`
- Generates JWE token via `JweTokenUtil.generateJweToken(email)` → payload: `{"email": "..."}`
- Verifies 200 response with `authToken` and `recaptchaType` fields

### Additional Required Environment Variable

| Variable | Purpose |
|----------|---------|
| `stageSecret` | Used by `EmailTestUser.generateRandomEmail()` to register test email via internal API |

---

## Phase 4: Advanced Edge Cases and Validation ✅

### Status: **Completed** (2026-02-24)

### Overview
Phase 4 added cross-field mutual exclusivity tests — validating that a JWE token generated for one identifier type (phone/email) cannot be combined with the other identifier type in the same request.

### Files Modified

1. **Test Class:**
   - `src/test/java/com/otpservice/otpVerification/JweTokenAuth.java` — Added 2 new tests, renamed existing mutual exclusivity tests for clarity

### Test Cases Added

**testPhoneJweTokenWithEmail**
- Validates that sending a phone-payload JWE token together with `email` field is rejected
- Sends `{"jweToken": <phone JWE>, "email": "test@example.com"}`
- Verifies 400 response with `errors`

**testEmailJweTokenWithPhone**
- Validates that sending an email-payload JWE token together with `phone`/`phoneCountryCode` fields is rejected
- Sends `{"jweToken": <email JWE>, "phone": "...", "phoneCountryCode": "+91"}`
- Verifies 400 response with `errors`

### Existing Mutual Exclusivity Tests Renamed

| Old Name | New Name |
|----------|----------|
| `testJweTokenWithPhone` | `testPhoneJweTokenWithPhone` |
| `testJweTokenWithEmail` | `testEmailJweTokenWithEmail` |

---

## Complete Test Suite Summary

### JweTokenAuth.java — 10 Test Cases

| # | Test Method | Type | Scenario |
|---|-------------|------|----------|
| 1 | `testValidJweTokenPhone` | ✅ Success | Phone JWE → authToken (200) |
| 2 | `testValidJweTokenWithTestAccount` | ✅ Success | Phone JWE (+SC) → authToken (200) |
| 3 | `testValidJweTokenEmail` | ✅ Success | Email JWE → authToken (200) |
| 4 | `testInvalidJweToken` | ❌ Failure | Malformed token → 400 |
| 5 | `testEmptyJweToken` | ❌ Failure | Empty jweToken → 400 |
| 6 | `testMissingJweToken` | ❌ Failure | Empty payload → 400 |
| 7 | `testPhoneJweTokenWithPhone` | ❌ Failure | Phone JWE + phone (mutual exclusivity) → 400 |
| 8 | `testEmailJweTokenWithEmail` | ❌ Failure | Email JWE + email (mutual exclusivity) → 400 |
| 9 | `testPhoneJweTokenWithEmail` | ❌ Failure | Phone JWE + email (cross exclusivity) → 400 |
| 10 | `testEmailJweTokenWithPhone` | ❌ Failure | Email JWE + phone (cross exclusivity) → 400 |

**Success: 3 | Failure/Validation: 7 | Total: 10**

### Execution

```bash
# Run JWE token auth tests only
mvn -Denv=staging -DsuiteXmlFiles=config/jwe_token_auth.xml clean test

# Run single test
mvn test -Dsurefire.suiteXmlFiles=config/jwe_token_auth.xml -Dtest="com.otpservice.otpVerification.JweTokenAuth#testValidJweTokenEmail"

# Run full OTP service suite
mvn -Denv=staging -DsuiteXmlFiles=config/otp_service_stage.xml clean test
```

### All Required Environment Variables

| Variable | Used In | Purpose |
|----------|---------|---------|
| `otpServiceClientSecret` | `@BeforeSuite` | Delete client state before suite |
| `jwe_encryption_public_key_stage` | `JweTokenUtil` | RSA public key for JWE encryption |
| `stageSecret` | `EmailTestUser.generateRandomEmail()` | Register test email via internal API |

---
## Known Issues

**Mutual Exclusivity Test (testJweTokenWithPhone)**
- **Issue:** Test expects 400 error when both `jweToken` and `phone` are provided, but currently returns 200 OK
- **Status:** Discuss with devs to return errors for these tests on OTP service side
- **Note:** Test are failing currently, but will be passed when the expected errors implemented

---

## Technical Architecture

### JWE Token Flow

```
Test → JweTokenUtil.generateJweToken(phone, countryCode)
              OR JweTokenUtil.generateJweToken(email)
  → Build payload JSON {"phone":..., "phoneCountryCode":...}
              OR {"email":...}
  → Encrypt with RSA public key (RSA-OAEP-256 + A256GCM)
  → POST /internal/auth/token { "jweToken": "..." }
  → OTP Service decrypts with private key
  → Returns { "data": { "authToken": "...", "recaptchaType": "..." } }
```

### Key Components

| Component | File | Purpose |
|-----------|------|---------|
| `JweTokenUtil` | `src/main/java/commonutils/JweTokenUtil.java` | Token generation (phone + email) |
| `JweTokenAuth` | `src/test/java/com/otpservice/otpVerification/JweTokenAuth.java` | 10 test cases |
| `jwe_token_auth.xml` | `config/jwe_token_auth.xml` | Standalone test suite |
| Config properties | `src/main/java/com/allconfig/*.properties` | Public key env var name per environment |

---

## Related Documentation

- [Phase 1 Plan](jwe-token-tests-phase1-plan.md)
- [Phase 2 Plan](jwe-token-tests-phase2-plan.md)
- [Implementation Summary](jwe-token-tests-implementation-summary.md)

---

**Last Updated:** 2026-02-24
**Status:** All Phases Completed ✅