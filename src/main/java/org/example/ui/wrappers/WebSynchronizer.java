package org.example.ui.wrappers;

import org.example.ui.factories.WebDriverFactory;
import org.example.utils.DataValidator;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.example.constants.Settings.PAGE_LOAD_WAIT_TIMEOUT_SECONDS;

/**
 * Web synchronizer class.
 */
public class WebSynchronizer {

    private final WebDriver driver;

    /**
     * Web synchronizer constructor.
     */
    public WebSynchronizer() {
        driver = WebDriverFactory.getDriver();
    }

    /**
     * Web synchronizer constructor.
     * @param driver The web driver
     */
    public WebSynchronizer(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Waits for page load.
     */
    public void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(PAGE_LOAD_WAIT_TIMEOUT_SECONDS));
        wait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Waits for web element to be clickable.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     * Throws runtime exception in case of timeout.
     * @return The web element to be clickable.
     */
    public WebElement waitForElementToBeClickableBy(By by, int timeoutSeconds) {
        DataValidator.notNull(by, "by");
        DataValidator.min(timeoutSeconds, 0, "timeoutInSeconds");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * Waits for web element to be present.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     * Throws runtime exception in case of timeout.
     * @return The web element to be present.
     */
    // TODO: add unit tests
    public WebElement waitForElementPresenceBy(By by, int timeoutSeconds) {
        DataValidator.notNull(by, "by");
        DataValidator.min(timeoutSeconds, 0, "timeoutInSeconds");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * Waits for web element visibility.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     * Throws runtime exception in case of timeout.
     * @return The web element to be visible.
     */
    public WebElement waitForElementVisibilityBy(By by, int timeoutSeconds) {
        DataValidator.notNull(by, "by");
        DataValidator.min(timeoutSeconds, 0, "timeoutInSeconds");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Waits for web element invisibility.
     * @param by The element locator.
     * @param timeoutSeconds The timeout seconds.
     * Throws runtime exception in case of timeout.
     */
    public void waitForElementInvisibility(By by, int timeoutSeconds) {
        DataValidator.notNull(by, "by");
        DataValidator.min(timeoutSeconds, 0, "timeoutInSeconds");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    /**
     * Waits for web element value setup.
     * @param element The web element.
     * @param attributeName The attribute name.
     * @param attributeValue The attribute value.
     * @param timeoutSeconds The timeout seconds.
     * Throws runtime exception in case of timeout.
     */
    public void waitForAttributeValue(WebElement element, String attributeName, String attributeValue, long timeoutSeconds) {
        DataValidator.notNull(element, "element");
        DataValidator.notBlank(attributeName, "attributeName");
        DataValidator.notNull(attributeValue, "attributeValue");
        DataValidator.min(timeoutSeconds, 0, "timeoutInSeconds");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                String actualValue = element.getAttribute(attributeName);
                return attributeValue.equals(actualValue);
            }
            @Override
            public String toString() {
                return String.format("waiting for element [%s] to have attribute '%s' with value '%s'",
                        element, attributeName, attributeValue);
            }
        });
    }

    /**
     * Waits for a checkbox or radio button element to be selected or unselected.
     * @param element The WebElement of the checkbox or radio button.
     * @param selected true to wait for the element to be selected, false to wait for it to be unselected.
     * @param timeoutSeconds The maximum time to wait in seconds.
     */
    public void waitForCheckboxOrRadioButton(WebElement element, boolean selected, int timeoutSeconds) {
        DataValidator.notNull(element, "element");
        DataValidator.min(timeoutSeconds, 0, "timeoutInSeconds");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        if (selected) {
            // Wait for the element to be selected (checked)
            wait.until(ExpectedConditions.elementToBeSelected(element));
        }
        else {
            // Wait for the element to be unselected (unchecked)
            wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeSelected(element)));
        }
    }
}
