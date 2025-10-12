package com.smarte2e.ui.playwright;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import lombok.extern.slf4j.Slf4j;
import com.smarte2e.ui.selectors.SmartByParser;
import com.smarte2e.ui.wrappers.BaseSmartWebElement;
import com.smarte2e.ui.wrappers.PlaywrightLocatorParser;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.utils.TimerUtils;
import com.smarte2e.utils.WebUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.smarte2e.constants.Settings.WAIT_ELEMENT_DELAY_MILLISECONDS;
import static com.smarte2e.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;


/**
 * The Playwright - WebElement wrapper class.
 */
@Slf4j
public class PlaywrightElement extends BaseSmartWebElement {

    private final Locator locator;

    /**
     * Playwright element constructor.
     * @param driver The Playwright driver instance.
     * @param locator The element locator.
     */
    public PlaywrightElement(By by, Locator locator, WebDriver driver) {
        super(null, by, driver);
        this.locator = locator;
        this.element = this;
    }

    /**
     * Returns element locator.
     * @return The element locator.
     */
    public Locator getLocator() {
        return locator;
    }

    /**
     * Selects option by its text.
     * @param option The text of the option to select.
     */
    public void selectOptionByText(String option) {
        locator.selectOption(option);
        log.debug("{} option selected by text: '{}'", locator, option);
    }

    /**
     * Sets elemnt valur.
     * @param value The value to set.
     */
    public void setValue(String value) {
        ElementHandle elementHandle = locator.elementHandle();
        locator.evaluate(String.format(
                "elementHandle => elementHandle.value='%s'", value),
                elementHandle);
        log.debug("{} setValue({})", locator, value);
    }

    /**
     * Clicks the web element.
     */
    @Override
    public void click() {
        locator.click();
    }

    /**
     * Submits element.
     */
    @Override
    public void submit() {
        locator.click();
        log.debug("{} submit().", locator);
        ((PlaywrightDriver) driver).checkAccessibility();
    }

    /**
     * Sends keys to the element.
     * @param keysToSend The keys to send.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        for (CharSequence keysToSendValue : keysToSend ) {
            String value = keysToSendValue.toString();
            try {
                locator.pressSequentially(value);
            } catch (Exception e) {
                throw new SmartRuntimeException(e);
            }
        }
        log.debug("{} sendKeys({})", locator, keysToSend);
    }

    /**
     * Clears the element.
     */
    @Override
    public void clear() {
        locator.clear();
        log.debug("{} clear().", locator);
    }

    /**
     * Returns element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        ElementHandle elementHandle = locator.elementHandle();
        String tagName = ((String) locator.evaluate(
                "elementHandle => elementHandle.tagName", elementHandle))
                .toLowerCase();
        log.debug("{} getTagName(): {}", locator, tagName);
        return tagName;
    }

    /**
     * Returns attribute value by its name.
     * @param attributeName The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String attributeName) {
        String attributeValue;
        if (attributeName.equals("value")) {
            attributeValue = locator.inputValue();
        } else {
            attributeValue = locator.getAttribute(attributeName);
        }
        log.debug("{} getAttribute({}) is: {}", locator, attributeName, attributeValue);
        return attributeValue;
    }

    /**
     * Returns selected true/false flag value.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        boolean isSelected;
        if (getTagName().equals("option")) {
            String selected = getDomProperty("selected");
            isSelected = selected.equals("true");
        } else {
            isSelected = locator.isChecked();
        }
        log.debug("{} isSelected(): {}", locator, isSelected);
        return isSelected;
    }

    /**
     * Returns enabled true/false flag value.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        boolean isEnabled = locator.isEnabled();
        log.debug("{} isEnabled(): {}", locator, isEnabled);
        return isEnabled;
    }

    /**
     * Returns element text.
     * @return The element text.
     */
    @Override
    public String getText() {
        String text = null;
        try {
            text = locator.innerText();
        }
        catch (Exception e) {
            // Ignore exception.
        }
        if (text == null || text.equals("")) {
            text = locator.textContent();
        }
        log.debug("{} getText(): {}", locator, text);
        return text;
    }

    /**
     * Finds elements by element locator.
     * @param by The element locator.
     * @return The list of found elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        long startMilliseconds = System.currentTimeMillis();
        long waitTimeoutMilliseconds = (long) WAIT_ELEMENT_TIMEOUT_SECONDS * 1000;
        String locatorString = SmartByParser.selectorValueFromBy(by);
        List<WebElement> playwrightElements = new ArrayList<>();
        PlaywrightException exception = null;
        List<Locator> locators;

        while ((System.currentTimeMillis() - startMilliseconds) < waitTimeoutMilliseconds) {
            if (WebUtils.isPngImageSelector(locatorString)) {
                try {
                    List<WebElement> webElements = WebUtils.findWebElementsByImage(locatorString, null);

                    if (webElements.size() != 1) {
                        continue;
                    }
                    for (WebElement webElement : webElements) {
                        Locator foundLocator = getLocatorByWebElement(webElement);
                        locatorString = PlaywrightLocatorParser.locatorToString(foundLocator);
                        By bySelector = SmartByParser.fromSelectorValue(locatorString);
                        playwrightElements.add(new PlaywrightElement(bySelector, foundLocator, driver));
                    }
                    return playwrightElements;
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    continue;
                }
            }
            try {
                locators = locator.locator(locatorString).all();
                log.debug("Elements found by {}: {}", by, locators);
                return locators.stream()
                        .map(locator -> new PlaywrightElement(by, locator, driver))
                        .collect(Collectors.toList());
            }
            catch (PlaywrightException e) {
                exception = e;
            }
            TimerUtils.waitMilliSeconds(WAIT_ELEMENT_DELAY_MILLISECONDS);
        }
        throw new SmartRuntimeException(String.format(
                "Playwright %s child web elements are not found by %s", locator, by), exception);
    }

    /**
     * Finds element by element locator.
     * @param by The element locator.
     * @return The found element.
     */
    @Override
    public WebElement findElement(By by) {
        long startMilliseconds = System.currentTimeMillis();
        long waitTimeoutMilliseconds = (long) WAIT_ELEMENT_TIMEOUT_SECONDS * 1000;
        String locatorString = SmartByParser.selectorValueFromBy(by);
        PlaywrightException exception = null;
        Locator childLocator;

        while ((System.currentTimeMillis() - startMilliseconds) < waitTimeoutMilliseconds) {

            if (WebUtils.isPngImageSelector(locatorString)) {
                try {
                    List<WebElement> elements = WebUtils.findWebElementsByImage(locatorString, this);
                    if (elements.size() != 1) {
                        continue;
                    }
                    Locator foundLocator = getLocatorByWebElement(elements.get(0));
                    locatorString = PlaywrightLocatorParser.locatorToString(foundLocator);
                    By bySelector = SmartByParser.fromSelectorValue(locatorString);
                    return new PlaywrightElement(bySelector, foundLocator, driver);
                }
                catch (Exception e) {
                    log.debug(e.getMessage());
                    continue;
                }
            }

            try {
                childLocator = locator.locator(locatorString);
                log.debug("{}.findElement by {}: {}", locator, by, childLocator);
                return new PlaywrightElement(by, childLocator, driver);
            }
            catch (PlaywrightException e) {
                exception = e;
            }
            TimerUtils.waitMilliSeconds(WAIT_ELEMENT_DELAY_MILLISECONDS);
        }
        throw new SmartRuntimeException(String.format(
                "Playwright %s child web element is not found by %s", locator, by), exception);
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
       boolean isDisplayed = locator.isVisible() && !locator.isHidden();
        log.debug("{} isDisplayed(): {}", locator, isDisplayed);
        return isDisplayed;
    }

    /**
     * Returns element location point.
     * @return The location point.
     */
    @Override
    public Point getLocation() {
        Map<String, Integer> location = (Map<String, Integer>) locator.evaluate("""
                el => {
                    return {
                        x: Math.round(el.getBoundingClientRect().left),
                        y: Math.round(el.getBoundingClientRect().top)
                    };
                }
                """.stripIndent());
        Point point = new Point(location.get("x"), location.get("y"));
        log.debug("Playwright element {} location: {}.", locator, point);
        return point;
    }

    /**
     * Returns element size dimension.
     * @return The element size dimension.
     */
    @Override
    public Dimension getSize() {
        int width = (Integer) locator.evaluate("el => Math.round(el.offsetWidth)");
        int height = (Integer) locator.evaluate("el => Math.round(el.offsetHeight)");
        Dimension size = new Dimension(width, height);
        log.debug("Playwright element {} size: {}.", locator, size);
        return size;
    }

    /**
     * Returns element rectangle.
     * @return The element rectangle.
     */
    @Override
    public Rectangle getRect() {
        Point location = getLocation();
        Dimension size = getSize();
        Rectangle rect = new Rectangle(location.x, location.y,
                size.height, size.width);
        log.debug("{} getRect(): {}", locator, rect);
        return rect;
    }

    /**
     * REturns element CSS property value by its name.
     * @param styleName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String styleName) {
        ElementHandle elementHandle = locator.elementHandle();
        String cssValue = (String) locator.evaluate(String.format(
                "elementHandle => elementHandle.style.%s", styleName), elementHandle);
        log.debug("{} getCssValue(): {}", locator, cssValue);
        return cssValue;
    }

    /**
     * Returns DOM property by its name.
     * @param propertyName The DOM property name.
     * @return The DOM property value.
     */
    @Override
    public String getDomProperty(String propertyName) {
        ElementHandle elementHandle = locator.elementHandle();
        Object property = locator.evaluate(
                "elementHandle => elementHandle." + propertyName, elementHandle);
        String domProperty = String.valueOf(property);
        log.debug("{} getDomProperty(): {}", locator, domProperty);
        return domProperty;
    }

    /**
     * Returns DOM attribute by its name.
     * @param attributeName The DOM attribute name.
     * @return The DOM attribute value.
     */
    @Override
    public String getDomAttribute(String attributeName) {
        ElementHandle elementHandle = locator.elementHandle();
        Object attribute = locator.evaluate(
                String.format("elementHandle => elementHandle.getAttribute('%s')", attributeName),
                elementHandle);
        String domAttributeValue = String.valueOf(attribute);
        log.debug("{} getDomAttribute(): {}", locator, domAttributeValue);
        return domAttributeValue;
    }

    /**
     * Returns the screenshot data.
     * @param target The screenshot target.
     * @return The screenshot data.
     * @param <X> The screenshot data type.
     * @throws WebDriverException The exception in case of error.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        try {
            byte[] data = locator.screenshot(new Locator.ScreenshotOptions());

            if (target == OutputType.BYTES) {
                log.debug("Screenshot bytes are returned.");
                return (X) data;
            } else if (target == OutputType.BASE64) {
                X base64 = (X) Base64.getEncoder().encodeToString(data);
                log.debug("Base 64 screenshot is returned.");
                return base64;
            } else if (target == OutputType.FILE) {
                File file = OutputType.FILE.convertFromBase64Png(Base64.getEncoder().encodeToString(data));
                log.debug("Screenshot file  is returned: {}", file.getPath());
                return (X) file;
            } else {
                throw new SmartRuntimeException(String.format(
                        "Unsupported OutputType: %s", target));
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot take pag screenshot fot target %s", target.toString()), e);
        }
    }

    private Locator getLocatorByWebElement(WebElement element) {
        String tagName = element.getTagName();
        Locator allElementsLocator = locator.locator(tagName);
        return PlaywrightDriver.getLocatorByWebElement(allElementsLocator, element);
    }
}
