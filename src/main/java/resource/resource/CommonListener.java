package resource.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import resource.reports.ExtentManager;
import resource.reports.LogStatus;
import com.relevantcodes.extentreports.ExtentTest;

import java.util.Arrays;

public class CommonListener implements ITestListener {

    private static String testcaseName;
    private static final Logger logger = LoggerFactory.getLogger(ITestListener.class.getName());

    private static final java.util.List<String> CONTEXT_PARAM_NAMES =
            java.util.Arrays.asList("Flow", "flow", "assetType", "broker");

    private String constructTestNameFromParameters(ITestResult result) {
        Object[] parameters = result.getParameters();
        if (parameters == null || parameters.length == 0) {
            return getBaseTestName(result);
        }

        Object param = parameters[0];

        // Hashtable from DataProvider (broker-based tests)
        if (param instanceof java.util.Hashtable) {
            @SuppressWarnings("unchecked")
            java.util.Hashtable<String, String> arguments = (java.util.Hashtable<String, String>) param;
            String broker = arguments.get("broker");
            if (broker != null) {
                return getBaseTestName(result) + " - " + broker;
            }
        }

        // @Parameters annotation — find a known context param by name
        try {
            org.testng.annotations.Parameters paramsAnnotation = result.getMethod()
                    .getConstructorOrMethod().getMethod()
                    .getAnnotation(org.testng.annotations.Parameters.class);
            if (paramsAnnotation != null) {
                String[] paramNames = paramsAnnotation.value();
                for (String contextParam : CONTEXT_PARAM_NAMES) {
                    for (int i = 0; i < paramNames.length; i++) {
                        if (contextParam.equals(paramNames[i]) && i < parameters.length) {
                            String value = parameters[i].toString();
                            if (!value.isEmpty()) {
                                return getBaseTestName(result) + " - " + value;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not resolve context parameter name: {}", e.getMessage());
        }

        return getBaseTestName(result);
    }

    private String getBaseTestName(ITestResult result) {
        String testName = result.getTestName();
        if (testName != null && !testName.isEmpty()) {
            return testName;
        }

        String description = result.getMethod().getDescription();
        if (description != null && !description.isEmpty()) {
            return description;
        }
        return result.getMethod().getMethodName();
    }

    @Override
    public void onTestStart(ITestResult result) {
        Object[] params = result.getParameters();
        String featureSuffix = (params != null && params.length > 0) ? " with Feature => " + Arrays.toString(params) : "";
        System.out.println("===========================================================");
        System.out.println("Execution Started for test =>\t" + result.getName() + featureSuffix);
        logger.info("Execution Started for test =>\t" + result.getName() + featureSuffix);
        System.out.println("===========================================================");

        testcaseName = constructTestNameFromParameters(result);

        try {
            ExtentTest test = resource.reports.ExtentReport.report.startTest(testcaseName);
            ExtentManager.setExtentTest(test);
            LogStatus.pass(testcaseName + " is started successfully" + featureSuffix);
        } catch (Exception e) {
            logger.warn("Extent report not initialized, logging will be console-only: {}", e.getMessage());
            System.out.println("[WARN] Extent report not initialized. Proceeding with console log only.");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logWithFallback("PASS", result, null);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logWithFallback("FAIL", result, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logWithFallback("SKIP", result, null);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        endExtentSafely();
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
        endExtentSafely();
    }

    private void logWithFallback(String status, ITestResult result, Throwable throwable) {
        Object[] params = result.getParameters();
        String featureSuffix = (params != null && params.length > 0) ? " with Feature => " + Arrays.toString(params) : "";
        String msg = testcaseName + " => " + status + featureSuffix;

        System.out.println("===========================================================");
        System.out.println("Execution " + status + " for test =>\t" + result.getName() + featureSuffix);
        System.out.println("===========================================================");

        try {
            ExtentTest extTest = ExtentManager.getExtTest();
            if (extTest != null) {
                switch (status) {
                    case "PASS":
                        LogStatus.pass(msg);
                        break;
                    case "FAIL":
                        LogStatus.fail(msg);
                        if (throwable != null) {
                            LogStatus.fail(throwable.toString());
                            extTest.log(com.relevantcodes.extentreports.LogStatus.FAIL, throwable);
                        }
                        break;
                    case "SKIP":
                        LogStatus.skip(msg);
                        break;
                }
                resource.reports.ExtentReport.report.endTest(extTest);
            } else {
                // fallback logging if Extent not set
                System.out.println("[LOG-" + status + "]: " + msg);
                if (throwable != null) throwable.printStackTrace(System.out);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Logging failed: " + e.getMessage());
        }
    }

    private void endExtentSafely() {
        try {
            ExtentTest extTest = ExtentManager.getExtTest();
            if (extTest != null && resource.reports.ExtentReport.report != null) {
                resource.reports.ExtentReport.report.endTest(extTest);
            }
        } catch (Exception e) {
            System.out.println("[WARN] Failed to end extent test safely: " + e.getMessage());
        }
    }
}