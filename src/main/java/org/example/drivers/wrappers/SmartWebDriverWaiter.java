package org.example.drivers.wrappers;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.example.constants.Settings.PAGE_LOAD_WAIT_TIMEOUT_SECONDS;

/**
 * Smart web driver waiter class.
 */
public class SmartWebDriverWaiter {

    private final WebDriver driver;
    JavascriptExecutor js;

    /**
     * Smart WebDriver waiter constructor.
     * @param driver The web driver instance.
     */
    public SmartWebDriverWaiter(WebDriver driver) {
        this.driver = driver;
        js = (JavascriptExecutor) driver;
    }

    /**
     * Waits for page load.
     */
    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(
                PAGE_LOAD_WAIT_TIMEOUT_SECONDS));
        wait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Waits for web element to be clickable.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     *                       Throws runtime exception in case of timeout.
     * @return The web element to be clickable.
     */
    public WebElement waitForElementToBeClickableBy(By by, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * Waits for web element to be present.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     *                       Throws runtime exception in case of timeout.
     * @return The web element to be present.
     */
    public WebElement waitForElementPresenceBy(By by, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * Waits for web element visibility.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     *                       Throws runtime exception in case of timeout.
     * @return The web element to be visible.
     */
    public WebElement waitForElementVisibilityBy(By by, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Waits for web element attribute.
     * @param by The element locator.
     * @param attributeName The attribute name.
     * @param attributeValue The attribute value.
     * @param timeoutSeconds The timeout seconds.
     *                       Throws runtime exception in case of timeout.
     * @return The true/false flag. True - when attribute is present, false - otherwise.
     */
    public boolean waitForElementAttributeBy(By by, String attributeName, String attributeValue, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.attributeToBe(by, attributeName, attributeValue));
    }
}
