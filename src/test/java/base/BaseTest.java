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
    protected PrintStream originalErr;

    @BeforeSuite(alwaysRun = true)
    public void setupReport() {
        extent = ExtentManager.getInstance();
    }

    @BeforeClass(alwaysRun = true)
    @Parameters("executionMode")
    public void setupClass(@Optional("headless") String executionMode) {

        System.out.println("Launching browser in [" + executionMode + "] mode...");

        ChromeOptions options = new ChromeOptions();

        // --- Critical flags required for GitHub Actions / Linux CI ---
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-debugging-port=9222");

        if (executionMode.equalsIgnoreCase("headless")) {
            options.addArguments("--headless=new");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        String url = ConfigReader.get("url");
        System.out.println("Opening URL: " + url);
        driver.get(url);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest(Method method) {

        // Capture console output FIRST — tees to both buffer and real stdout
        // so GitHub Actions logs still show everything live
        logStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;

        PrintStream tee = new PrintStream(logStream) {
            @Override public void println(String x) { originalOut.println(x); super.println(x); }
            @Override public void print(String x)   { originalOut.print(x);   super.print(x);   }
            @Override public void println(Object x) { originalOut.println(x); super.println(x); }
        };
        System.setOut(tee);
        System.setErr(tee);

        System.out.println("\n============================================================");
        System.out.println("STARTING TEST: " + method.getName());
        System.out.println("============================================================");

        startTime = System.currentTimeMillis();

        ExtentTest test = extent.createTest(method.getName());
        ExtentTestManager.setTest(test);
        ExtentTestManager.logInfo("Test Started: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest(Method method, ITestResult result) {

        long totalTime = (System.currentTimeMillis() - startTime) / 1000;

        System.out.println("------------------------------------------------------------");
        System.out.println("RESULT : " + (result.getStatus() == ITestResult.SUCCESS ? "PASSED" :
                            result.getStatus() == ITestResult.FAILURE ? "FAILED" : "SKIPPED"));
        System.out.println("Duration: " + totalTime + "s");
        System.out.println("============================================================\n");

        // Restore streams BEFORE reading buffer so the final lines above are captured
        System.setOut(originalOut);
        System.setErr(originalErr);

        String consoleLogs = logStream.toString();
        ExtentTest test = ExtentTestManager.getTest();

        // ── Console logs block ──────────────────────────────────────────
        if (consoleLogs != null && !consoleLogs.trim().isEmpty()) {
            String escaped = consoleLogs
                    .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
            test.info(
                "<details open><summary><b>📋 Console Output</b></summary>" +
                "<pre style='background:#1e1e1e;color:#d4d4d4;padding:10px;" +
                "border-radius:4px;font-size:12px;max-height:350px;overflow:auto;" +
                "white-space:pre-wrap;word-break:break-all;'>" +
                escaped + "</pre></details>");
        }

        // ── Pass / Fail / Skip ──────────────────────────────────────────
        if (result.getStatus() == ITestResult.SUCCESS) {
            ExtentTestManager.logPass("✅ Test Passed");

        } else if (result.getStatus() == ITestResult.FAILURE) {

            Throwable t = result.getThrowable();
            String errorMsg = (t != null) ? t.getMessage() : "Unknown failure";
            ExtentTestManager.logFail("❌ Test Failed: " + errorMsg);

            // Full stack trace collapsible block
            if (t != null) {
                java.io.StringWriter sw = new java.io.StringWriter();
                t.printStackTrace(new java.io.PrintWriter(sw));
                String stack = sw.toString()
                        .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                test.info(
                    "<details><summary><b>🔍 Stack Trace</b></summary>" +
                    "<pre style='background:#2d1b1b;color:#f8b4b4;padding:10px;" +
                    "border-radius:4px;font-size:11px;max-height:250px;overflow:auto;" +
                    "white-space:pre-wrap;'>" +
                    stack + "</pre></details>");
            }

            // Screenshot: base64 inline embed + raw file link fallback
            String screenshotPath = ScreenshotUtil.capture(driver, method.getName());
            if (screenshotPath != null) {
                // Always add a plain clickable link first (works even if image fails)
                test.info(
                    "<b>📸 Screenshot:</b> <a href='" + screenshotPath +
                    "' target='_blank' style='color:#4a9eff;'>" +
                    screenshotPath + "</a>");

                // Embed as base64 so it renders inline regardless of report location
                try {
                    byte[] imgBytes = java.nio.file.Files.readAllBytes(
                            java.nio.file.Paths.get(screenshotPath));
                    String b64 = java.util.Base64.getEncoder().encodeToString(imgBytes);
                    test.info(
                        "<img src='data:image/png;base64," + b64 + "' " +
                        "style='max-width:100%;border:2px solid #e74c3c;" +
                        "border-radius:4px;margin-top:6px;cursor:pointer;' " +
                        "onclick=\"window.open(this.src,'_blank')\" " +
                        "title='Click to open full size' " +
                        "alt='Failure screenshot: " + method.getName() + "'/>");
                } catch (Exception e) {
                    System.out.println("[WARN] base64 embed failed: " + e.getMessage());
                }
            }

        } else {
            ExtentTestManager.logInfo("⚠️ Test Skipped");
        }

        ExtentTestManager.logInfo("⏱ Execution Time: " + totalTime + " seconds");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}
