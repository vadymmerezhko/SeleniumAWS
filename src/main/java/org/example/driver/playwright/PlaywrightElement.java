package org.example.driver.playwright;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import org.example.driver.by.ByParser;
import org.example.utils.ScreenshotManager;
import org.openqa.selenium.*;

import java.util.List;
import java.util.stream.Collectors;

public class PlaywrightElement implements WebElement {

    private final PlaywrightDriver driver;
    private final Locator locator;

    public PlaywrightElement(PlaywrightDriver driver, Locator locator) {
        this.driver = driver;
        this.locator = locator;
    }

    @Override
    public void click() {
        locator.click();
        driver.checkAccessibility();
    }

    public void selectOptionByText(String option) {
        locator.selectOption(option);
    }

    public void setValue(String value) {
        ElementHandle elementHandle = locator.elementHandle();
        locator.evaluate(String.format("elementHandle => elementHandle.value='%s'", value), elementHandle);
    }

    @Override
    public void submit() {
        locator.click();
    }

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

    @Override
    public void clear() {
        locator.clear();
    }

    @Override
    public String getTagName() {
        ElementHandle elementHandle = locator.elementHandle();
        String tagName = (String) locator.evaluate("elementHandle => elementHandle.tagName", elementHandle);
        return tagName.toLowerCase();
    }

    @Override
    public String getAttribute(String name) {
        if (name.equals("value")) {
            return locator.inputValue();
        }
        return locator.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        if (getTagName().equals("option")) {
            String selected = getDomProperty("selected");
            return selected.equals("true");
        }
        return locator.isChecked();
    }

    @Override
    public boolean isEnabled() {
        return locator.isEnabled();
    }

    @Override
    public String getText() {
        return locator.innerText();
    }

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

    @Override
    public boolean isDisplayed() {
        return locator.isVisible() && !locator.isHidden();
    }

    @Override
    public Point getLocation() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Point((int)boundingBox.x, (int)boundingBox.y);
    }

    @Override
    public Dimension getSize() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Dimension((int)boundingBox.width, (int)boundingBox.height);
    }

    @Override
    public Rectangle getRect() {
        BoundingBox boundingBox = locator.boundingBox();
        return new Rectangle((int)boundingBox.x, (int)boundingBox.y, (int)boundingBox.width, (int)boundingBox.height);
    }

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

    @Override
    public String getDomProperty(String propertyName) {
        ElementHandle elementHandle = locator.elementHandle();
        Object property = locator.evaluate(
                "elementHandle => elementHandle." + propertyName, elementHandle);
        return String.valueOf(property);
    }

    @Override
    public String getDomAttribute(String attributeName) {
        ElementHandle elementHandle = locator.elementHandle();
        Object attribute = locator.evaluate(
                String.format("elementHandle => elementHandle.getAttribute('%s')", attributeName), elementHandle);
        return String.valueOf(attribute);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        byte[] data = locator.screenshot();
        return ScreenshotManager.convertScreenshotBytes(target, data);
    }
}
