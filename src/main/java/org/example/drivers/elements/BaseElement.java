package org.example.drivers.elements;

import org.example.data.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.selectors.ByAI;
import org.example.drivers.wrappers.RobustWebElement;
import org.example.pages.BasePage;
import org.example.utils.ClassUtils;
import org.example.utils.WebUtils;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;
import static org.example.constants.Settings.PAGE_OBJECT_FOLDER_PATH;

/**
 * Base web element class.
 */
public abstract class BaseElement implements WebElement, WrapsElement {
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> elementSelectorMap = new ConcurrentHashMap<>();

    static protected final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
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
                if (by instanceof ByAI byAI) {
                    setElementName();

                    if (byAI.getBy() == null && config.getDebugMode()) {
                        setElementSelector(byAI);
                    }
                }
                element = WebDriverFactory.getDriver().findElement(by);
                handleElement();
                return element;
            }
            return element;
        }
        catch (NoSuchElementException e) {
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

        if (element instanceof RobustWebElement) {
            webElement = ((RobustWebElement)element).getNativeElement();
        }

        WebUtils.highlightElement(webElement);
    }

    private void setElementName() {
        elementName = String.format("%s.%s",
                page.getClass().getSimpleName(),
                ClassUtils.getClassFieldName(page, this));
    }

    private void setElementSelector(ByAI byAI) {
        String text = byAI.getText();
        boolean isReadFromFile;

        try {
            byAI.setElementName(elementName);
            String selector;

            if (elementSelectorMap.containsKey(elementName)) {
                selector = elementSelectorMap.get(elementName);
                isReadFromFile = selector != null;
            } else {
                selector = WebUtils.readElementSelectorFromFile(PAGE_OBJECT_FOLDER_PATH, elementName);
                isReadFromFile = selector != null;
            }
            if (selector == null) {
                WebElement element = WebUtils.selectWebElement(elementName);
                selector = WebUtils.getElementSelectorByAllMeans(elementName, element, text);
            }
            if (selector == null) {
                throw new RuntimeException(String.format(
                        "'%s' element selector is NULL (not detected).", elementName));
            }
            By bySelector = WebUtils.convertSelectorTemplateToBy(selector, text);
            byAI.setBy(bySelector);
            String selectorTemplate = WebUtils.getSelectorTemplate(selector, text);

            if (!isReadFromFile) {
                elementSelectorMap.put(elementName, selectorTemplate);
                WebUtils.saveElementSelectorToFile(PAGE_OBJECT_FOLDER_PATH, elementName, selectorTemplate);
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(String.format(
                    "Can not set element selector:\n%s", e.getMessage()));
        }
    }

    private void fixElementSelector() {

        if (by instanceof ByAI byAI) {
            String elementName = byAI.getElementName();
            String text = byAI.getText();
            WebElement webElement = WebUtils.selectWebElement(byAI.getElementName());
            String selector = WebUtils.getElementSelectorByAllMeans(
                    elementName, webElement, text);
            if (selector == null) {
                return;
            }
            element = webElement;
            By bySelector = WebUtils.convertSelectorTemplateToBy(selector, text);
            byAI.setBy(bySelector);
            String selectorTemplate = WebUtils.getSelectorTemplate(selector, text);
            WebUtils.saveElementSelectorToFile(
                    PAGE_OBJECT_FOLDER_PATH, elementName, selectorTemplate);
        }
    }
}
