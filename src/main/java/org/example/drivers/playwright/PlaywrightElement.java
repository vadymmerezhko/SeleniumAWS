package org.example.drivers.playwright;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import org.example.drivers.selectors.ByParser;
import org.example.utils.ScreenshotUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Playwright - WebElement wrapper class.
 */
public class PlaywrightElement implements WebElement {

    private final PlaywrightDriver driver;
    private final Locator locator;

    /**
     * Playwright element constructor.
     * @param driver The Playwright driver instance.
     * @param locator The element locator.
     */
    public PlaywrightElement(PlaywrightDriver driver, Locator locator) {
        this.driver = driver;
        this.locator = locator;
    }

    /**
     * Clicks the web element.
     */
    @Override
    public void click() {
        locator.click();
        driver.checkAccessibility();
    }

    /**
     * Selects option by its text.
     * @param option The text of the option to select.
     */
    public void selectOptionByText(String option) {
        locator.selectOption(option);
    }

    /**
     * Sets elemnt valur.
     * @param value The value to set.
     */
    public void setValue(String value) {
        ElementHandle elementHandle = locator.elementHandle();
        locator.evaluate(String.format("elementHandle => elementHandle.value='%s'", value), elementHandle);
    }

    /**
     * Submits element.
     */
    @Override
    public void submit() {
        locator.click();
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
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Clears the element.
     */
    @Override
    public void clear() {
        locator.clear();
    }

    /**
     * Returns element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        ElementHandle elementHandle = locator.elementHandle();
        String tagName = (String) locator.evaluate("elementHandle => elementHandle.tagName", elementHandle);
        return tagName.toLowerCase();
    }

    /**
     * Returns attribute value by its name.
     * @param name The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String name) {
        if (name.equals("value")) {
            return locator.inputValue();
        }
        return locator.getAttribute(name);
    }

    /**
     * Returns selected true/false flag value.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        if (getTagName().equals("option")) {
            String selected = getDomProperty("selected");
            return selected.equals("true");
        }
        return locator.isChecked();
    }

    /**
     * Returns enabled true/false flag value.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        return locator.isEnabled();
    }

    /**
     * Returns element text.
     * @return The element text.
     */
    @Override
    public String getText() {
        return locator.innerText();
    }

    /**
     * Finds elements by element locator.
     * @param by The element locator.
     * @return The list of found elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        String locatorString = ByParser.getLocatorString(by);
        List<Locator> childLocators;
        try {
            childLocators = locator.locator(locatorString).all();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return childLocators.stream()
                .map(locator -> new PlaywrightElement(driver, locator))
                .collect(Collectors.toList());
    }

    /**
     * Finds element by element locator.
     * @param by The element locator.
     * @return The found element.
     */
    @Override
    public WebElement findElement(By by) {
        String locatorString = ByParser.getLocatorString(by);
        try {
            Locator childLocator = locator.locator(locatorString);
            return new PlaywrightElement(driver, childLocator);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
        return locator.isVisible() && !locator.isHidden();
    }

    /**
     * Returns element location point.
     * @return The location point.
     */
    @Override
    public Point getLocation() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Point((int)boundingBox.x, (int)boundingBox.y);
    }

    /**
     * Returns element size dimension.
     * @return The element size dimension.
     */
    @Override
    public Dimension getSize() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Dimension((int)boundingBox.width, (int)boundingBox.height);
    }

    /**
     * Returns element rectangle.
     * @return The element rectangle.
     */
    @Override
    public Rectangle getRect() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Rectangle((int)boundingBox.x, (int)boundingBox.y, (int)boundingBox.width, (int)boundingBox.height);
    }

    /**
     * REturns element CSS property value by its name.
     * @param styleName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String styleName) {
        try {
            ElementHandle elementHandle = locator.elementHandle();
            return (String) locator.evaluate(String.format(
                    "elementHandle => elementHandle.style.%s", styleName), elementHandle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        return String.valueOf(property);
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
        return String.valueOf(attribute);
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
        byte[] data = locator.screenshot();
        return ScreenshotUtils.convertScreenshotBytes(target, data);
    }
}
