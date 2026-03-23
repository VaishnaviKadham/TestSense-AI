package utils;

import com.aventstack.extentreports.*;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static void setTest(ExtentTest extentTest) {
        test.set(extentTest);
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    // ✅ STEP LOGGING METHODS
    public static void logInfo(String message) {
        if (getTest() != null)
            getTest().log(Status.INFO, message);
        else
            System.out.println(message);
    }

    public static void logPass(String message) {
        if (getTest() != null)
            getTest().log(Status.PASS, message);
        else
            System.out.println(message);
    }

    public static void logFail(String message) {
        if (getTest() != null)
            getTest().log(Status.FAIL, message);
        else
            System.out.println(message);
    }
}