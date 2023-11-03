package org.example.driver;

import org.example.Waiter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class RobustWebElement implements WebElement {
    private static final int WAIT_FOR_ELEMENT_TIMEOUT_SEC = 15;
    private static final int RETRY_WAIT_SECONDS = 1;
    private static final int RETRY_COUNT = 10;
    private WebElement element;
    private final RobustWebElement parent;
    private final By by;
    private final WebDriver driver;
    private final RobustWebDriverWaiter waiter;

    public RobustWebElement(WebElement element, RobustWebElement parent, By by, WebDriver driver) {
        this.element = element;
        this.parent = parent;
        this.by = by;
        this.driver = driver;
        waiter = new RobustWebDriverWaiter(driver);
        fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
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
                fixWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public List<WebElement> findElements(By childBy) {
        return element.findElements(childBy).stream().map(childElement ->
                        new RobustWebElement(childElement, this, childBy, driver))
                .collect(Collectors.toList());
    }

    @Override
    public WebElement findElement(By childBy) {
        System.out.println("Fix element.");
        return new RobustWebElement(waitForChildElementPresence(childBy), this, childBy, driver);
    }

    private WebElement waitForChildElementPresence(By childBy) {
        try{
            return element.findElement(childBy);
        }
        catch (NoSuchElementException e) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                Waiter.waitSeconds(1);
                List<WebElement> elements = element.findElements(childBy);
                if (!elements.isEmpty()) {
                    if (elements.size() > 1) {
                        throw new RuntimeException(
                                "More than one child element is found: " + childBy.toString());
                    }
                    return elements.get(0);
                }
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isDisplayed() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.isDisplayed();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    private void fixWebElement() {
        Waiter.waitSeconds(RETRY_WAIT_SECONDS);
        if (parent == null) {
            element = driver.findElement(by);
        } else {
            parent.fixWebElement();
            element = parent.findElement(by);
        }
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
