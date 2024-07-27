package org.example.drivers.elements;

import org.example.data.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.selectors.SmartBy;
import org.example.drivers.wrappers.SmartWebElement;
import org.example.exceptions.SmartRuntimeException;
import org.example.pages.BasePage;
import org.example.utils.ClassUtils;
import org.example.utils.WebUtils;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base web element class.
 */
public abstract class BaseElement implements WebElement, WrapsElement {
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> elementSelectorMap = new ConcurrentHashMap<>();

    static protected final Config config = Config.getInstance();
    private WebElement element = null;
    protected By by;
    protected final BasePage page;
    protected WebDriver driver;
    protected String elementName;
    protected long threadId;

    public static Map<String, String> getElementSelectorMap() {
        return elementSelectorMap;
    }


    /**
     * Base element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public BaseElement(BasePage page, By by) {
        this.page = page;
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

    /**
     * Returns WebElement instance.
     * @return The WebElement instance.
     */
    protected WebElement getElement() {
        try {
            if (element == null) {
                if (by instanceof SmartBy smartBy) {
                    setElementName();

                    if (smartBy.getBy() == null) {
                        setElementSelector(smartBy);
                    }
                }
                element = WebDriverFactory.getDriver().findElement(by);
                handleElement();
                return element;
            }
            return element;
        }
        catch (NoSuchElementException | InvalidSelectorException e) {
            if (config.getDebugMode()) {
                fixElementSelector();
            } else {
                throw e;
            }
        }
        return null;
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

    /**
     * Highlights the element.
     */
    protected void highlightElement() {
        WebElement webElement = element;

        if (element instanceof SmartWebElement) {
            webElement = ((SmartWebElement)element).getNativeElement();
        }

        WebUtils.highlightElement(webElement);
    }

    private void setElementName() {
        elementName = String.format("%s.%s",
                page.getClass().getSimpleName(),
                ClassUtils.getClassFieldName(page, this));
    }

    private void setElementSelector(SmartBy smartBy) {
        String text = smartBy.getText();
        boolean isSelectorUpdated = false;
        boolean isReadFromFile = true;

        try {
            smartBy.setElementName(elementName);
            String selector;

            if (elementSelectorMap.containsKey(elementName)) {
                selector = elementSelectorMap.get(elementName);
            } else {
                selector = WebUtils.readElementSelectorFromFile(
                        config.getPagesFolderPath(), elementName);
                isSelectorUpdated = true;
            }
            if (selector == null) {
                if (config.getDebugMode()) {
                    selector = WebUtils.selectElementAndGetSelector(elementName, text);
                    isSelectorUpdated = true;
                    isReadFromFile = false;
                }
            }
            if (selector == null) {
                throw new SmartRuntimeException(String.format(
                        "'%s' element selector is not detected.", elementName));
            }
            By bySelector = WebUtils.convertSelectorTemplateToBy(selector, text);
            smartBy.setBy(bySelector);
            String selectorTemplate = WebUtils.getSelectorTemplate(selector, text);

            if (isSelectorUpdated) {
                elementSelectorMap.put(elementName, selectorTemplate);
            }

            if (!isReadFromFile) {
                WebUtils.saveElementSelectorToFile(
                        config.getPagesFolderPath(), elementName, selectorTemplate);
            }
        }
        catch (Throwable e) {
            throw new SmartRuntimeException("Can not set element selector.", e);
        }
    }

    private void fixElementSelector() {

        if (by instanceof SmartBy smartBy) {
            String elementName = smartBy.getElementName();
            String text = smartBy.getText();
            String selector = WebUtils.selectElementAndGetSelector(elementName, text);
            if (selector == null) {
                return;
            }
            try {
                element = WebUtils.getElementBySelector(selector, text);
            } catch (Exception e) {
                // Ignore exception
            }
            By bySelector = WebUtils.convertSelectorTemplateToBy(selector, text);
            smartBy.setBy(bySelector);
            String selectorTemplate = WebUtils.getSelectorTemplate(selector, text);
            WebUtils.saveElementSelectorToFile(
                    config.getPagesFolderPath(), elementName, selectorTemplate);
            elementSelectorMap.put(elementName, selector);
        }
    }
}
