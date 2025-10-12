package com.smarte2e.ui.wrappers;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.configs.Config;
import com.smarte2e.ui.selectors.SmartByParser;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.utils.ClassUtils;
import com.smarte2e.utils.DataValidator;
import com.smarte2e.utils.TimerUtils;
import com.smarte2e.utils.WebUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.smarte2e.constants.Settings.*;

/**
 * Smart WebElement class.
 * This class wraps WebElement and adds auto wait, retry on error
 * to make the WebElement more reliable.
 */
@Slf4j
public class SmartWebElement extends BaseSmartWebElement {
    static protected final Config config = Config.getInstance();
    private static final int WAIT_FOR_ELEMENT_TIMEOUT_SEC = 15;
    private final SmartWebElement parent;

    /**
     * Smart WebElement constructor.
     * @param element The wrapped WebElement instance.
     * @param parent The parent smart element (if any).
     * @param by The element locator.
     * @param driver The WebDriver instance.
     */
    public SmartWebElement(WebElement element,
                           SmartWebElement parent,
                           By by,
                           WebDriver driver) {
        super(element, by, driver);
        this.parent = parent;
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
                this::doClick,
                this::fixClickableWebElement,
                "click",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.click()", element);
    }

    private void doClick() {
        element.click();
    }

    /**
     * Submits web element.
     */
    @Override
    public void submit() {
        ClassUtils.performRunnableMethod(
                this::doSubmit,
                this::fixClickableWebElement,
                "submit",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.submit()", element);
    }

    private void doSubmit() {
        element.submit();
    }

    /**
     * Sends keys to web element.
     * @param keysToSend The keys to send.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        ClassUtils.performConsumerMethod(
                this::doSendKeys,
                keysToSend,
                this::fixClickableWebElement,
                "sendKeys",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.sendKeys('{}')", element, keysToSend);
    }

    private void doSendKeys(CharSequence... keysToSend) {
        element.sendKeys(keysToSend);
    }

    /**
     * Clears web element.
     */
    @Override
    public void clear() {
        ClassUtils.performRunnableMethod(
                this::doClear,
                this::fixClickableWebElement,
                "clear",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.clear()", element);
    }

    private void doClear() {
        element.clear();
    }

    /**
     * Returns web element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        String tagName = ClassUtils.performSupplierMethod(
                this::doGetTagName,
                this::fixClickableWebElement,
                "getTagName",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getTagName(): {}", element, tagName);
        return tagName;
    }

    private String doGetTagName() {
        return element.getTagName();
    }

    /**
     * Returns the web element attribute value by its name.
     * @param name The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String name) {
        try {
            String tagAttribute = ClassUtils.performFunctionMethod(
                    this::doGetAttribute,
                    name,
                    this::fixVisibleWebElement,
                    "getAttribute",
                    RETRY_COUNT,
                    RETRY_WAIT_MILLISECONDS);
            log.debug("{}.getAttribute('{}'): {}", element, name, tagAttribute);
            return tagAttribute;
        }
        catch (Exception e) {
            log.debug("{}.getAttribute('{}'): null", element, name);
            return null;
        }
    }

    private String doGetAttribute(String name) {
        return element.getAttribute(name);
    }

    /**
     * Returns the DOM property value by its name.
     * @param name The DOM property name.
     * @return The DOM property value.
     */
    @Override
    public String getDomProperty(String name) {
        String domProperty = ClassUtils.performFunctionMethod(
                this::doGetDomProperty,
                name,
                this::fixVisibleWebElement,
                "getDomProperty",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getDomProperty('{}'): {}", element, name, domProperty);
        return domProperty;
    }

    private String doGetDomProperty(String name) {
        return element.getDomProperty(name);
    }

    /**
     * Returns the DOM attribute value by its name.
     * @param name The DOM attribute name.
     * @return The DOM attribute value.
     */
    @Override
    public String getDomAttribute(String name) {
        String domAttribute = ClassUtils.performFunctionMethod(
                this::doGetDomAttribute,
                name,
                this::fixVisibleWebElement,
                "getDomAttribute",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getDomAttribute('{}'): {}", element, name, domAttribute);
        return domAttribute;
    }

    private String doGetDomAttribute(String name) {
        return element.getDomAttribute(name);
    }

    /**
     * Returns the aria role value.
     * @return The aria role value.
     */
    @Override
    public String getAriaRole() {
        String arialRole = ClassUtils.performSupplierMethod(
                this::doGetAriaRole,
                this::fixVisibleWebElement,
                "getAriaRole",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getAriaRole(): {}", element, arialRole);
        return arialRole;
    }

    private String doGetAriaRole() {
        return element.getAriaRole();
    }

    /**
     * Returns accessibility name value.
     * @return The accessibility name value.
     */
    @Override
    public String getAccessibleName() {
        String accessibleName = ClassUtils.performSupplierMethod(
                this::doGetAccessibleName,
                this::fixVisibleWebElement,
                "getAccessibleName",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getAccessibleName(): {}", element, accessibleName);
        return accessibleName;
    }

    private String doGetAccessibleName() {
        return element.getAccessibleName();
    }

    /**
     * Returns true/false selected flag.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        boolean isSelected = ClassUtils.performSupplierMethod(
                this::doIsSelected,
                this::fixVisibleWebElement,
                "isSelected",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.isSelected(): {}", element, isSelected);
        return isSelected;
    }

    private boolean doIsSelected() {
        return element.isSelected();
    }

    /**
     * Returns true/false enabled flag.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        boolean isEnabled = ClassUtils.performSupplierMethod(
                this::doIsEnabled,
                this::fixVisibleWebElement,
                "isEnabled",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.isEnabled(): {}", element, isEnabled);
        return isEnabled;
    }

    private boolean doIsEnabled() {
        return element.isEnabled();
    }

    /**
     * Returns the web element text.
     * @return The web element text.
     */
    @Override
    public String getText() {
        String text = ClassUtils.performSupplierMethod(
                this::doGetText,
                this::fixVisibleWebElement,
                "getText",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getText(): {}", element, text);
        return text;
    }

    private String doGetText() {
        return element.getText();
    }

    /**
     * Finds web elements by their locator.
     * @param childBy The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By childBy) {
        String selector = SmartByParser.selectorValueFromBy(childBy);
        List<WebElement> elements;

        if (WebUtils.isPngImageSelector(selector)) {
            elements =  WebUtils.findWebElementsByImage(selector, element);
        }
        else {
            elements = element.findElements(childBy);
        }
        List<WebElement> smartElements = elements.stream().map(childElement ->
                new SmartWebElement(childElement, this, childBy, driver))
                .collect(Collectors.toList());
        log.debug("{} child web elements are found by selector{}:\n{}.",
                smartElements.size(), by, smartElements);
        return smartElements;
    }

    /**
     * Finds web element by its locator.
     * @param childBy The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By childBy) {
        long startMilliseconds = System.currentTimeMillis();
        long waitTimeoutMilliseconds = (long) WAIT_ELEMENT_TIMEOUT_SECONDS * 1000;
        String selector = SmartByParser.selectorValueFromBy(childBy);
        List<WebElement> elements;
        WebElement foundElement;

        while ((System.currentTimeMillis() - startMilliseconds) < waitTimeoutMilliseconds) {

            if (WebUtils.isPngImageSelector(selector)) {
                elements = WebUtils.findWebElementsByImage(selector, element);
            }
            else {
                elements = element.findElements(childBy);
            }
            int size = elements.size();

            if (size == 1) {
                foundElement = elements.get(0);
                log.debug("Child web element {} is found by selector {}.", foundElement, by);
                return new SmartWebElement(foundElement, null, by, driver);
            }
            TimerUtils.waitMilliSeconds(WAIT_ELEMENT_DELAY_MILLISECONDS);
        }
        foundElement = new SmartWebElement(driver.findElement(by), null, by, driver);
        log.debug("Child web element {} is found by selector {}.", foundElement, by);
        return foundElement;
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
        boolean isDisplayed = ClassUtils.performSupplierMethod(
                this::doIsDisplayed,
                null,
                "isDisplayed",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.isDisplayed(): {}", element, isDisplayed);
        return isDisplayed;
    }

    private boolean doIsDisplayed() {
        return element.isDisplayed();
    }

    /**
     * Returns web element location point.
     * @return The web element location point.
     */
    @Override
    public Point getLocation() {
        Point location = ClassUtils.performSupplierMethod(
                this::doGetLocation,
                this::fixVisibleWebElement,
                "getLocation",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getLocation(): {}", element, location);
        return location;
    }

    private Point doGetLocation() {
        return element.getLocation();
    }

    /**
     * REturns web element size dimension.
     * @return The web element size dimension.
     */
    @Override
    public Dimension getSize() {
        Dimension size = ClassUtils.performSupplierMethod(
                this::doGetSize,
                this::fixVisibleWebElement,
                "getSize",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getSize(): {}", element, size);
        return size;
    }

    private Dimension doGetSize() {
        return element.getSize();
    }

    /**
     * Returns web element rectangle.
     * @return The web element rectangle.
     */
    @Override
    public Rectangle getRect() {
        Rectangle rect = ClassUtils.performSupplierMethod(
                this::doGetRect,
                this::fixVisibleWebElement,
                "getRect",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getRect(): {}", element, rect);
        return rect;
    }

    private Rectangle doGetRect() {
        return element.getRect();
    }

    /**
     * Returns web element CSS property value by its name.
     * @param propertyName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String propertyName) {
        String cssValue = ClassUtils.performFunctionMethod(
                this::doGetCssValue,
                propertyName,
                this::fixVisibleWebElement,
                "getCssValue",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getCssValue('{}'): {}", element, propertyName, cssValue);
        return cssValue;
    }

    private String doGetCssValue(String propertyName) {
        return element.getCssValue(propertyName);
    }

    /**
     * Returns web element shadow root.
     * @return The shadow root.
     */
    @Override
    public SearchContext getShadowRoot() {
        SearchContext shadowRoot = ClassUtils.performSupplierMethod(
                this::doGetShadowRoot,
                this::fixVisibleWebElement,
                "getShadowRoot",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.getShadowRoot(): {}", element, shadowRoot);
        return shadowRoot;
    }

    private SearchContext doGetShadowRoot() {
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
            X screenshot = ClassUtils.performFunctionMethod(
                    this::doGetScreenshotAs,
                    target,
                    this::fixVisibleWebElement,
                    "getScreenshotAs",
                    RETRY_COUNT,
                    RETRY_WAIT_MILLISECONDS);
            log.debug("{}.getScreenshotAs({}", element, target);
            return screenshot;
    }

    private <X> X doGetScreenshotAs(OutputType<X> target) throws WebDriverException {
        return element.getScreenshotAs(target);
    }

    /**
     * Scrolls page to web element.
     */
    public void scrollToElement() {
        ClassUtils.performRunnableMethod(
                this::doScrollToElement,
                this::fixClickableWebElement,
                "scrollToElement",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.scrollToElement()", element);
    }

    private void doScrollToElement() {
        WebElement nativeElement = element;
        if (element instanceof SmartWebElement smartWebElement) {
            nativeElement = smartWebElement.getNativeElement();
        }
        try {
            ((JavascriptExecutor)driver).executeScript(
                    "arguments[0].scrollIntoView(true);",
                    nativeElement);
        }
        catch (StaleElementReferenceException |
               ElementNotInteractableException |
               NoSuchElementException e) {
            // Ignore exception;
        }
        log.debug("Page scrolled to element {}.", element);
    }

    /**
     * Sets web element attribute value.
     * @param name The attribute name.
     * @param value The attribute value to set.
     */
    public void setAttributeValue(String name, String value) {
        DataValidator.notNull(value, "value");
        ClassUtils.performBiConsumerMethod(
                this::doSetAttributeValue,
                name,
                value,
                this::fixClickableWebElement,
                "setAttributeValue",
                RETRY_COUNT,
                RETRY_WAIT_MILLISECONDS);
        log.debug("{}.setAttributeValue('{}', '{}')", element, name, value);
    }

    private void doSetAttributeValue(String name, String value) {
        WebElement nativeElement = element;

        if (element instanceof SmartWebElement smartWebElement) {
            nativeElement = smartWebElement.getNativeElement();
        }
        ((JavascriptExecutor)driver).executeScript(
                String.format("arguments[0].%s = '%s'", name, value), nativeElement);
        synchro.waitForAttributeValue(element, name, value, WAIT_ELEMENT_TIMEOUT_SECONDS);
    }

    /**
     * Sets web element value.
     * @param value The value to set.
     */
    public void setValue(String value) {
        setAttributeValue("value", value);
        log.debug("Element {} value is set to '{}'.", element, value);
    }

    private WebElement waitForChildElementPresence(By childBy) {
        try {
            return element.findElement(childBy);
        }
        catch (NoSuchElementException e) {
            for (int i = 0; i < RETRY_COUNT; i++) {
                TimerUtils.waitSeconds(RETRY_WAIT_MILLISECONDS);
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

        if (exception instanceof StaleElementReferenceException ||
            exception instanceof ElementNotInteractableException ||
            exception instanceof NoSuchElementException) {

            synchro.waitForPageLoad();

            if (parent == null) {
                scrollToElement();
                element = synchro.waitForElementToBeClickableBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            } else {
                parent.fixClickableWebElement(exception);
                element = parent.findElement(by);
            }
        }
    }

    private void fixVisibleWebElement(Exception exception) {

        if (exception instanceof StaleElementReferenceException ||
            exception instanceof ElementNotInteractableException ||
            exception instanceof NoSuchElementException) {
            WebElement fixedElement;

            synchro.waitForPageLoad();

            if (parent == null) {
                scrollToElement();
                fixedElement = synchro.waitForElementVisibilityBy(by, WAIT_FOR_ELEMENT_TIMEOUT_SEC);
            } else {
                parent.fixClickableWebElement(exception);
                fixedElement = waitForChildElementPresence(by);
            }
            WebUtils.waitForElementNotMoving(fixedElement);
            WebUtils.waitForElementNotSizing(fixedElement);
            WebUtils.waitForElementStableStyle(fixedElement);
            element = fixedElement;
        }
    }
}
