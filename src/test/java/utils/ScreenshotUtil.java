package utils;

import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;
import java.io.File;

public class ScreenshotUtil {

    public static String capture(WebDriver driver, String testName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            String path = "screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";

            File dest = new File(path);
            FileUtils.copyFile(src, dest);

            // ✅ Return absolute path (important for report)
            return dest.getAbsolutePath();

        } catch (Exception e) {
            System.out.println("Screenshot capture failed: " + e.getMessage());
            return null;
        }
    }
}