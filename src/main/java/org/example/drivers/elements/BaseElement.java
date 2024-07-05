package org.example.drivers.elements;

import org.example.data.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.wrappers.BaseWebElement;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;

/**
 * Base web element class.
 */
public abstract class BaseElement implements WebElement, WrapsElement {
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, WebElement> highlightedElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, String> prevElementStyleMap = new ConcurrentHashMap<>();

    static protected final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
    private WebElement element = null;
    private final By by;
    protected WebDriver driver;
    protected long threadId;

    /**
     * Base element constructor by its locator.
     * @param by The element locator.
     */
    public BaseElement(By by) {
        this.by = by;
        driver = WebDriverFactory.getDriver();
        threadId = Thread.currentThread().threadId();
    }

    /**
     * Returns wrapped web element.
     * @return The wrapped web element.
     */
    @Override
    public WebElement getWrappedElement() {
        return getElement();
    }

    /**
     * Clicks web element.
     */
    @Override
    public void click() {
        getElement().click();
    }

    /**
     * Submits web element.
     */
    @Override
    public void submit() {
        getElement().submit();
    }

    /**
     * Sends keys to web element.
     * @param keysToSend The keys to send.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        getElement().sendKeys(keysToSend);
    }

    /**
     * Clears the web element.
     */
    @Override
    public void clear() {
        getElement().clear();
    }

    /**
     * Returns web element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        return getElement().getTagName();
    }

    /**
     * Returns the eb element attribute value by its name.
     * @param name The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String name) {
        return getElement().getAttribute(name);
    }

    /**
     * Returns the DOM property by its name.
     * @param name The DOM property name.
     * @return The DOM property value.
     */
    @Override
    public String getDomProperty(String name) {
        return getElement().getDomProperty(name);
    }

    /**
     * Returns DOM attribute value by its name.
     * @param name The DOM attribute name.
     * @return The DOM attribute value.
     */
    @Override
    public String getDomAttribute(String name) {
        return getElement().getDomAttribute(name);
    }

    /**
     * Returns area role.
     * @return The area role.
     */
    @Override
    public String getAriaRole() {
        return getElement().getAriaRole();
    }

    /**
     * Returns the accessible name.
     * @return The accessible name.
     */
    @Override
    public String getAccessibleName() {
        return getElement().getAccessibleName();
    }

    /**
     * Returns true/false selected flag.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        return getElement().isSelected();
    }

    /**
     * Returns true/false enabled flag.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    /**
     * Returns element text.
     * @return The element text.
     */
    @Override
    public String getText() {
        return getElement().getText();
    }

    /**
     * Finds a list of web elements by their locator.
     * @param by The element locator.
     * @return The list of found elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        return getElement().findElements(by);
    }

    /**
     * Finds the web element by its locator.
     * @param by The element locator.
     * @return The web element.
     */
    @Override
    public WebElement findElement(By by) {
        return getElement().findElement(by);
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
        return getElement().isDisplayed();
    }

    /**
     * Returns element location point.
     * @return The element location point.
     */
    @Override
    public Point getLocation() {
        return getElement().getLocation();
    }

    /**
     * Returns element size dimension.
     * @return The element size dimension.
     */
    @Override
    public Dimension getSize() {
        return getElement().getSize();
    }

    /**
     * Returns element rectangle.
     * @return The element rectangle.
     */
    @Override
    public Rectangle getRect() {
        return getElement().getRect();
    }

    /**
     * Returns element CSS property value by its name.
     * @param propertyName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String propertyName) {
        return getElement().getCssValue(propertyName);
    }

    /**
     * Returns element shadow root search context.
     * @return The search context.
     */
    public SearchContext getShadowRoot() {
        return getElement().getShadowRoot();
    }

    /**
     * Returs elemwnt screenshot.
     * @param target The screenshot target.
     * @return The screenshot data.
     * @param <X> The screenshot data type.
     * @throws WebDriverException in case of error.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return getElement().getScreenshotAs(target);
    }

    protected WebElement getElement() {
        if (element == null) {
            element = WebDriverFactory.getDriver().findElement(by);
            handleElement();
            return element;
        }
        return element;
    }

    /**
     * Handles web element after an action on it.
     */
    protected void handleElement() {
        if (handledElementMap.isEmpty() || handledElementMap.get(threadId) != element) {
            handledElementMap.put(threadId, element);

            if (config.getHighlightElement()) {
                highlightElement();
            }
            WaiterUtils.waitMilliSeconds(config.getStepDelay());
        }
    }

    protected void highlightElement() {
        if (highlightedElementMap.containsKey(threadId) &&
                prevElementStyleMap.get(threadId) != null) {
            // Restore element style.
            try {
                String style = prevElementStyleMap.get(threadId);
                ((BaseWebElement) highlightedElementMap.get(threadId))
                        .setStyle("border", style);
            } catch (Exception e) {
                // Ignore exception if not possible to restore style.
            }
        }
        // Save the current element style.
        String style = null;
        try {
            style = ((BaseWebElement) element).getStyle("border");
        } catch (Exception e) {
            // Ignore exception if style is not available.
        }
        prevElementStyleMap.put(threadId, style);

        // Change current element border style.
        highlightedElementMap.put(threadId, element);
        try {
            ((BaseWebElement) element).setStyle("border", "3px solid red");
        } catch (Exception e) {
            // Ignore exception is previous element is not available.
        }
    }
}
