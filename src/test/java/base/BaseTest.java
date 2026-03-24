package base;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.testng.annotations.*;
import org.testng.ITestResult;

import utils.*;

import com.aventstack.extentreports.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BaseTest {

    protected WebDriver driver;
    protected long startTime;

    protected static ExtentReports extent;

    protected ByteArrayOutputStream logStream;
    protected PrintStream originalOut;

    @BeforeSuite
    public void setupReport() {
        extent = ExtentManager.getInstance();
    }

    @BeforeClass
    @Parameters("executionMode")
    public void setupClass(@Optional("UI") String executionMode) {

        System.out.println("Launching browser...");

        ChromeOptions options = new ChromeOptions();

        if (executionMode.equalsIgnoreCase("headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        String url = ConfigReader.get("url");

        System.out.println("Opening URL: " + url);
        driver.get(url);
    }

    @BeforeMethod
    public void beforeTest(Method method) {

        System.out.println("\n************** STARTING TEST **************");
        System.out.println("Executing Test Case: " + method.getName());

        startTime = System.currentTimeMillis();

        ExtentTest test = extent.createTest(method.getName());
        ExtentTestManager.setTest(test);

        // Capture console logs
        logStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(logStream));

        ExtentTestManager.logInfo("Test Started: " + method.getName());
    }

    @AfterMethod
    public void afterTest(Method method, ITestResult result) {

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;

        // Restore console
        System.setOut(originalOut);
        String logs = logStream.toString();

        ExtentTest test = ExtentTestManager.getTest();

        // Attach console logs
        test.info("<pre>" + logs + "</pre>");

        if (result.getStatus() == ITestResult.SUCCESS) {

            ExtentTestManager.logPass("Test Passed");

        } else if (result.getStatus() == ITestResult.FAILURE) {

            ExtentTestManager.logFail("Test Failed: " + result.getThrowable());

            // ✅ TAKE SCREENSHOT
            String path = ScreenshotUtil.capture(driver, method.getName());

            try {
                test.addScreenCaptureFromPath(path);
            } catch (Exception e) {
                System.out.println("Failed to attach screenshot");
            }

        } else {
            ExtentTestManager.logInfo("Test Skipped");
        }

        ExtentTestManager.logInfo("Execution Time: " + totalTime + " seconds");

        System.out.println("Execution Time: " + totalTime + " seconds");
        System.out.println("************** END TEST **************\n");
    }

    @AfterSuite
    public void tearDownReport() {
        extent.flush();
    }

    @AfterClass
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }
}