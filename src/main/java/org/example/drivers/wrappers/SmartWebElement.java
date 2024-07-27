package org.example.drivers.wrappers;

import org.example.data.Config;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ClassUtils;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.constants.Settings.*;

/**
 * Smart WebElement class.
 * This class wraps WebElement and adds auto wait, retry on error
 * to make the WebElement more reliable.
 */
public class SmartWebElement extends BaseSmartWebElement {
    static protected final Config config = Config.getInstance();
    private static final int WAIT_FOR_ELEMENT_TIMEOUT_SEC = 15;

    private final SmartWebElement parent;
    private final SmartWebDriverWaiter waiter;

    /**
     * Smart WebElement constructor.
     * @param element The wrapped WebElement instance.
     * @param parent The parent smart element (if any).
     * @param by The element locator.
     * @param driver The WebDriver instance.
     * @param waiter The Waiter instance.
     */
    public SmartWebElement(WebElement element,
                           SmartWebElement parent,
                           By by,
                           WebDriver driver,
                           SmartWebDriverWaiter waiter) {
        super(element, by, driver);
        this.parent = parent;
        this.waiter = waiter;
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
        ClassUtils.performRunnableMethod(
                element::click,
                this::fixClickableWebElement,
                "click",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Submits web element.
     */
    @Override
    public void submit() {
        ClassUtils.performRunnableMethod(
                element::submit,
                this::fixClickableWebElement,
                "submit",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Sends keys to web element.
     * @param keysToSend The keys to send.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        ClassUtils.performConsumerMethod(
                element::sendKeys,
                keysToSend,
                this::fixClickableWebElement,
                "sendKeys",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Clears web element.
     */
    @Override
    public void clear() {
        ClassUtils.performRunnableMethod(
                element::clear,
                this::fixClickableWebElement,
                "clear",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns web element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        return ClassUtils.performSupplierMethod(
                element::getTagName,
                this::fixClickableWebElement,
                "getTagName",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns the web element attribute value by its name.
     * @param name The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String name) {
        return ClassUtils.performFunctionMethod(
                element::getAttribute,
                name,
                this::fixVisibleWebElement,
                "getAttribute",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns the DOM property value by its name.
     * @param name The DOM property name.
     * @return The DOM property value.
     */
    @Override
    public String getDomProperty(String name) {
        return ClassUtils.performFunctionMethod(
                element::getDomProperty,
                name,
                this::fixVisibleWebElement,
                "getDomProperty",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns the DOM attribute value by its name.
     * @param name The DOM attribute name.
     * @return The DOM attribute value.
     */
    @Override
    public String getDomAttribute(String name) {
        return ClassUtils.performFunctionMethod(
                element::getDomAttribute,
                name,
                this::fixVisibleWebElement,
                "getDomAttribute",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns the aria role value.
     * @return The aria role value.
     */
    @Override
    public String getAriaRole() {
        return ClassUtils.performSupplierMethod(
                element::getAriaRole,
                this::fixVisibleWebElement,
                "getAriaRole",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns accessibility name value.
     * @return The accessibility name value.
     */
    @Override
    public String getAccessibleName() {
        return ClassUtils.performSupplierMethod(
                element::getAccessibleName,
                this::fixVisibleWebElement,
                "getAccessibleName",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns true/false selected flag.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        return ClassUtils.performSupplierMethod(
                element::isSelected,
                this::fixVisibleWebElement,
                "isSelected",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns true/false enabled flag.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        return ClassUtils.performSupplierMethod(
                element::isEnabled,
                this::fixVisibleWebElement,
                "isEnabled",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns the web element text.
     * @return The web element text.
     */
    @Override
    public String getText() {
        return ClassUtils.performSupplierMethod(
                element::getText,
                this::fixVisibleWebElement,
                "getText",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Finds web elements by their locator.
     * @param childBy The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By childBy) {
        return element.findElements(childBy).stream().map(childElement ->
                        new SmartWebElement(childElement, this, childBy, driver, waiter))
                .collect(Collectors.toList());
    }

    /**
     * Finds web element by its locator.
     * @param childBy The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By childBy) {
        return new SmartWebElement(element.findElement(childBy), this, childBy, driver, waiter);
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
        return ClassUtils.performSupplierMethod(
                element::isDisplayed,
                null,
                "isDisplayed",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns web element location point.
     * @return The web element location point.
     */
    @Override
    public Point getLocation() {
        return ClassUtils.performSupplierMethod(
                element::getLocation,
                this::fixVisibleWebElement,
                "getLocation",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * REturns web element size dimension.
     * @return The web element size dimension.
     */
    @Override
    public Dimension getSize() {
        return ClassUtils.performSupplierMethod(
                element::getSize,
                this::fixVisibleWebElement,
                "getSize",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns web element rectangle.
     * @return The web element rectangle.
     */
    @Override
    public Rectangle getRect() {
        return ClassUtils.performSupplierMethod(
                element::getRect,
                this::fixVisibleWebElement,
                "getRect",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns web element CSS property value by its name.
     * @param propertyName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String propertyName) {
        return ClassUtils.performFunctionMethod(
                element::getCssValue,
                propertyName,
                this::fixVisibleWebElement,
                "getCssValue",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Returns web elemnt shadow root.
     * @return The shadow root.
     */
    public SearchContext getShadowRoot() {
        return ClassUtils.performSupplierMethod(
                element::getShadowRoot,
                this::fixVisibleWebElement,
                "getShadowRoot",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
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
            return ClassUtils.performFunctionMethod(
                    element::getScreenshotAs,
                    target,
                    this::fixVisibleWebElement,
                    "getScreenshotAs",
                    RETRY_COUNT,
                    RETRY_WAIT_MILLISECONDS);
    }

    /**
     * Scrolls page to web element.
     */
    public void scrollToElement() {
        WebElement nativeElement = element;
        if (element instanceof SmartWebElement) {
            nativeElement = ((SmartWebElement) element).getNativeElement();
        }
        try {
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", nativeElement);
        }
        catch (StaleElementReferenceException |
               ElementNotInteractableException |
               NoSuchElementException e) {
            // Ignore exception;
        }
    }

    /**
     * Sets web element value.
     * @param value The value to set.
     */
    public void setValue(String value) {
        WebElement nativeElement = element;
        if (element instanceof SmartWebElement) {
            nativeElement = ((SmartWebElement) element).getNativeElement();
        }
        ((JavascriptExecutor)driver).executeScript(String.format("arguments[0].value='%s'", value), nativeElement);
    }

    private WebElement waitForChildElementPresence(By childBy) {
        try{
            return element.findElement(childBy);
        }
        catch (NoSuchElementException e) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                WaiterUtils.waitSeconds(RETRY_WAIT_MILLISECONDS);
                List<WebElement> elements = element.findElements(childBy);
                if (!elements.isEmpty()) {
                    if (elements.size() > 1) {
                        throw new SmartRuntimeException(String.format(
                                "More than one child element is found by %s.", childBy));
                    }
                    return elements.get(0);
                }
            }
            throw new SmartRuntimeException(
                    "Exception while waiting for child element presence.", e);
        }
    }

    private void fixClickableWebElement(Exception exception) {
        WaiterUtils.waitMilliSeconds(RETRY_WAIT_MILLISECONDS);

        if (exception instanceof StaleElementReferenceException ||
            exception instanceof ElementNotInteractableException ||
            exception instanceof NoSuchElementException) {

            if (parent == null) {
                scrollToElement();
                element = waiter.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            } else {
                parent.fixClickableWebElement(exception);
                element = parent.findElement(by);
            }
        }
    }

    private void fixVisibleWebElement(Exception exception) {
        WaiterUtils.waitMilliSeconds(RETRY_WAIT_MILLISECONDS);

        if (exception instanceof StaleElementReferenceException ||
            exception instanceof ElementNotInteractableException ||
            exception instanceof NoSuchElementException) {

            if (parent == null) {
                scrollToElement();
                element = waiter.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            } else {
                parent.fixClickableWebElement(exception);
                element = waitForChildElementPresence(by);
            }
        }
    }
}
