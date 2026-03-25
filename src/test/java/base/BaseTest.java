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

    // ✅ Custom Tee PrintStream (console + memory)
    class TeePrintStream extends PrintStream {
        private final PrintStream second;

        public TeePrintStream(PrintStream main, PrintStream second) {
            super(main);
            this.second = second;
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            try {
                second.write(buf, off, len);
            } catch (Exception ignored) {}
            super.write(buf, off, len);
        }
    }

    @BeforeSuite
    public void setupReport() {
        extent = ExtentManager.getInstance();
    }

    @BeforeClass
    @Parameters("executionMode")
    public void setupClass(@Optional("UI") String executionMode) {

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

        ExtentTestManager.logInfo("Launching Browser");
        ExtentTestManager.logInfo("Opening URL: " + url);

        driver.get(url);

        // ✅ Add Browser + Mode info to report
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Execution Mode", executionMode);
    }

    @BeforeMethod
    public void beforeTest(Method method) {

        startTime = System.currentTimeMillis();

        ExtentTest test = extent.createTest(method.getName());
        ExtentTestManager.setTest(test);

        // Capture logs
        logStream = new ByteArrayOutputStream();
        originalOut = System.out;

        PrintStream teeStream = new TeePrintStream(new PrintStream(logStream), originalOut);
        System.setOut(teeStream);

        ExtentTestManager.logInfo("Test Started: " + method.getName());
    }

    @AfterMethod
    public void afterTest(Method method, ITestResult result) {

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;

        System.setOut(originalOut);
        String logs = logStream.toString();
        
        try {
            java.io.File logDir = new java.io.File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            java.io.FileWriter fw = new java.io.FileWriter("logs/" + method.getName() + ".log");
            fw.write(logs);
            fw.close();
        } catch (Exception e) {
            System.out.println("Failed to write logs file");
        }

        ExtentTest test = ExtentTestManager.getTest();

        // ✅ Attach full console logs
        test.info("<pre>" + logs + "</pre>");

        if (result.getStatus() == ITestResult.SUCCESS) {

            ExtentTestManager.logPass("Test Passed");

        } else if (result.getStatus() == ITestResult.FAILURE) {

            ExtentTestManager.logFail("Test Failed: " + result.getThrowable());

            String path = ScreenshotUtil.capture(driver, method.getName());

            if (path != null) {
                try {
                    test.addScreenCaptureFromPath(path);
                } catch (Exception e) {
                    System.out.println("Failed to attach screenshot");
                }
            }

        } else {
            ExtentTestManager.logInfo("Test Skipped");
        }

        ExtentTestManager.logInfo("Execution Time: " + totalTime + " seconds");
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