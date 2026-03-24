package tests;

import base.BaseTest;
import pages.HomePage;
import pages.ProductPage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.ExcelUtil;
import utils.ExtentTestManager;

import org.testng.Assert;
import org.testng.annotations.*;

public class AmazonUITests extends BaseTest {

	HomePage home;
	SearchResultsPage results;

	@BeforeClass
	public void init() {
		System.out.println("Initializing Page Objects for Functional Tests");
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

		ExtentTestManager.logInfo("Checking cart visibility");
		Assert.assertTrue(home.isCartVisible(), "Cart not visible");
		ExtentTestManager.logPass("Cart is visible");

		ExtentTestManager.logPass("Homepage UI verified successfully");
	}

	@Test(priority = 2)
	public void verifySearchSuggestionsUI() {

		ExtentTestManager.logInfo("Executing verifySearchSuggestionsUI");

		ExtentTestManager.logInfo("Typing 'laptop' in search box");
		int size = home.search("laptop").size();

		ExtentTestManager.logInfo("Validating suggestions count");
		Assert.assertTrue(size > 0, "Suggestions not displayed");

		ExtentTestManager.logPass("Search suggestions verified");
	}

	@Test(priority = 3)
	public void verifyProductListingUI() {

		ExtentTestManager.logInfo("Executing verifyProductListingUI");

		ExtentTestManager.logInfo("Searching for books");
		home.submitSearch("books");

		ExtentTestManager.logInfo("Validating product results");
		Assert.assertTrue(results.areResultsDisplayed(), "Results not displayed");

		ExtentTestManager.logPass("Product listing verified");
	}

	@Test(priority = 4)
	public void writeDataToExcel() {

		ExtentTestManager.logInfo("Executing writeDataToExcel");

		String searchText = "laptop";

		ExtentTestManager.logInfo("Writing data to Excel");
		ExcelUtil.writeData("AmazonData", 0, 0, searchText);

		ExtentTestManager.logPass("Excel write completed");
	}

	@Test(priority = 5)
	public void verifyDataFromExcel() {

		ExtentTestManager.logInfo("Executing verifyDataFromExcel");

		ExtentTestManager.logInfo("Reading data from Excel");
		String value = ExcelUtil.readData("AmazonData", 0, 0);

		ExtentTestManager.logInfo("Validating Excel data");
		Assert.assertEquals(value, "laptop", "Excel data mismatch");

		ExtentTestManager.logPass("Excel data verified");
	}

	@Test(priority = 6)
	public void verifySearchUsingExcelMatrix() {

		ExtentTestManager.logInfo("Executing verifySearchUsingExcelMatrix");

		String path = ConfigReader.get("excelPath");

		ExtentTestManager.logInfo("Reading Excel from: " + path);
		String[][] data = ExcelUtil.getSheetData(path, "Sheet1");

		for (int i = 1; i < data.length; i++) {

			String searchText = data[i][0];

			ExtentTestManager.logInfo("Searching: " + searchText);
			home.submitSearch(searchText);

			ExtentTestManager.logInfo("Validating results");
			Assert.assertTrue(results.areResultsDisplayed(), "Search failed for: " + searchText);

			ExtentTestManager.logPass("Search successful for: " + searchText);
		}
	}

	@Test(priority = 7)
	public void fail_wrongLocatorUI() {

		ExtentTestManager.logInfo("Executing wrong locator test");

		boolean flag = driver.findElements(org.openqa.selenium.By.id("invalid-id-123")).size() > 0;

		ExtentTestManager.logInfo("Validating element presence");
		Assert.assertTrue(flag, "Element not found");
	}
}