package org.example.driver.robust;

import org.example.driver.waiter.RobustWebDriverWaiter;
import org.example.utils.Waiter;
import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

public class RobustWebElement implements WebElement {
    private static final int WAIT_FOR_ELEMENT_TIMEOUT_SEC = 15;
    private static final int RETRY_WAIT_MILLI_SEC = 100;
    private static final int RETRY_COUNT = 100;
    private WebElement element;
    private final RobustWebElement parent;
    private final By by;
    private final WebDriver driver;
    private final RobustWebDriverWaiter waiter;

    public RobustWebElement(WebElement element,
                            RobustWebElement parent,
                            By by,
                            WebDriver driver,
                            RobustWebDriverWaiter waiter) {
        this.element = element;
        this.parent = parent;
        this.by = by;
        this.driver = driver;
        this.waiter = waiter;
    }

    public WebElement getNativeElement() {
        return element;
    }

    @Override
    public void click() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.click();
                return;
            } catch (StaleElementReferenceException |
                     ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public void submit() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.submit();
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.sendKeys(keysToSend);
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public void clear() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.clear();
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
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
            try {
                return element.getAttribute(name);
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getDomProperty(String name) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.getDomProperty(name);
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getDomAttribute(String name) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.getDomAttribute(name);
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getAriaRole() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.getAriaRole();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getAccessibleName() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.getAccessibleName();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }


    @Override
    public boolean isSelected() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.isSelected();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
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
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public String getText() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                return element.getText();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    @Override
    public List<WebElement> findElements(By childBy) {
        return element.findElements(childBy).stream().map(childElement ->
                        new RobustWebElement(childElement, this, childBy, driver, waiter))
                .collect(Collectors.toList());
    }

    @Override
    public WebElement findElement(By childBy) {
        return new RobustWebElement(element.findElement(childBy), this, childBy, driver, waiter);
    }

    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
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

    public SearchContext getShadowRoot() {
        return element.getShadowRoot();
    }

        @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return element.getScreenshotAs(target);
    }

    protected void scrollToElement() {
        WebElement nativeElement = element;
        if (element instanceof RobustWebElement) {
            nativeElement = ((RobustWebElement) element).getNativeElement();
        }
        try {
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", nativeElement);
        }
        catch (StaleElementReferenceException |
               ElementNotInteractableException |
               java.util.NoSuchElementException e) {
            // Ignore exception;
        }
    }

    public void setValue(String value) {
        WebElement nativeElement = element;
        if (element instanceof RobustWebElement) {
            nativeElement = ((RobustWebElement) element).getNativeElement();
        }
        ((JavascriptExecutor)driver).executeScript(String.format("arguments[0].value='%s'", value), nativeElement);
    }

    private WebElement waitForChildElementPresence(By childBy) {
        try{
            return element.findElement(childBy);
        }
        catch (NoSuchElementException e) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                Waiter.waitSeconds(RETRY_WAIT_MILLI_SEC);
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

    private void fixClickableWebElement() {
        Waiter.waitMilliSeconds(RETRY_WAIT_MILLI_SEC);
        if (parent == null) {
            scrollToElement();
            element = waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
        } else {
            parent.fixClickableWebElement();
            element = parent.findElement(by);
        }
        //System.out.println("Element fixed: " + by.toString());
    }

    private void fixVisibleWebElement() {
        Waiter.waitMilliSeconds(RETRY_WAIT_MILLI_SEC);
        if (parent == null) {
            scrollToElement();
            element = waiter.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
        } else {
            parent.fixClickableWebElement();
            element = waitForChildElementPresence(by);
        }
    }
}
