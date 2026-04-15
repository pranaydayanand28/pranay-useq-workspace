package resource.reports;

import com.relevantcodes.extentreports.ExtentTest;

public class LogStatus {

    private LogStatus() {
        // private to prevent instantiation
    }

    /** Safely get ExtentTest, fallback to console if null */
    private static ExtentTest safeGetTest() {
        ExtentTest test = ExtentManager.getExtTest();
        if (test == null) {
            System.out.println("[WARN] ExtentTest not initialized — logging fallback to console.");
        }
        return test;
    }

    public static void pass(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.PASS, message);
        else
            System.out.println("[PASS] " + message);
    }

    public static void fail(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.FAIL, message);
        else
            System.err.println("[FAIL] " + message);
    }

    public static void fail(Exception e) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.FAIL, e);
        else
            e.printStackTrace(System.err);
    }

    public static void fail(AssertionError a) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.FAIL, a);
        else
            a.printStackTrace(System.err);
    }

    public static void info(String message, String category) {
        ExtentTest test = ExtentManager.getExtTest(category);
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.INFO, message);
        else
            System.out.println("[INFO][" + category + "] " + message);
    }

    public static void info(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.INFO, message);
        else
            System.out.println("[INFO] " + message);
    }

    public static void error(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.ERROR, message);
        else
            System.err.println("[ERROR] " + message);
    }

    public static void fatal(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.FATAL, message);
        else
            System.err.println("[FATAL] " + message);
    }

    public static void skip(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.SKIP, message);
        else
            System.out.println("[SKIP] " + message);
    }

    public static void unknown(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.UNKNOWN, message);
        else
            System.out.println("[UNKNOWN] " + message);
    }

    public static void warning(String message) {
        ExtentTest test = safeGetTest();
        if (test != null)
            test.log(com.relevantcodes.extentreports.LogStatus.WARNING, message);
        else
            System.out.println("[WARN] " + message);
    }
}