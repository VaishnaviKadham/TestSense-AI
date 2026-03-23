package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class SearchResultsPage {

    WebDriver driver;
    WebDriverWait wait;

    By results = By.cssSelector("div.s-main-slot div[data-component-type='s-search-result']");
    By prices = By.cssSelector("span.a-price-whole");

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<WebElement> getResults() {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(results));
    }

    public boolean areResultsDisplayed() {
        return getResults().size() > 0;
    }

    public boolean verifyPricesPresent() {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(prices)).size() > 0;
    }
}