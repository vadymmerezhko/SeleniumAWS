package org.example.drivers.wrappers;

import org.example.data.Config;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;

/**
 * The robust web element class.
 * This class wraps WebElement and adds auto wait, retry on error
 * to make the WebElement more reliable.
 */
public class RobustWebElement implements WebElement {
    private static final int WAIT_FOR_ELEMENT_TIMEOUT_SEC = 15;
    private static final int RETRY_WAIT_MILLI_SEC = 100;
    private static final int RETRY_COUNT = 100;
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, WebElement> highlightedElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, String> prevElementStyleMap = new ConcurrentHashMap<>();
    static private final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
    long threadId;
    private WebElement element;
    private final RobustWebElement parent;
    private final By by;
    private final WebDriver driver;
    private final RobustWebDriverWaiter waiter;

    /**
     * The robust WebElemnt constructor.
     * @param element The wrapped WebElement instance.
     * @param parent The parent robust element (if any).
     * @param by The element locator.
     * @param driver The WebDriver instance.
     * @param waiter The Waiter instance.
     */
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
        threadId = Thread.currentThread().threadId();
    }

    /**
     * Returns native (wrapped) WebElement.
     * @return The native WebElement.
     */
    public WebElement getNativeElement() {
        return element;
    }

    /**
     * Clicks web element.
     */
    @Override
    public void click() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.click();
                handleElement(element);
                return;
            } catch (StaleElementReferenceException |
                     ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Submits web element.
     */
    @Override
    public void submit() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.submit();
                handleElement(element);
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Sends keys to web element.
     * @param keysToSend The keys to send.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.sendKeys(keysToSend);
                handleElement(element);
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Clears web element.
     */
    @Override
    public void clear() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                element.clear();
                handleElement(element);
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixClickableWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns web element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        return element.getTagName();
    }

    /**
     * Returns the web element attribute value by its name.
     * @param name The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String name) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String attribute = element.getAttribute(name);
                handleElement(element);
                return attribute;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns the DOM property value by its name.
     * @param name The DOM property name.
     * @return The DOM property value.
     */
    @Override
    public String getDomProperty(String name) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String domProperty = element.getDomProperty(name);
                handleElement(element);
                return domProperty;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns the DOM attribute value by its name.
     * @param name The DOM attribute name.
     * @return The DOM attribute value.
     */
    @Override
    public String getDomAttribute(String name) {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String domAttribute = element.getDomAttribute(name);
                handleElement(element);
                return domAttribute;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns the aria role value.
     * @return The aria role value.
     */
    @Override
    public String getAriaRole() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String arialRole = element.getAriaRole();
                handleElement(element);
                return arialRole;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns accessibility name value.
     * @return The accessibility name value.
     */
    @Override
    public String getAccessibleName() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String accessibleName = element.getAccessibleName();
                handleElement(element);
                return accessibleName;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }


    /**
     * Returns true/false selected flag.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                boolean isSelected = element.isSelected();
                handleElement(element);
                return isSelected;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns true/false enabled flag.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                boolean isEnabled = element.isEnabled();
                handleElement(element);
                return isEnabled;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Returns the web element text.
     * @return The web element text.
     */
    @Override
    public String getText() {
        Exception exception = null;
        for (int i = 0; i < RETRY_COUNT; i++) {
            try {
                String text = element.getText();
                handleElement(element);
                return text;
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                exception = e;
                fixVisibleWebElement();
            }
        }
        throw new RuntimeException(exception);
    }

    /**
     * Finds web elements by their locator.
     * @param childBy The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By childBy) {
        return element.findElements(childBy).stream().map(childElement ->
                        new RobustWebElement(childElement, this, childBy, driver, waiter))
                .collect(Collectors.toList());
    }

    /**
     * Finds web element by its locator.
     * @param childBy The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By childBy) {
        return new RobustWebElement(element.findElement(childBy), this, childBy, driver, waiter);
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
        return element.isDisplayed();
    }

    /**
     * Returns web element location point.
     * @return The web element location point.
     */
    @Override
    public Point getLocation() {
        return element.getLocation();
    }

    /**
     * REturns web element size dimension.
     * @return The web element size dimension.
     */
    @Override
    public Dimension getSize() {
        return element.getSize();
    }

    /**
     * Returns web element rectangle.
     * @return The web element rectangle.
     */
    @Override
    public Rectangle getRect() {
        return element.getRect();
    }

    /**
     * Returns web element CSS property value by its name.
     * @param propertyName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    /**
     * Returns web elemnt shadow root.
     * @return The shadow root.
     */
    public SearchContext getShadowRoot() {
        return element.getShadowRoot();
    }

    /**
     * Returns web element screenshot data.
     * @param target The screenshot target.
     * @return The screenshot data.
     * @param <X> The screenshot data type.
     * @throws WebDriverException The exception in case of error.
     */
        @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return element.getScreenshotAs(target);
    }

    /**
     * Scrolls page to web element.
     */
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

    /**
     * Sets web element value.
     * @param value The value to set.
     */
    public void setValue(String value) {
        WebElement nativeElement = element;
        if (element instanceof RobustWebElement) {
            nativeElement = ((RobustWebElement) element).getNativeElement();
        }
        ((JavascriptExecutor)driver).executeScript(String.format("arguments[0].value='%s'", value), nativeElement);
    }

    /**
     * Handles web element after an action on it.
     * @param element The web element.
     */
    public void handleElement(WebElement element) {
        if (handledElementMap.isEmpty() || handledElementMap.get(threadId) != element) {
            handledElementMap.put(threadId,element);

            if (config.getHighlightElement()) {
                highlightElement(element);
            }
            WaiterUtils.waitMilliSeconds(config.getStepDelay());
        }
    }

    private WebElement waitForChildElementPresence(By childBy) {
        try{
            return element.findElement(childBy);
        }
        catch (NoSuchElementException e) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                WaiterUtils.waitSeconds(RETRY_WAIT_MILLI_SEC);
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
        WaiterUtils.waitMilliSeconds(RETRY_WAIT_MILLI_SEC);
        if (parent == null) {
            scrollToElement();
            element = waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
        } else {
            parent.fixClickableWebElement();
            element = parent.findElement(by);
        }
    }

    private void fixVisibleWebElement() {
        WaiterUtils.waitMilliSeconds(RETRY_WAIT_MILLI_SEC);
        if (parent == null) {
            scrollToElement();
            element = waiter.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
        } else {
            parent.fixClickableWebElement();
            element = waitForChildElementPresence(by);
        }
    }

    private void highlightElement(WebElement element) {

        if (highlightedElementMap.containsKey(threadId)) {
            // Restore element style.
            String setStyleScript = "arguments[0].setAttribute('style', arguments[0]);";
            try {
                ((JavascriptExecutor) driver).executeScript(setStyleScript,
                        highlightedElementMap.get(threadId),
                        prevElementStyleMap.get(threadId));
            } catch (Exception e) {
                // Ignore exception is previous element is not available.
            }
        }
        String highlightScript = "arguments[0].style.border='3px solid red';";
        // Save the current element style.
        prevElementStyleMap.put(threadId, element.getAttribute("style"));
        highlightedElementMap.put(threadId, element);
        // Change current element border style.
        try {
            ((JavascriptExecutor) driver).executeScript(highlightScript, element);
        } catch (Exception e) {
            // Ignore exception if element cannot be highlighted.
        }
    }
}
