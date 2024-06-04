package org.example.driver.waiter;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Robust web driver waiter class.
 */
public class RobustWebDriverWaiter {

    private final WebDriver driver;
    JavascriptExecutor js;

    /**
     * Robust web driver waiter constructor.
     * @param driver The web driver instance.
     */
    public RobustWebDriverWaiter(WebDriver driver) {
        this.driver = driver;
        js = (JavascriptExecutor) driver;
    }

    /**
     * Waits for page load.
     * @param timeoutSeconds The timeout seconds.
     *                       Throws runtime exception in case of timeout.
     */
    public void waitForPageLoad(int timeoutSeconds) {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript(
                                "return document.readyState").toString().equals("complete");
                    }
                };
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(expectation);
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
