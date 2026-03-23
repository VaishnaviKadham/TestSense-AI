package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {

            // Allow report path to be overridden via -DreportPath=... (used by GitHub Actions)
            String reportDir = System.getProperty("reportPath", "reports");
            String reportFile = reportDir + "/ExtentReport.html";

            // Ensure directory exists
            new java.io.File(reportDir).mkdirs();

            ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);
            reporter.config().setReportName("Amazon Automation Report");
            reporter.config().setDocumentTitle("Test Execution Report");
            reporter.config().setTheme(Theme.DARK);
            reporter.config().setEncoding("utf-8");

            // Inline all resources so the HTML is self-contained (critical for CI artifacts)
            reporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(reporter);

            extent.setSystemInfo("Environment", System.getProperty("env", "GitHub Actions"));
            extent.setSystemInfo("Browser", "Chrome (Headless)");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extent;
    }
}
