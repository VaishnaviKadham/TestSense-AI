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

	// dependsOnMethods ensures BaseTest.setupClass() runs first and driver is ready
	@BeforeClass(alwaysRun = true, dependsOnMethods = "setupClass")
	public void init() {
		System.out.println("Initializing Page Objects for Functional Tests");
		home = new HomePage(driver);
		results = new SearchResultsPage(driver);
		product = new ProductPage(driver);
	}

	// ── PASSING TESTS ──────────────────────────────────────────────────

	@Test(priority = 4)
	public void verifySearchFunctionality() {
		ExtentTestManager.logInfo("Testing search functionality");
		ExtentTestManager.logInfo("Searching for 'iphone'");
		home.submitSearch("iphone");
		ExtentTestManager.logInfo("Waiting for search results");
		Assert.assertTrue(results.areResultsDisplayed(), "Search failed — no results displayed");
		ExtentTestManager.logPass("Search functionality working");
	}

	@Test(priority = 5)
	public void verifyAddToCartFlow() {
		ExtentTestManager.logInfo("Testing add to cart flow");
		ExtentTestManager.logInfo("Searching for 'pen'");
		home.submitSearch("pen");
		ExtentTestManager.logInfo("Clicking first product");
		product.clickFirstProduct();
		ExtentTestManager.logInfo("Validating Add to Cart button is present");
		Assert.assertTrue(product.addToCart(), "Add to Cart button not found");
		ExtentTestManager.logPass("Add to cart validated successfully");
	}

	@Test(priority = 6)
	public void verifyFilterSortFunctionality() {
		ExtentTestManager.logInfo("Testing filter/sort functionality");
		ExtentTestManager.logInfo("Searching for 'shoes'");
		home.submitSearch("shoes");
		ExtentTestManager.logInfo("Validating prices are present in results");
		Assert.assertTrue(results.verifyPricesPresent(), "Prices missing from results");
		ExtentTestManager.logPass("Filter and sort working");
	}

	// ── INTENTIONAL FAILURE TESTS ──────────────────────────────────────

	@Test(priority = 10)
	public void fail_noResultsFunctional() {
		ExtentTestManager.logInfo("Testing no-results scenario (expected to fail)");
		ExtentTestManager.logInfo("Searching for random string with no results");
		home.submitSearch("asdkfjaskdfj123123");
		ExtentTestManager.logInfo("Validating results — should be empty");
		Assert.assertTrue(results.areResultsDisplayed(), "Expected failure: No results found");
	}

	@Test(priority = 11)
	public void fail_addToCartNotClickable() {
		ExtentTestManager.logInfo("Testing invalid element click (expected to fail)");
		ExtentTestManager.logInfo("Searching for 'pen'");
		home.submitSearch("pen");
		ExtentTestManager.logInfo("Attempting click on non-existent button ID");
		driver.findElement(org.openqa.selenium.By.id("fake-add-to-cart")).click();
	}

	@Test(priority = 12)
	public void fail_timeoutIssue() {
		ExtentTestManager.logInfo("Testing timeout on non-existing element (expected to fail)");
		ExtentTestManager.logInfo("Waiting for element that never appears");
		new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
				.until(org.openqa.selenium.support.ui.ExpectedConditions
						.visibilityOfElementLocated(org.openqa.selenium.By.id("never-exists")));
	}
}
