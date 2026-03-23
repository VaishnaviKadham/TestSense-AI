package tests;

import base.BaseTest;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.ExcelUtil;
import utils.ExtentTestManager;

import org.testng.Assert;
import org.testng.annotations.*;

public class AmazonUITests extends BaseTest {

	HomePage home;
	SearchResultsPage results;

	// dependsOnMethods ensures BaseTest.setupClass() runs first and driver is ready
	@BeforeClass(alwaysRun = true, dependsOnMethods = "setupClass")
	public void init() {
		System.out.println("Initializing Page Objects for UI Tests");
		home = new HomePage(driver);
		results = new SearchResultsPage(driver);
	}

	@Test(priority = 1)
	public void verifyHomepageUI() {
		ExtentTestManager.logInfo("Executing verifyHomepageUI");
		ExtentTestManager.logInfo("Checking Amazon logo visibility");
		Assert.assertTrue(home.isLogoVisible(), "Logo not visible");
		ExtentTestManager.logPass("Logo is visible");

		ExtentTestManager.logInfo("Checking search box visibility");
		Assert.assertTrue(home.isSearchBoxVisible(), "Search box not visible");
		ExtentTestManager.logPass("Search box is visible");

		ExtentTestManager.logInfo("Checking cart icon visibility");
		Assert.assertTrue(home.isCartVisible(), "Cart not visible");
		ExtentTestManager.logPass("Cart is visible");

		ExtentTestManager.logPass("Homepage UI verified successfully");
	}

	@Test(priority = 2)
	public void verifySearchSuggestionsUI() {
		ExtentTestManager.logInfo("Executing verifySearchSuggestionsUI");
		ExtentTestManager.logInfo("Typing 'laptop' in search box and waiting for suggestions");
		int size = home.search("laptop").size();
		ExtentTestManager.logInfo("Suggestions found: " + size);
		Assert.assertTrue(size > 0, "No search suggestions displayed");
		ExtentTestManager.logPass("Search suggestions verified — count: " + size);
	}

	@Test(priority = 3)
	public void verifyProductListingUI() {
		ExtentTestManager.logInfo("Executing verifyProductListingUI");
		ExtentTestManager.logInfo("Searching for 'books'");
		home.submitSearch("books");
		ExtentTestManager.logInfo("Validating product results are displayed");
		Assert.assertTrue(results.areResultsDisplayed(), "Results not displayed");
		ExtentTestManager.logPass("Product listing verified");
	}

	@Test(priority = 4)
	public void writeDataToExcel() {
		ExtentTestManager.logInfo("Executing writeDataToExcel");
		String searchText = "laptop";
		ExtentTestManager.logInfo("Writing value '" + searchText + "' to Excel");
		ExcelUtil.writeData("AmazonData", 0, 0, searchText);
		ExtentTestManager.logPass("Excel write completed");
	}

	@Test(priority = 5)
	public void verifyDataFromExcel() {
		ExtentTestManager.logInfo("Executing verifyDataFromExcel");
		ExtentTestManager.logInfo("Reading data from Excel row 0, col 0");
		String value = ExcelUtil.readData("AmazonData", 0, 0);
		ExtentTestManager.logInfo("Value read from Excel: " + value);
		Assert.assertEquals(value, "laptop", "Excel data mismatch");
		ExtentTestManager.logPass("Excel data verified successfully");
	}

	@Test(priority = 6)
	public void verifySearchUsingExcelMatrix() {
		ExtentTestManager.logInfo("Executing verifySearchUsingExcelMatrix");
		String path = ConfigReader.get("excelPath");
		ExtentTestManager.logInfo("Reading test data from Excel: " + path);
		String[][] data = ExcelUtil.getSheetData(path, "Sheet1");
		ExtentTestManager.logInfo("Total rows found: " + (data.length - 1));

		for (int i = 1; i < data.length; i++) {
			String searchText = data[i][0];
			ExtentTestManager.logInfo("[Row " + i + "] Searching: " + searchText);
			home.submitSearch(searchText);
			ExtentTestManager.logInfo("[Row " + i + "] Validating results");
			Assert.assertTrue(results.areResultsDisplayed(), "Search failed for: " + searchText);
			ExtentTestManager.logPass("[Row " + i + "] Search successful for: " + searchText);
		}
	}

	@Test(priority = 7)
	public void fail_wrongLocatorUI() {
		ExtentTestManager.logInfo("Executing wrong locator test (expected to fail)");
		ExtentTestManager.logInfo("Looking for element with invalid ID");
		boolean flag = driver.findElements(org.openqa.selenium.By.id("invalid-id-123")).size() > 0;
		ExtentTestManager.logInfo("Element found: " + flag);
		Assert.assertTrue(flag, "Element not found — as expected this test fails");
	}
}
