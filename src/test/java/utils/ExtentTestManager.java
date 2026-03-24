package utils;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static void setTest(ExtentTest extentTest) {
        test.set(extentTest);
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    // ✅ INFO (Blue)
    public static void logInfo(String message) {
        if (getTest() != null)
            getTest().info(
                MarkupHelper.createLabel(message, ExtentColor.BLUE)
            );
        System.out.println(message);
    }

    // ✅ PASS (Green)
    public static void logPass(String message) {
        if (getTest() != null)
            getTest().pass(
                MarkupHelper.createLabel(message, ExtentColor.GREEN)
            );
        System.out.println(message);
    }

    // ✅ FAIL (Red)
    public static void logFail(String message) {
        if (getTest() != null)
            getTest().fail(
                MarkupHelper.createLabel(message, ExtentColor.RED)
            );
        System.out.println(message);
    }
}