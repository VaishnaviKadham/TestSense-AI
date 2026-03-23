package utils;

import org.openqa.selenium.*;
import org.apache.commons.io.FileUtils;
import java.io.File;

public class ScreenshotUtil {

	public static String capture(WebDriver driver, String testName) {
		try {
			// Resolve screenshot directory from system property (set by GitHub Actions
			// workflow)
			String screenshotDir = System.getProperty("screenshotPath", "screenshots");
			new File(screenshotDir).mkdirs();

			// Sanitise test name so it's safe as a filename
			String safeName = testName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
			String path = screenshotDir + "/" + safeName + ".png";

			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(src, new File(path));

			System.out.println("[Screenshot] Saved: " + path);
			return path;

		} catch (Exception e) {
			System.out.println("[Screenshot] Capture failed: " + e.getMessage());
			return null;
		}
	}
}
