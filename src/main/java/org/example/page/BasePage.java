package org.example.page;

import org.example.factory.WebDriverFactory;
import org.openqa.selenium.*;

/**
 * Base page class.
 * Contains common functionality for all page classes.
 */
public abstract class BasePage {

    protected WebDriver driver;

    /**
     * Base page constructor.
     */
    BasePage() {
        driver = WebDriverFactory.getDriver();
    }

    /**
     * Opens web page by its URL.
     * @param url The page URL.
     */
    public void open(String url) {
        driver.get(url);
    }

    /**
     * Returns page URL.
     * @return The page URL.
     */
    public String getURL() {
        return driver.getCurrentUrl();
    }

    /**
     * Refreshes the page.
     */
    public void refresh() {
        driver.navigate().refresh();
    }

    /**
     * Navigates back.
     */
    public void back() {
        driver.navigate().back();
    }

    /**
     * Navigates forward.
     */
    public void forward() {
        driver.navigate().forward();
    }

    /**
     * Scrolls page down.
     */
    public void scrollDown() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.END);
    }

    /**
     * Scrolls page up.
     */
    public void scrollUp() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.UP);
    }

    /**
     * Scrolls page right.
     */
    public void scrollRight() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.RIGHT);
    }

    /**
     * Scrolls page left.
     */
    public void scrollLeft() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.LEFT);
    }

    /**
     * Scrolls page to the bottom.
     */
    public void scrollToBottom() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.END);
    }

    /**
     * Scrolls page to the top.
     */
    public void scrollToTop() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.HOME);
    }

    /**
     * Scrolls page to web element.
     * @param element The element to scroll to.
     */
    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
