package org.example.driver;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RobustWebDriverWaiter {

    private final WebDriver driver;
    JavascriptExecutor js;

    public RobustWebDriverWaiter(WebDriver driver) {
        this.driver = driver;
        js = (JavascriptExecutor) driver;

/*        wait = new FluentWait(driver);
        wait.withTimeout(5000, TimeUnit.MILLISECONDS);
        wait.pollingEvery(250, TimeUnit.MILLISECONDS);
        wait.ignoring(NoSuchElementException.class, InvalidElementStateException.class);*/
    }

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

    public WebElement waitForElementToBeClickableBy(By by, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public WebElement waitForElementPresenceBy(By by, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement waitForElementVisibilityBy(By by, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public boolean waitForElementAttributeBy(By by, String attributeName, String attributeValue, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.attributeToBe(by, attributeName, attributeValue));
    }
}
