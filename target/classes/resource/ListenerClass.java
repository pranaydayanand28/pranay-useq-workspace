package resource.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import resource.reports.ExtentManager;
import resource.reports.LogStatus;

import java.util.Arrays;

public class ListenerClass implements ITestListener {

    private static String TestcaseName;
    private static final Logger logger = LoggerFactory.getLogger(ITestListener.class.getName());

    public static void setTestcaseName(String testcaseName) {
        TestcaseName = testcaseName;
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("===========================================================");
        System.out.println("Execution Started for test => " + result.getName() +
                " with Broker => " + Arrays.toString(result.getParameters()));
        logger.info("Execution Started for test => {} with Broker => {}", result.getName(), Arrays.toString(result.getParameters()));
        System.out.println("===========================================================");

        TestcaseName = result.getMethod().getDescription();
        setTestcaseName(TestcaseName);

        try {
            ExtentManager.setExtentTest(resource.reports.ExtentReport.report.startTest(TestcaseName));
            LogStatus.pass(TestcaseName + " is started successfully with broker => " + Arrays.toString(result.getParameters()));
        } catch (Exception e) {
            logger.error("Failed to start ExtentTest for {}: {}", result.getName(), e.getMessage());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("===========================================================");
        System.out.println("Executed Successfully, Test Name => " + result.getName() +
                " with Broker => " + Arrays.toString(result.getParameters()));
        logger.info("Executed Successfully, Test Name => {} with Broker => {}", result.getName(), Arrays.toString(result.getParameters()));
        System.out.println("===========================================================");

        try {
            if (ExtentManager.getExtTest() != null) {
                LogStatus.pass(result.getMethod().getDescription() +
                        " : passed with broker => " + Arrays.toString(result.getParameters()));
                resource.reports.ExtentReport.report.endTest(ExtentManager.getExtTest());
            } else {
                logger.warn("ExtentTest is null on success for {}", result.getName());
            }
        } catch (Exception e) {
            logger.error("Error while handling success for {}: {}", result.getName(), e.getMessage());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("===========================================================");
        System.out.println("Execution Failed for test => " + result.getName() +
                " with Broker => " + Arrays.toString(result.getParameters()));
        logger.error("Execution Failed for test => {} with Broker => {}", result.getName(), Arrays.toString(result.getParameters()));
        System.out.println("===========================================================");

        try {
            if (ExtentManager.getExtTest() != null) {
                LogStatus.fail(result.getMethod().getDescription() +
                        " has failed with broker => " + Arrays.toString(result.getParameters()));
                if (result.getThrowable() != null) {
                    LogStatus.fail(result.getThrowable().toString());
                    ExtentManager.getExtTest().log(com.relevantcodes.extentreports.LogStatus.FAIL, result.getThrowable());
                }
                resource.reports.ExtentReport.report.endTest(ExtentManager.getExtTest());
            } else {
                logger.error("ExtentTest is null — cannot log failure for {}", result.getName());
            }
        } catch (Exception e) {
            logger.error("Error while handling test failure for {}: {}", result.getName(), e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("===========================================================");
        System.out.println("Test Skipped => " + result.getName() +
                " with Broker => " + Arrays.toString(result.getParameters()));
        logger.warn("Test Skipped => {} with Broker => {}", result.getName(), Arrays.toString(result.getParameters()));
        System.out.println("===========================================================");

        try {
            if (ExtentManager.getExtTest() != null) {
                LogStatus.skip(result.getMethod().getDescription() +
                        " : skipped with broker => " + Arrays.toString(result.getParameters()));
                resource.reports.ExtentReport.report.endTest(ExtentManager.getExtTest());
            } else {
                logger.warn("ExtentTest is null on skip for {}", result.getName());
            }
        } catch (Exception e) {
            logger.error("Error while handling skipped test for {}: {}", result.getName(), e.getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        try {
            if (ExtentManager.getExtTest() != null) {
                resource.reports.ExtentReport.report.endTest(ExtentManager.getExtTest());
            }
        } catch (Exception e) {
            logger.error("Error handling partial success for {}: {}", result.getName(), e.getMessage());
        }
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite '{}' started.", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test Suite '{}' finished.", context.getName());
        try {
            if (ExtentManager.getExtTest() != null) {
                resource.reports.ExtentReport.report.endTest(ExtentManager.getExtTest());
            }
        } catch (Exception e) {
            logger.error("Error while finishing suite {}: {}", context.getName(), e.getMessage());
        }
    }
}