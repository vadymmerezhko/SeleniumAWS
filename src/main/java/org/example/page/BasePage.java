package org.example.page;

import org.example.driver.WebDriverFactory;
import org.openqa.selenium.*;

public abstract class BasePage {

    protected WebDriver driver;

    BasePage() {
        driver = WebDriverFactory.getDriver();
    }

    public void open(String url) {
        driver.get(url);
    }

    public String getURL() {
        return driver.getCurrentUrl();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    public void back() {
        driver.navigate().back();
    }

    public void forward() {
        driver.navigate().forward();
    }

    public void scrollDown() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.END);
    }

    public void scrollUp() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.UP);
    }

    public void scrollRight() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.RIGHT);
    }

    public void scrollLeft() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.LEFT);
    }

    public void scrollToBottom() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.END);
    }

    public void scrollToTop() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.HOME);
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
