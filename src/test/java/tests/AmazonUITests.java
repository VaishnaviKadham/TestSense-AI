package tests;

import base.BaseTest;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.ExcelUtil;
import utils.ScreenshotUtil;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class AmazonUITests extends BaseTest {

    HomePage home;
    SearchResultsPage results;

    @BeforeClass
    public void init() {
        System.out.println("Initializing Page Objects for UI Tests");
        home = new HomePage(driver);
        results = new SearchResultsPage(driver);
    }

    @Test(priority = 1)
    public void verifyHomepageUI() {

        System.out.println("Executing verifyHomepageUI");

        System.out.println("Checking Amazon logo visibility");
        Assert.assertTrue(home.isLogoVisible(), "Logo not visible");

        System.out.println("Checking search box visibility");
        Assert.assertTrue(home.isSearchBoxVisible(), "Search box not visible");

        System.out.println("Checking cart visibility");
        Assert.assertTrue(home.isCartVisible(), "Cart not visible");

        System.out.println("Homepage UI verification completed");
    }

    @Test(priority = 2)
    public void verifySearchSuggestionsUI() {

        System.out.println("Executing verifySearchSuggestionsUI");

        System.out.println("Typing 'laptop' in search box");
        int size = home.search("laptop").size();

        System.out.println("Validating suggestions count");
        Assert.assertTrue(size > 0, "Suggestions not displayed");

        System.out.println("Search suggestions verified");
    }

    @Test(priority = 3)
    public void verifyProductListingUI() {

        System.out.println("Executing verifyProductListingUI");

        System.out.println("Searching for 'books'");
        home.submitSearch("books");

        System.out.println("Waiting for product results");
        Assert.assertTrue(results.areResultsDisplayed(), "Results not displayed");

        System.out.println("Product listing verified");
    }

    @Test(priority = 4)
    public void writeDataToExcel() {

        System.out.println("Executing writeDataToExcel");

        String searchText = "laptop";

        System.out.println("Writing data to Excel sheet AmazonData");
        ExcelUtil.writeData("AmazonData", 0, 0, searchText);

        System.out.println("Excel write completed");
    }

    @Test(priority = 5)
    public void verifyDataFromExcel() {

        System.out.println("Executing verifyDataFromExcel");

        System.out.println("Reading data from Excel sheet AmazonData");
        String value = ExcelUtil.readData("AmazonData", 0, 0);

        System.out.println("Validating Excel data");
        Assert.assertEquals(value, "laptop", "Excel data mismatch");

        System.out.println("Excel data verified successfully");
    }

    @Test(priority = 6)
    public void verifySearchUsingExcelMatrix() {

        System.out.println("Executing verifySearchUsingExcelMatrix");

        String path = ConfigReader.get("excelPath");

        System.out.println("Reading Excel data from: " + path);
        String[][] data = ExcelUtil.getSheetData(path, "Sheet1");

        for (int i = 1; i < data.length; i++) {

            System.out.println("----------------------------------");

            String searchText = data[i][0];

            System.out.println("Using search value: " + searchText);

            System.out.println("Entering search text");
            home.submitSearch(searchText);

            System.out.println("Waiting for results");
            Assert.assertTrue(results.areResultsDisplayed(),
                    "Search failed for: " + searchText);

            System.out.println("Search validated for: " + searchText);
        }
    }

    @Test(priority = 7)
    public void fail_wrongLocatorUI() {

        System.out.println("Executing fail_wrongLocatorUI");

        System.out.println("Trying to locate invalid element");
        boolean flag = driver.findElements(
                org.openqa.selenium.By.id("invalid-id-123")).size() > 0;

        System.out.println("Validating element presence");
        Assert.assertTrue(flag, "Element not found");
    }

    @AfterMethod
    public void handleResult(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("TEST FAILED: " + result.getName());
            System.out.println("Reason: " + result.getThrowable());
            ScreenshotUtil.capture(driver, result.getName());
        } else {
            System.out.println("TEST PASSED: " + result.getName());
        }
    }
}