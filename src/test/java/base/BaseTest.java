package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import utils.ConfigReader;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected long startTime;

    // ✅ RUN ONCE PER TEST CLASS
    @BeforeClass
    public void setupClass() {

        System.out.println("\n=======================================");
        System.out.println("Launching Browser for Test Class");
        System.out.println("=======================================");

        System.out.println("Reading URL from config file");
        String url = ConfigReader.get("url");

        // 🔥 HEADLESS TOGGLE (LOCAL vs CI)
        // Default = true (for GitHub Actions)
        String headless = System.getProperty("headless", "true");

        System.out.println("Configuring Chrome options");

        ChromeOptions options = new ChromeOptions();

        if (headless.equalsIgnoreCase("true")) {
            System.out.println("Running in HEADLESS mode");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        } else {
            System.out.println("Running in NORMAL (UI) mode");
        }

        System.out.println("Launching Chrome browser");
        driver = new ChromeDriver(options);

        System.out.println("Browser launched successfully");

        System.out.println("Maximizing browser window");
        driver.manage().window().maximize();

        System.out.println("Applying implicit wait");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        System.out.println("Opening URL: " + url);
        driver.get(url);

        System.out.println("Application opened successfully");
        System.out.println("=======================================\n");
    }

    // ✅ RUN BEFORE EACH TEST METHOD
    @BeforeMethod
    public void beforeTest(Method method) {

        System.out.println("\n************** STARTING TEST **************");
        System.out.println("Executing Test Case: " + method.getName());

        startTime = System.currentTimeMillis();
    }

    // ✅ RUN AFTER EACH TEST METHOD
    @AfterMethod
    public void afterTest(Method method) {

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;

        System.out.println("Execution Time for " + method.getName() + ": " + totalTime + " seconds");

        System.out.println("************** END TEST **************\n");
    }

    // ✅ RUN ONCE AFTER ALL TESTS
    @AfterClass
    public void tearDownClass() {

        System.out.println("\n=======================================");
        System.out.println("Closing Browser for Test Class");
        System.out.println("=======================================");

        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed successfully");
        }

        System.out.println("=======================================\n");
    }
}