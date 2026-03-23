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

    // ✅ PARAMETER FROM testng.xml
    @Parameters("executionMode")
    @BeforeClass
    public void setupClass(@Optional("headless") String executionMode) {

        System.out.println("\n=======================================");
        System.out.println("Launching Browser for Test Class");
        System.out.println("Execution Mode: " + executionMode);
        System.out.println("=======================================");

        String url = ConfigReader.get("url");

        ChromeOptions options = new ChromeOptions();

        if (executionMode.equalsIgnoreCase("headless")) {

            System.out.println("Running in HEADLESS mode");

            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

        } else {

            System.out.println("Running in UI mode");

        }

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.get(url);

        System.out.println("Application opened successfully");
        System.out.println("=======================================\n");
    }

    @BeforeMethod
    public void beforeTest(Method method) {

        System.out.println("\n************** STARTING TEST **************");
        System.out.println("Executing Test Case: " + method.getName());

        startTime = System.currentTimeMillis();
    }

    @AfterMethod
    public void afterTest(Method method) {

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;

        System.out.println("Execution Time for " + method.getName() + ": " + totalTime + " seconds");

        System.out.println("************** END TEST **************\n");
    }

    @AfterClass
    public void tearDownClass() {

        System.out.println("\nClosing Browser");

        if (driver != null) {
            driver.quit();
        }

        System.out.println("Browser closed");
    }
}