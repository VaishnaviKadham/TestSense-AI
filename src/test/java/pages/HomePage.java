package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class HomePage {

    WebDriver driver;
    WebDriverWait wait;

    By logo = By.id("nav-logo-sprites");
    By searchBox = By.id("twotabsearchtextbox");
    By cart = By.id("nav-cart");
    By suggestions = By.cssSelector("div.s-suggestion");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLogoVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(logo)).isDisplayed();
    }

    public boolean isSearchBoxVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox)).isDisplayed();
    }

    public boolean isCartVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cart)).isDisplayed();
    }

    public List<WebElement> search(String text) {
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.clear();
        box.sendKeys(text);

        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(suggestions));
    }

    public void submitSearch(String text) {
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        box.clear();
        box.sendKeys(text);
        box.submit();
    }
}