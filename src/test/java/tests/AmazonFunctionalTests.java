package tests;

import base.BaseTest;
import pages.HomePage;
import pages.ProductPage;
import pages.SearchResultsPage;
import utils.ScreenshotUtil;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class AmazonFunctionalTests extends BaseTest {

	HomePage home;
	SearchResultsPage results;
	ProductPage product;

	@BeforeClass
	public void init() {
	    System.out.println("Initializing Page Objects for Functional Tests");
	    home = new HomePage(driver);
	    results = new SearchResultsPage(driver);
	    product = new ProductPage(driver);
	}

	// ✅ PASS TESTS

	@Test(priority = 4)
	public void verifySearchFunctionality() {
		System.out.println("Testing search functionality...");

		System.out.println(" Searching for iPhone...");
		home.submitSearch("iphone");

		System.out.println(" Waiting for results...");
		Assert.assertTrue(results.areResultsDisplayed(), " Search failed");

		System.out.println(" Search working fine");
	}

	@Test(priority = 5)
	public void verifyAddToCartFlow() {
		System.out.println("Testing add to cart flow...");

		System.out.println("Searching for pen...");
		home.submitSearch("pen");

		System.out.println("Clicking first product...");
		product.clickFirstProduct();

		System.out.println("Checking Add to Cart button...");
		Assert.assertTrue(product.addToCart(), " Add to cart failed");

		System.out.println("Add to cart validated");
	}

	@Test(priority = 6)
	public void verifyFilterSortFunctionality() {
		System.out.println("Testing filter/sort functionality...");

		System.out.println(" Searching for shoes...");
		home.submitSearch("shoes");

		System.out.println("Checking prices...");
		Assert.assertTrue(results.verifyPricesPresent(), " Prices missing");

		System.out.println("Filter/sort working");
	}

	// ❌ FAILING TESTS

	@Test(priority = 10)
	public void fail_noResultsFunctional() {
		System.out.println("Testing no results scenario...");

		System.out.println("Searching random string...");
		home.submitSearch("asdkfjaskdfj123123");

		System.out.println("Validating results...");
		Assert.assertTrue(results.areResultsDisplayed(), "No results found (Expected failure)");
	}

	@Test(priority = 11)
	public void fail_addToCartNotClickable() {
		System.out.println("Testing add to cart click failure...");

		System.out.println("Searching product...");
		home.submitSearch("pen");

		System.out.println("Trying to click invalid button...");
		driver.findElement(org.openqa.selenium.By.id("fake-add-to-cart")).click();
	}

	@Test(priority = 12)
	public void fail_timeoutIssue() {
		System.out.println(" Testing timeout/wait issue...");

		System.out.println("Waiting for non-existing element...");
		new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
				.until(org.openqa.selenium.support.ui.ExpectedConditions
						.visibilityOfElementLocated(org.openqa.selenium.By.id("never-exists")));

		System.out.println("This line will never execute");
	}

	// 📸 HANDLER
	@AfterMethod
	public void handleResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			System.out.println("FUNCTIONAL TEST FAILED: " + result.getName());
			System.out.println("Reason: " + result.getThrowable());
			ScreenshotUtil.capture(driver, result.getName());
		} else {
			System.out.println("FUNCTIONAL TEST PASSED: " + result.getName());
		}
	}
}