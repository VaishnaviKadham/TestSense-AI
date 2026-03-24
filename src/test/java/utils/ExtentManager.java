package utils;

import org.testng.annotations.AfterSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
	

    private static ExtentReports extent;

    public static ExtentReports getInstance() {

        if (extent == null) {

            ExtentSparkReporter reporter =
                    new ExtentSparkReporter("reports/ExtentReport.html");

            reporter.config().setReportName("Amazon Automation Report");
            reporter.config().setDocumentTitle("Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }

        return extent;
    }
    @AfterSuite
    public void tearDown() {
        if (extent != null) {	
            extent.flush();
        }
    }
}
