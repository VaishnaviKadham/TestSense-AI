package utils;

import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;
import java.io.File;

public class ScreenshotUtil {

    public static void capture(WebDriver driver, String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);

            File dest = new File("screenshots/" + testName + ".png");
            FileUtils.copyFile(src, dest);

            System.out.println("Screenshot captured: " + dest.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Failed to capture screenshot");
        }
    }
}