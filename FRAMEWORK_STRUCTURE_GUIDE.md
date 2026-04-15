# Rest Assured Automation Framework - Structure Guide

## Overview
This is a comprehensive Rest Assured automation framework built with Java, TestNG, Maven, and ExtentReports for testing Smallcase APIs. The framework supports multiple environments (staging, development, production) and various test execution flows with CI/CD integration.

## Technology Stack
- **Java 1.8+**
- **Maven** - Build and dependency management
- **TestNG** - Test framework
- **Rest Assured 5.0.1** - API testing library
- **ExtentReports 5.0.9** - Test reporting
- **SLF4J & Logback** - Logging
- **Apache POI** - Excel file handling
- **Jackson & Gson** - JSON processing
- **Hamcrest & AssertJ** - Assertions
- **Lombok** - Code generation
- **GitHub Actions** - CI/CD Pipeline
- **YAML Configuration** - Workflow management

## Project Structure

### 📁 Root Level
```
sc-qa-restassured/
├── 📄 pom.xml                            # Maven configuration
├── 📄 README.md                          # Project documentation
├── 📄 FRAMEWORK_STRUCTURE_GUIDE.md       # Detailed framework guide
├── 📁 .github/                           # GitHub Actions workflows
│   └── 📁 workflows/                     # CI/CD pipeline configurations
│       ├── 📄 dynamic-dispatcher.yml     # Dynamic test execution
│       ├── 📄 scheduled-jobs.yml         # Scheduled cron jobs
│       ├── 📄 dev-flow-POC.yml           # Development flow POC
│       └── 📄 prod.yml                   # Production workflows
├── 📁 config/                            # TestNG suite configurations
│   ├── 📄 build_sanity.xml               # Build sanity tests
│   ├── 📄 create_test_users.xml          # Test user creation
│   ├── 📄 end2end_flow.xml               # End-to-end test flow
│   ├── 📄 extent_config.xml              # ExtentReports configuration
│   ├── 📄 filter_and_delete_client.xml   # Client management tests
│   ├── 📄 health_check.xml               # Health check tests
│   ├── 📄 main_runner.xml                # Main test runner
│   ├── 📄 mf_experience.xml              # Mutual Fund experience tests
│   ├── 📄 multi_asset_flow.xml           # Multi-asset flow tests
│   ├── 📄 multi_asset_orders_listing.xml # Multi-asset order listing
│   ├── 📄 multi_asset_search.xml         # Multi-asset search tests
│   ├── 📄 order_flow_with_sip.xml        # Order flow with SIP
│   ├── 📄 order_flow_without_sip.xml     # Order flow without SIP
│   ├── 📄 otp_service_stage.xml          # OTP service staging tests
│   ├── 📄 page_navigation_flow.xml       # Page navigation tests
│   ├── 📄 prod_sam_flow.xml              # Production SAM flow
│   ├── 📄 rebalance_flow.xml             # Rebalance flow tests
│   ├── 📄 sam_bam_connect.xml            # SAM-BAM connection tests
│   ├── 📄 smallcase_profile.xml          # Smallcase profile tests
│   ├── 📄 smoke_test.xml                 # Smoke tests
│   ├── 📄 stage_sam_flow.xml             # Staging SAM flow
│   └── 📄 test_runner.xml                # General test runner
└── 📁 src/
    ├── 📁 main/                          # Main source code
    └── 📁 test/                          # Test source code
```

### 📁 Main Folder Structure (`src/main/`)

#### 🔧 **Java Package: `com.allconfig`**
**Purpose**: Environment-specific configuration files
- `configAPI.properties` - Staging environment config
- `configAPIDEV.properties` - Development environment config  
- `configAPIPreview.properties` - Preview environment config
- `configAPIPROD.properties` - Production environment config

**Key Configuration Properties**:
```properties
auth_url = https://auth-stag.smallcase.com
smallcaseapi_url = https://api-stag.smallcase.com
gatewayapi_url = https://gatewayapi-stag.smallcase.com
otp_url = https://otp-stag.smallcase.com
client_id = smallcase-platform
phoneCountryCode = +91
```

#### 🔧 **Java Package: `com.otpService.resource`**
**Purpose**: OTP Service API resources and specifications
- Request specifications for OTP service
- Resource endpoints for OTP operations

#### 🔧 **Java Package: `com.smallcase.resource`**
**Purpose**: Smallcase API resources and specifications
- Request specifications for different API calls
- Resource endpoints and constants
- POJO classes for request/response mapping

#### 🔧 **Java Package: `commonutils`**
**Purpose**: Reusable utility classes and helper methods

| Class | Purpose |
|-------|---------|
| `AssertActions.java` | Custom assertion methods |
| `ConfigRead.java` | Configuration file reader |
| `CurrentDate.java` | Date/time utilities |
| `DataToShare.java` | Global data sharing (Map-based) |
| `EmailTestUser.java` | Email test user management |
| `ExcelRead.java` | Excel file reading utilities |
| `ExcelWrite.java` | Excel file writing utilities |
| `GetSmallcaseID.java` | Smallcase ID generation |
| `IConst.java` | Framework constants |
| `JsonfetchUtil.java` | JSON data extraction |
| `JsonPathFinder.java` | JSON path utilities |
| `ListDatatoShare.java` | List data sharing |
| `PhoneAndEmailGenerator.java` | Test data generation |
| `PhoneNoTestUser.java` | Phone number utilities |
| `ReadJSON.java` | JSON file reading |
| `RemoveArrayBrackets.java` | String manipulation |
| `TextFileReader.java` | Text file reading |
| `TextFileWriter.java` | Text file writing |
| `WaitMethod.java` | Wait utilities |

#### 🔧 **Java Package: `resource`**
**Purpose**: Framework resources and reporting

**Reports Package (`resource.reports`)**:
- `ExtentManager.java` - ExtentReports thread management
- `ExtentReport.java` - Report initialization and configuration
- `LogStatus.java` - Logging utilities for reports

**Resource Package (`resource.resource`)**:
- `CommonListener.java` - TestNG listener for common operations
- `ListenerClass.java` - TestNG listener for report generation
- `MyTransformer.java` - Test data transformation
- `RetryAnalyzer.java` - Test retry mechanism

**Test Data Package (`resource.testData`)**:
- `CreatedSmallcasePicks.json` - Test data for smallcase picks
- `createPayload.json` - API payload templates
- `JSONSchemas/` - JSON schema validation files (25 files)
- `PayloadParamSheet.xlsx` - Parameter data
- `QueryParams.json` - Query parameters
- `scid_prod.xlsx` - Production smallcase IDs
- `scid_stag.xlsx` - Staging smallcase IDs
- `Stockquery.xlsx` - Stock query data
- `SubscriptionFlowData.xlsx` - Subscription flow data

### 📁 Test Folder Structure (`src/test/`)

#### 🧪 **Java Package: `com.asserts`**
**Purpose**: Custom assertion classes
- `ResponseAssert.java` - API response assertions

#### 🧪 **Java Package: `com.CommonBaseTest`**
**Purpose**: Common base test class for shared functionality

#### 🧪 **Java Package: `com.multiassetcollection`**
**Purpose**: Multi-asset collection testing
- `experience/MultiAssetCollection.java` - Multi-asset experience tests
- `HealthCheck.java` - Health check tests

#### 🧪 **Java Package: `com.multiassetorderlisting`**
**Purpose**: Order listing API tests
- `GroupOrdersListingAPI.java` - Group order listing tests
- `OrdersListingAPI.java` - Order listing tests

#### 🧪 **Java Package: `com.multiassetsearch`**
**Purpose**: Multi-asset search functionality tests
- `MFConfigAPI.java` - Mutual Fund configuration tests
- `MFDiscoverAPI.java` - Mutual Fund discovery tests
- `MFIncludeExclude.java` - Mutual Fund include/exclude tests
- `multiAssetUtils.java` - Multi-asset utilities
- `SCConfigAPI.java` - Smallcase configuration tests
- `SCDiscoverAPI.java` - Smallcase discovery tests
- `SCIncludeExclude.java` - Smallcase include/exclude tests
- `SearchAPI.java` - Search functionality tests
- `StockConfigAPI.java` - Stock configuration tests
- `StockDiscoverAPI.java` - Stock discovery tests
- `StockIncludeExclude.java` - Stock include/exclude tests
- `TrendingAPI.java` - Trending assets tests

#### 🧪 **Java Package: `com.mutualfund`**
**Purpose**: Mutual Fund specific tests
- `experience/` - Mutual Fund experience tests (6 files)
- `HealthCheck.java` - Health check tests
- `onboarding/CreateTransaction.java` - Transaction creation tests

#### 🧪 **Java Package: `com.otpservice`**
**Purpose**: OTP Service testing
- `Auth.java` - Authentication tests
- `Common.java` - Common OTP service tests
- `CreateClient.java` - Client creation tests
- `DeleteClient.java` - Client deletion tests
- `DeleteFilteredClients.java` - Filtered client deletion tests
- `EmailTestUser.java` - Email test user tests
- `FilterClient.java` - Client filtering tests
- `OtpServiceBaseTest.java` - Base test for OTP service
- `PhoneTestUser.java` - Phone test user tests

#### 🧪 **Java Package: `com.samBamConnect`**
**Purpose**: SAM-BAM connection tests
- `ConnectHelper.java` - Connection helper utilities
- `SamBamConnect.java` - SAM-BAM connection tests

#### 🧪 **Java Package: `com.smallboard`**
**Purpose**: Smallboard functionality tests
- `GetLeprechaun.java` - Leprechaun generation tests
- `multiassetcollection/` - Multi-asset collection tests (5 files)
- `PushRebalance.java` - Rebalance push tests

#### 🧪 **Java Package: `com.smallcaseapi`**
**Purpose**: Core Smallcase API tests

**Main API Test Classes**:
- `BaseTest.java` - Base test class with common setup
- `SmallcaseLoginAPI.java` - Login API tests
- `SmallcaseProfileAPI.java` - Profile API tests
- `DashboardAPI.java` - Dashboard API tests
- `DiscoverAPI.java` - Discovery API tests
- `Search.java` - Search API tests
- `CollectionAPI.java` - Collection API tests
- `GetCollection.java` - Collection retrieval tests
- `GetUser.java` - User data tests
- `GetWatchlistAPI.java` - Watchlist retrieval tests
- `AddToWatchlistAPI.java` - Watchlist addition tests
- `RemoveWatchlist.java` - Watchlist removal tests
- `CheckStatusAPI.java` - Status check tests
- `BlogProxyAPI.java` - Blog proxy tests
- `Highlighted.java` - Highlighted content tests
- `InvestmentInsightAPI.java` - Investment insight tests
- `LogoutAPI.java` - Logout tests
- `NotificationAPI.java` - Notification tests
- `OfferAPI.java` - Offer tests
- `PriceandChangeAPI.java` - Price and change tests
- `FundsAPI.java` - Funds API tests

**Order Flow Tests (`orderFlow/`)**:
- 18 files covering complete order flow scenarios
- Buy, sell, manage order operations
- SIP and non-SIP order flows

**SAM Flow Tests (`samflow/`)**:
- 18 files covering SAM (Smallcase Account Management) flows
- Login, authentication, and user management flows

**Payload Classes (`payload/`)**:
- 17 files containing request payload structures
- POJO classes for different API requests

**Create Smallcase Tests (`createsc/`)**:
- 5 files for smallcase creation functionality

**Broker Auth Tests (`brokerAuthCookies/`)**:
- Broker authentication and cookie management

### 📁 Configuration Files (`config/`)

#### TestNG Suite Files:
- `build_sanity.xml` - Build sanity tests
- `create_test_users.xml` - Test user creation
- `end2end_flow.xml` - End-to-end test flow
- `extent_config.xml` - ExtentReports configuration
- `filter_and_delete_client.xml` - Client management tests
- `health_check.xml` - Health check tests
- `main_runner.xml` - Main test runner
- `mf_experience.xml` - Mutual Fund experience tests
- `multi_asset_flow.xml` - Multi-asset flow tests
- `multi_asset_orders_listing.xml` - Multi-asset order listing
- `multi_asset_search.xml` - Multi-asset search tests
- `order_flow_with_sip.xml` - Order flow with SIP
- `order_flow_without_sip.xml` - Order flow without SIP
- `otp_service_stage.xml` - OTP service staging tests
- `page_navigation_flow.xml` - Page navigation tests
- `prod_sam_flow.xml` - Production SAM flow
- `rebalance_flow.xml` - Rebalance flow tests
- `sam_bam_connect.xml` - SAM-BAM connection tests
- `smallcase_profile.xml` - Smallcase profile tests
- `smoke_test.xml` - Smoke tests
- `stage_sam_flow.xml` - Staging SAM flow
- `test_runner.xml` - General test runner

### 📁 GitHub Actions Workflows (`.github/workflows/`)

#### YAML Configuration Files:

##### 1. **Dynamic Dispatcher** (`dynamic-dispatcher.yml`)
**Purpose**: Manual test execution with dynamic suite selection
- **Trigger**: `workflow_dispatch` (Manual execution)
- **Features**:
  - Suite selection from dropdown (22+ available suites)
  - Environment selection (staging, production, development, preview)
  - PR number input for tracking
  - Phone/Email users input for test user creation
- **Available Suites**:
  - `config/build_sanity.xml`
  - `config/end2end_flow.xml`
  - `config/main_runner.xml`
  - `config/multi_asset_flow.xml`
  - `config/order_flow_without_sip.xml`
  - `config/order_flow_with_sip.xml`
  - `config/otp_service_stage.xml`
  - `config/page_navigation_flow.xml`
  - `config/stage_sam_flow.xml`
  - `config/smoke_test.xml`
  - `config/test_runner.xml`
  - `config/multi_asset_search.xml`
  - `config/mf_experience.xml`
  - `config/create_test_users.xml`
  - `config/filter_and_delete_client.xml`
  - `config/prod_sam_flow.xml`
  - `config/multi_asset_orders_listing.xml`
  - `config/smallcase_profile.xml`
  - `config/rebalance_flow.xml`
  - `config/sam_bam_connect.xml`

##### 2. **Scheduled Jobs** (`scheduled-jobs.yml`)
**Purpose**: Automated cron job execution for regular testing
- **Trigger**: 
  - `push` to master branch
  - `schedule` with cron expressions
- **Cron Schedule**:
  ```yaml
  schedule:
    - cron: '30 9 * * 1-5'   # 3:00 PM IST - end2end_flow - production
    - cron: '30 10 * * 1-5'  # 3:30 PM IST - multi_asset_search - production
    - cron: '30 11 * * 1-5'  # 4:30 PM IST - stage_sam_flow - staging
    - cron: '30 14 * * 1-5'  # 7:00 PM IST - end2end_flow - staging
  ```
- **Matrix Strategy**:
  - `config/end2end_flow.xml` → production
  - `config/multi_asset_search.xml` → production
  - `config/stage_sam_flow.xml` → staging
  - `config/end2end_flow.xml` → staging
- **Timeout**: 240 minutes
- **Runner**: `staging-arc-runner-set`

##### 3. **Development Flow POC** (`dev-flow-POC.yml`)
**Purpose**: Development branch testing and POC validation
- **Trigger**: 
  - `pull_request` (opened, reopened, synchronize)
  - `workflow_dispatch` (Manual execution)
- **Features**:
  - Branch-specific execution
  - Suite selection for development testing
  - Environment configuration for dev flows

##### 4. **Production Workflows** (`prod.yml`)
**Purpose**: Production-specific test execution
- **Features**:
  - Production environment validation
  - Critical path testing
  - Performance monitoring

#### Cron Job Schedule Details:

| Time (IST) | Cron Expression | Suite | Environment | Purpose |
|------------|----------------|--------|-------------|---------|
| 3:00 PM | `30 9 * * 1-5` | end2end_flow.xml | production | Daily E2E validation |
| 3:30 PM | `30 10 * * 1-5` | multi_asset_search.xml | production | Search functionality |
| 4:30 PM | `30 11 * * 1-5` | stage_sam_flow.xml | staging | SAM flow validation |
| 7:00 PM | `30 14 * * 1-5` | end2end_flow.xml | staging | Staging E2E validation |

#### Workflow Execution Matrix:

| Workflow | Trigger | Environment | Suites | Frequency |
|----------|---------|-------------|--------|-----------|
| **Dynamic Dispatcher** | Manual | All | All 22+ suites | On-demand |
| **Scheduled Jobs** | Cron | Production/Staging | 4 core suites | Daily (Mon-Fri) |
| **Dev Flow POC** | PR/Manual | Development | Selected suites | Per PR |
| **Production** | Manual | Production | Critical suites | As needed |

#### YAML Configuration Features:

##### Environment Support:
- **Staging**: `staging-arc-runner-set`
- **Production**: `production-arc-runner-set`
- **Development**: `ubuntu-latest`

##### Java & Maven Setup:
```yaml
- uses: actions/setup-java@v4
  with:
    distribution: 'corretto'
    java-version: '8'
    cache: 'maven'

- name: Set up Maven
  uses: stCarolas/setup-maven@v4.5
  with:
    maven-version: 3.6.3
```

##### Secret Management:
- Environment-specific secrets
- Token-based authentication
- Secure credential handling

##### Reporting Integration:
- ExtentReports generation
- Test result artifacts
- Failure notification system

## Framework Features

### 🔧 **Configuration Management**
- Environment-specific property files
- Dynamic configuration switching
- Centralized constant management

### 📊 **Reporting & Logging**
- ExtentReports integration with custom listeners
- Thread-safe report management
- Request/response logging
- Test execution tracking
- Screenshot and attachment support

### 🧪 **Test Organization**
- Modular test structure
- Base test classes for common functionality
- Parameterized tests
- Data-driven testing with Excel/JSON
- Test suite organization by functionality

### 🔄 **Data Management**
- Global data sharing via DataToShare class
- Test data generation utilities
- Excel and JSON data handling
- Dynamic test data creation

### 🛠️ **Utility Functions**
- JSON path extraction
- Date/time utilities
- File I/O operations
- Wait mechanisms
- Assertion helpers

## How to Write New Test Cases

### 1. **Choose the Right Base Class**
```java
// For Smallcase API tests
public class YourNewTest extends BaseTest {
    // Your test methods
}

// For OTP Service tests  
public class YourNewTest extends OtpServiceBaseTest {
    // Your test methods
}

// For Common functionality tests
public class YourNewTest extends CommonBaseTest {
    // Your test methods
}
```

### 2. **Test Method Structure**
```java
@Test(testName = "Your Test Name", description = "Test description")
@Parameters({"param1", "param2"})
public void yourTestMethod(String param1, String param2) {
    // Test implementation
    logger.info("Starting test: " + param1);
    
    // API call
    Response response = given()
        .filter(new RequestLoggingFilter(captor))
        .spec(RequestSpec.yourSpec())
        .when()
        .get(yourEndpoint);
    
    // Assertions
    ResponseAssert.assertStatusCode(response, 200);
    
    // Report logging
    writeRequestAndResponseInReport(writer, response, "YourTag");
}
```

### 3. **Configuration Setup**
- Add new properties to appropriate config files in `src/main/java/com/allconfig/`
- Use `ConfigRead.getPropertyValue("your_property")` to access values
- Store test data in `src/main/java/resource/testData/`

### 4. **Test Suite Integration**
- Add your test class to appropriate XML file in `config/` folder
- Use parameters for test data
- Follow naming conventions

### 5. **Best Practices**
- Extend appropriate base class
- Use RequestLoggingFilter for request/response logging
- Implement proper error handling
- Use DataToShare for sharing data between tests
- Follow naming conventions for test methods
- Add proper logging statements
- Use appropriate assertions from ResponseAssert class

## 📁 Complete Folder Structure

### Detailed Project Hierarchy
```
sc-qa-restassured/
├── 📄 pom.xml                                       # Maven configuration
├── 📄 README.md                                     # Project documentation
├── 📄 FRAMEWORK_STRUCTURE_GUIDE.md                  # Detailed framework guide
├── 📁 .github/                                      # GitHub Actions workflows
│   └── 📁 workflows/                                # CI/CD pipeline configurations
│       ├── 📄 dynamic-dispatcher.yml                # Dynamic test execution
│       ├── 📄 scheduled-jobs.yml                    # Scheduled cron jobs
│       ├── 📄 dev-flow-POC.yml                      # Development flow POC
│       └── 📄 prod.yml                              # Production workflows
├── 📁 config/                                       # TestNG suite configurations
│   ├── 📄 build_sanity.xml                          # Build sanity tests
├── 📁 src/                                          # Source code
│   ├── 📁 main/java/                                # Framework Core Components
│   │   ├── 📁 com/                                  # Main packages
│   │   │   ├── 📁 allconfig/                        # Environment configurations
│   │   │   │   ├── 📄 configAPI.properties          # Staging environment
│   │   │   │   ├── 📄 configAPIDEV.properties       # Development environment
│   │   │   │   ├── 📄 configAPIPreview.properties   # Preview environment
│   │   │   │   └── 📄 configAPIPROD.properties      # Production environment
│   │   │   ├── 📁 otpService/                       # OTP Service resources
│   │   │   │   └── 📁 resource/                     # OTP service endpoints
│   │   │   └── 📁 smallcase/                        # API resources & POJOs
│   │   │       └── 📁 resource/                     # API endpoints & specifications
│   │   ├── 📁 commonutils/                          # Utility classes (20+ files)
│   │   │   ├── 📄 AssertActions.java                # Custom assertion methods
│   │   │   ├── 📄 ConfigRead.java                   # Configuration file reader
│   │   │   ├── 📄 CurrentDate.java                  # Date/time utilities
│   │   │   ├── 📄 DataToShare.java                  # Global data sharing
│   │   │   ├── 📄 EmailTestUser.java                # Email test user management
│   │   │   ├── 📄 ExcelRead.java                    # Excel file reading utilities
│   │   │   ├── 📄 ExcelWrite.java                   # Excel file writing utilities
│   │   │   ├── 📄 GetSmallcaseID.java               # Smallcase ID generation
│   │   │   ├── 📄 IConst.java                       # Framework constants
│   │   │   ├── 📄 JsonfetchUtil.java                # JSON data extraction
│   │   │   ├── 📄 JsonPathFinder.java               # JSON path utilities
│   │   │   ├── 📄 ListDatatoShare.java              # List data sharing
│   │   │   ├── 📄 PhoneAndEmailGenerator.java       # Test data generation
│   │   │   ├── 📄 PhoneNoTestUser.java              # Phone number utilities
│   │   │   ├── 📄 ReadJSON.java                     # JSON file reading
│   │   │   ├── 📄 RemoveArrayBrackets.java          # String manipulation
│   │   │   ├── 📄 TextFileReader.java               # Text file reading
│   │   │   ├── 📄 TextFileWriter.java               # Text file writing
│   │   │   └── 📄 WaitMethod.java                   # Wait utilities
│   │   └── 📁 resource/                             # Reporting & test data
│   │       ├── 📁 reports/                          # ExtentReports
│   │       │   ├── 📄 ExtentManager.java            # Thread-safe report management
│   │       │   ├── 📄 ExtentReport.java             # Report initialization
│   │       │   └── 📄 LogStatus.java                # Logging utilities
│   │       ├── 📁 resource/                         # TestNG listeners
│   │       │   ├── 📄 CommonListener.java           # Common test listener
│   │       │   ├── 📄 ListenerClass.java            # Report generation listener
│   │       │   ├── 📄 MyTransformer.java            # Data transformation
│   │       │   └── 📄 RetryAnalyzer.java            # Test retry mechanism
│   │       ├── 📁 testData/                         # Test data & schemas
│   │       │   ├── 📄 CreatedSmallcasePicks.json    # Test data for smallcase picks
│   │       │   ├── 📄 createPayload.json            # API payload templates
│   │       │   ├── 📁 JSONSchemas/                  # JSON schema validation (25 files)
│   │       │   ├── 📄 PayloadParamSheet.xlsx        # Parameter data
│   │       │   ├── 📄 QueryParams.json              # Query parameters
│   │       │   ├── 📄 scid_prod.xlsx                # Production smallcase IDs
│   │       │   ├── 📄 scid_stag.xlsx                # Staging smallcase IDs
│   │       │   ├── 📄 Stockquery.xlsx               # Stock query data
│   │       │   └── 📄 SubscriptionFlowData.xlsx     # Subscription flow data
│   │       └── 📄 logback.xml                       # Logging configuration
│   └── 📁 test/java/                                # Test Implementation
│       ├── 📁 com/                                  # Test packages
│       │   ├── 📁 asserts/                          # Custom assertions
│       │   │   └── 📄 ResponseAssert.java           # API response assertions
│       │   ├── 📁 multiassetsearch/                 # Multi-asset search tests
│       │   │   ├── 📄 SearchAPI.java                # Asset search
│       │   │   ├── 📄 TrendingAPI.java              # Trending assets
│       │   │   ├── 📄 SCDiscoverAPI.java            # Smallcase discovery
│       │   │   ├── 📄 StockDiscoverAPI.java         # Stock discovery
│       │   │   ├── 📄 MFDiscoverAPI.java            # Mutual Fund discovery
│       │   │   ├── 📄 SCConfigAPI.java              # Smallcase config
│       │   │   ├── 📄 StockConfigAPI.java           # Stock config
│       │   │   ├── 📄 MFConfigAPI.java              # MF config
│       │   │   ├── 📄 SCIncludeExclude.java         # SC include/exclude
│       │   │   ├── 📄 StockIncludeExclude.java      # Stock include/exclude
│       │   │   ├── 📄 MFIncludeExclude.java         # MF include/exclude
│       │   │   └── 📄 multiAssetUtils.java          # Multi-asset utilities
│       │   ├── 📁 multiassetorderlisting/           # Order listing tests
│       │   │   ├── 📄 OrdersListingAPI.java         # Order listing tests
│       │   │   └── 📄 GroupOrdersListingAPI.java    # Group order listing tests
│       │   ├── 📁 multiassetcollection/             # Multi-asset collection tests
│       │   │   ├── 📁 experience/                   # Multi-asset experience
│       │   │   │   └── 📄 MultiAssetCollection.java
│       │   │   └── 📄 HealthCheck.java              # Health check tests
│       │   ├── 📁 mutualfund/                       # Mutual fund tests
│       │   │   ├── 📁 experience/                   # MF experience (6 files)
│       │   │   ├── 📁 onboarding/                   # MF onboarding
│       │   │   │   └── 📄 CreateTransaction.java
│       │   │   └── 📄 HealthCheck.java              # Health check tests
│       │   ├── 📁 otpservice/                       # OTP service tests
│       │   │   ├── 📄 Auth.java                     # Authentication tests
│       │   │   ├── 📄 Common.java                   # Common OTP service tests
│       │   │   ├── 📄 CreateClient.java             # Client creation tests
│       │   │   ├── 📄 DeleteClient.java             # Client deletion tests
│       │   │   ├── 📄 DeleteFilteredClients.java    # Filtered client deletion
│       │   │   ├── 📄 EmailTestUser.java            # Email test user tests
│       │   │   ├── 📄 FilterClient.java             # Client filtering tests
│       │   │   ├── 📄 OtpServiceBaseTest.java       # Base test for OTP service
│       │   │   └── 📄 PhoneTestUser.java            # Phone test user tests
│       │   ├── 📁 samBamConnect/                    # SAM-BAM connection tests
│       │   │   ├── 📄 ConnectHelper.java            # Connection helper utilities
│       │   │   └── 📄 SamBamConnect.java            # SAM-BAM connection tests
│       │   ├── 📁 smallboard/                       # Smallboard tests
│       │   │   ├── 📄 GetLeprechaun.java            # Leprechaun generation tests
│       │   │   ├── 📄 PushRebalance.java            # Rebalance push tests
│       │   │   └── 📁 multiassetcollection/         # Collections (5 files)
│       │   └── 📁 smallcaseapi/                     # Core API tests
│       │       ├── 📄 BaseTest.java                 # Base test class
│       │       ├── 📄 SmallcaseLoginAPI.java        # Login API tests
│       │       ├── 📄 SmallcaseProfileAPI.java      # Profile API tests
│       │       ├── 📄 DashboardAPI.java             # Dashboard API tests
│       │       ├── 📄 DiscoverAPI.java              # Discovery API tests
│       │       ├── 📄 Search.java                   # Search API tests
│       │       ├── 📄 CollectionAPI.java            # Collection API tests
│       │       ├── 📄 GetCollection.java            # Collection retrieval tests
│       │       ├── 📄 GetUser.java                  # User data tests
│       │       ├── 📄 GetWatchlistAPI.java          # Watchlist retrieval tests
│       │       ├── 📄 AddToWatchlistAPI.java        # Watchlist addition tests
│       │       ├── 📄 RemoveWatchlist.java          # Watchlist removal tests
│       │       ├── 📄 CheckStatusAPI.java           # Status check tests
│       │       ├── 📄 BlogProxyAPI.java             # Blog proxy tests
│       │       ├── 📄 Highlighted.java              # Highlighted content tests
│       │       ├── 📄 InvestmentInsightAPI.java     # Investment insight tests
│       │       ├── 📄 LogoutAPI.java                # Logout tests
│       │       ├── 📄 NotificationAPI.java          # Notification tests
│       │       ├── 📄 OfferAPI.java                 # Offer tests
│       │       ├── 📄 PriceandChangeAPI.java        # Price and change tests
│       │       ├── 📄 FundsAPI.java                 # Funds API tests
│       │       ├── 📁 orderFlow/                    # Order flow tests (18 files)
│       │       │   ├── 📄 BuySmallcaseAPI.java      # Buy order execution
│       │       │   ├── 📄 ManageOrder.java          # Order management
│       │       │   ├── 📄 StartSIP.java             # SIP creation
│       │       │   ├── 📄 ManageSIP.java            # SIP management
│       │       │   ├── 📄 EndSipAPI.java            # SIP termination
│       │       │   ├── 📄 InvestMore.java           # Additional investments
│       │       │   ├── 📄 PartialExit.java          # Partial exits
│       │       │   ├── 📄 ExitSmallcase.java        # Exit orders
│       │       │   ├── 📄 OrderIscidAPI.java        # Order status tracking
│       │       │   └── 📄 ... (9+ more)             # Additional order flow tests
│       │       ├── 📁 samflow/                      # SAM flow tests (18 files)
│       │       │   ├── 📄 SamLogin.java             # SAM login flows
│       │       │   ├── 📄 SamLoginHelper.java       # SAM login utilities
│       │       │   ├── 📄 SSOLogin.java             # SSO authentication
│       │       │   ├── 📁 investmentscore/          # Investment score (3 files)
│       │       │   ├── 📁 lamfflow/                 # LAMF flow (7 files)
│       │       │   └── 📄 ... (8+ more)             # Additional SAM flow tests
│       │       ├── 📁 payload/                      # Request payloads (17 files)
│       │       ├── 📁 createsc/                     # Smallcase creation (5 files)
│       │       └── 📁 brokerAuthCookies/            # Broker auth (1 file)
│       └── 📁 resource/                             # Test resources
└── 📁 test-output/                                  # Generated reports & logs
    ├── 📁 extentreports/                            # ExtentReports HTML files
    └── 📁 Logs/                                     # Test execution logs
```

## Execution Commands

### **Maven Commands**
```bash
# Run specific test suite
mvn -Denv=staging -Dsurefire.suiteXmlFiles=config/your_suite.xml clean test

# Run with specific environment
mvn -Denv=production clean test

# Run build tests
mvn clean test -Pbuild-test
```

### **GitHub Actions Execution**
```bash
# Manual execution via GitHub Actions
# Go to Actions tab → Select workflow → Run workflow
# Available workflows:
# - Dynamic Dispatcher (Manual suite selection)
# - Scheduled Jobs (Automated cron execution)
# - Dev Flow POC (Development testing)
# - Production Workflows (Production validation)
```

### **Available Test Suites**
- `config/end2end_flow.xml` - Complete end-to-end flow
- `config/main_runner.xml` - Main test runner
- `config/smoke_test.xml` - Smoke tests
- `config/multi_asset_search.xml` - Multi-asset search tests
- `config/order_flow_with_sip.xml` - Order flow with SIP
- `config/order_flow_without_sip.xml` - Order flow without SIP
- `config/page_navigation_flow.xml` - Page navigation tests
- `config/rebalance_flow.xml` - Rebalance flow tests
- `config/mf_experience.xml` - Mutual Fund experience tests
- `config/sam_bam_connect.xml` - SAM-BAM integration tests
- `config/health_check.xml` - Health check tests
- `config/create_test_users.xml` - Test user creation
- `config/filter_and_delete_client.xml` - Client management tests
- `config/otp_service_stage.xml` - OTP service staging tests
- `config/smallcase_profile.xml` - Smallcase profile tests
- `config/stage_sam_flow.xml` - Staging SAM flow
- `config/prod_sam_flow.xml` - Production SAM flow
- `config/multi_asset_flow.xml` - Multi-asset flow tests
- `config/multi_asset_orders_listing.xml` - Multi-asset order listing
- `config/build_sanity.xml` - Build sanity tests
- `config/test_runner.xml` - General test runner
- `config/extent_config.xml` - ExtentReports configuration

## Environment Support
- **Staging**: `configAPIDEV.properties`
- **Development**: `configAPIDEV.properties`  
- **Preview**: `configAPIPreview.properties`
- **Production**: `configAPIPROD.properties`

## Reports Location
- **ExtentReports**: `test-output/extentreports/`
- **Logs**: `test-output/Logs/`

This framework provides a robust foundation for API testing with comprehensive reporting, data management, and test organization capabilities.

## Quick Reference

### Quick Start for New API Automation
1. Copy `prompts/generic_api_prompt_template.md`
2. Rename to `[your_api_name].md`
3. Fill in placeholders (endpoint, base URLs, headers, payload, parameters)
4. Follow the step-by-step instructions in the template

### Common Placeholders to Replace
| Placeholder | Replace With | Example |
|-------------|--------------|---------|
| `[API_NAME]` | Your API name | `UserProfileAPI` |
| `[API_ENDPOINT_PATH]` | API path | `/api/v1/user/profile` |
| `[STAGING_BASE_URL]` | Staging URL | `https://api-stag.smallcase.com` |
| `[PRODUCTION_BASE_URL]` | Production URL | `https://api.smallcase.com` |
| `[CLASS_NAME]` | Java class name | `UserProfileAPI` |
| `[PARAMETER_NAME]` | TestNG parameter | `userId` |

### Common Code Patterns

Test method structure:
```java
@Test
@Parameters({"parameter_name"})
public void testApiName(String parameterValue) {
    // Implementation
}
```

Payload creation:
```java
Map<String, Object> payload = new HashMap<>();
payload.put("field1", value1);
```

Response validation:
```java
ResponseAssert.assertThat(response)
    .returns_200_OK()
    .hasHeaderApplicationJSON();
```

### Checklist for New APIs
- [ ] Choose appropriate template
- [ ] Fill in all placeholders
- [ ] Create Java class in correct package
- [ ] Add API resource to the right resource class
- [ ] Implement test method
- [ ] Apply request spec and headers
- [ ] Add logging and reporting
- [ ] Create TestNG XML and parameters
- [ ] Execute tests and validate results