package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class ProductPage {

    WebDriver driver;
    WebDriverWait wait;

    By addToCart = By.id("add-to-cart-button");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickFirstProduct() {
        WebElement first = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.cssSelector("h2 a")));
        first.click();
    }

    public boolean addToCart() {
        return wait.until(ExpectedConditions.elementToBeClickable(addToCart)).isDisplayed();
    }
}