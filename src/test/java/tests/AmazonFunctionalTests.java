package tests;

import base.BaseTest;
import pages.HomePage;
import pages.ProductPage;
import pages.SearchResultsPage;
import utils.ExtentTestManager;

import org.testng.Assert;
import org.testng.annotations.*;

public class AmazonFunctionalTests extends BaseTest {

	HomePage home;
	SearchResultsPage results;
	ProductPage product;

	@BeforeClass
	public void init() {
	    System.out.println("Initializing Page Objects for UI Tests");
		home = new HomePage(driver);
		results = new SearchResultsPage(driver);
		product = new ProductPage(driver);
	}

	// PASS TESTS

	@Test(priority = 4)
	public void verifySearchFunctionality() {

		ExtentTestManager.logInfo("Testing search functionality");

		ExtentTestManager.logInfo("Searching for iPhone");
		home.submitSearch("iphone");

		ExtentTestManager.logInfo("Waiting for results");
		Assert.assertTrue(results.areResultsDisplayed(), "Search failed");

		ExtentTestManager.logPass("Search functionality working");
	}

	@Test(priority = 5)
	public void verifyAddToCartFlow() {

		ExtentTestManager.logInfo("Testing add to cart flow");

		ExtentTestManager.logInfo("Searching for pen");
		home.submitSearch("pen");

		ExtentTestManager.logInfo("Clicking first product");
		product.clickFirstProduct();

		ExtentTestManager.logInfo("Validating Add to Cart button");
		Assert.assertTrue(product.addToCart(), "Add to cart failed");

		ExtentTestManager.logPass("Add to cart validated successfully");
	}

	@Test(priority = 6)
	public void verifyFilterSortFunctionality() {

		ExtentTestManager.logInfo("Testing filter/sort functionality");

		ExtentTestManager.logInfo("Searching for shoes");
		home.submitSearch("shoes");

		ExtentTestManager.logInfo("Validating prices");
		Assert.assertTrue(results.verifyPricesPresent(), "Prices missing");

		ExtentTestManager.logPass("Filter and sort working");
	}

	//  FAILING TESTS

	@Test(priority = 10)
	public void fail_noResultsFunctional() {

		ExtentTestManager.logInfo("Testing no results scenario");

		ExtentTestManager.logInfo("Searching random string");
		home.submitSearch("asdkfjaskdfj123123");

		ExtentTestManager.logInfo("Validating results");
		Assert.assertTrue(results.areResultsDisplayed(), "Expected failure: No results");
	}

	@Test(priority = 11)
	public void fail_addToCartNotClickable() {

		ExtentTestManager.logInfo("Testing add to cart click failure");

		ExtentTestManager.logInfo("Searching product");
		home.submitSearch("pen");

		ExtentTestManager.logInfo("Clicking invalid button");
		driver.findElement(org.openqa.selenium.By.id("fake-add-to-cart")).click();
	}

	@Test(priority = 12)
	public void fail_timeoutIssue() {

		ExtentTestManager.logInfo("Testing timeout issue");

		ExtentTestManager.logInfo("Waiting for non-existing element");
		new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
				.until(org.openqa.selenium.support.ui.ExpectedConditions
						.visibilityOfElementLocated(org.openqa.selenium.By.id("never-exists")));
	}
}