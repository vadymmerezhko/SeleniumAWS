package org.example;

import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

public class RobustWebElement implements WebElement {
    private static final int WAIT_FOR_ELEMENT_TIMEOUT_SEC = 15;
    private static final int RETRY_WAIT_SECONDS = 1;
    private static final int RETRY_COUNT = 5;
    private WebElement element;
    private final By by;
    private final WebDriver driver;
    private final RobustWebDriverWaiter waiter;

    public RobustWebElement(WebElement element, By by, WebDriver driver) {
        this.element = element;
        this.by = by;
        this.driver = driver;
        waiter = new RobustWebDriverWaiter(driver);
    }

    @Override
    public void click() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                element.click();
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public void submit() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                element.submit();
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                element.sendKeys(keysToSend);
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public void clear() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                element.clear();
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                return element.getAttribute(name);
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public boolean isSelected() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                return element.isSelected();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public boolean isEnabled() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.isEnabled();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getText() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            waiter.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            try {
                return element.getText();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public List<WebElement> findElements(By childBy) {
        return element.findElements(childBy).stream().map(element ->
                        new RobustWebElement(element, childBy, driver))
                .collect(Collectors.toList());
    }

    @Override
    public WebElement findElement(By childBy) {
        return new RobustWebElement(element.findElement(childBy), childBy, driver);
    }

    @Override
    public boolean isDisplayed() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.isDisplayed();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                Waiter.waitSeconds(RETRY_WAIT_SECONDS);
                element = driver.findElement(by);
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return element.getScreenshotAs(target);
    }
}
