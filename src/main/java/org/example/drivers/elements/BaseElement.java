package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.selectors.SmartBy;
import org.example.drivers.wrappers.SmartWebElement;
import org.example.exceptions.SmartRuntimeException;
import org.example.pages.BasePage;
import org.example.utils.ClassUtils;
import org.example.utils.FileSystemUtils;
import org.example.utils.WebUtils;
import org.example.utils.WaiterUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Base web element class.
 */
@Slf4j
public abstract class BaseElement implements WebElement, WrapsElement {
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> elementSelectorMap =
            readAllElementSelectorsFromFiles(Config.getInstance().getPagesFolderPath());

    static protected final Config config = Config.getInstance();
    private WebElement element = null;
    protected By by;
    protected BasePage page;
    protected WebDriver driver;
    protected String elementName;

    /**
     * Reads asynchronously all element selector from JSON file
     * to "element name-selector" map.
     * If no files or they are empty then empty map is returned.
     * @param folderPath The folder path.
     * @return  The element name - selector map.
     */
    private static ConcurrentMap<String, String> readAllElementSelectorsFromFiles(String folderPath) {
        ConcurrentMap<String, String> elementMap = new ConcurrentHashMap<>();
        Set<String> fileNames = FileSystemUtils.getFileNamesInFolder(folderPath);

        Thread thread = new Thread(() -> {
            try {
                log.debug("Asynchronous page object element selectors reading began.");

                for (String fileName : fileNames) {
                    if (FileSystemUtils.getFileExtension(fileName).equals("json")) {
                        String filePath = String.format("%s/%s", folderPath, fileName);
                        String pageName = FileSystemUtils.getFileNameWithoutExtension(fileName);
                        String fileContent = FileSystemUtils.readFile(filePath);

                        if (fileContent.trim().isEmpty()) {
                            continue;
                        }
                        JSONObject json = new JSONObject(fileContent);

                        for (String fieldName : json.keySet()) {
                            String selector = json.get(fieldName).toString();
                            String elementName = String.format("%s.%s", pageName, fieldName);
                            elementMap.put(elementName, selector);
                            log.debug("Element {} selector {} is asynchronously read from file {}",
                                    elementName, selector, filePath);
                        }
                    }
                    log.debug("Asynchronous page object element selectors reading finished.");
                }
            } catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read all web page files from: %s", folderPath), e);
            }
        });
        thread.start();
        return elementMap;
    }

    /**
     * Base element constructor by its page and auto selector.
     */
    public BaseElement() {
        this.by = SmartBy.auto();
        driver = WebDriverFactory.getDriver();
    }

    /**
     * Base element constructor by its selector.
     * @param by The element selector.
     */
    public BaseElement(By by) {
        this.by = by;
        driver = WebDriverFactory.getDriver();
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
        validatePage();

        try {
            if (element == null) {
                if (by instanceof SmartBy smartBy) {
                    setElementName();

                    if (smartBy.getBy() == null) {
                        setElementSelector(smartBy);
                    }
                }
                element = driver.findElement(by);
                handleElement();
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
        long threadId = Thread.currentThread().threadId();

        if (handledElementMap.isEmpty() || handledElementMap.get(threadId) != element) {
            handledElementMap.put(threadId, element);

            if (config.getHighlightElement()) {
                highlightElement();
            }
            WaiterUtils.waitMilliSeconds(config.getStepDelay());
        }
    }

    /**
     * Sets parent web page.
     * @param page The web page.
     */
    public void setPage(BasePage page) {
        this.page = page;
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

    /**
     * Saves element selector to JSON file.
     * @param elementName The element name.
     * @param selector The element selector.
     */
    private void saveElementSelectorToFile(String folderPath, String elementName, String selector) {
        String filePath = null;
        try {
            String[] nameParts = elementName.split("\\.");

            if (nameParts.length != 2) {
                throw new SmartRuntimeException(String.format(
                        "Invalid web element name: '%s'", elementName));
            }
            String fileName = String.format("%s.json", nameParts[0]);
            String fieldName = nameParts[1];
            JSONObject json;
            filePath = String.format("%s/%s", folderPath, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);
                json = new JSONObject(jsonString);
            } else {
                json = new JSONObject();
            }
            json.put(fieldName, selector);
            FileSystemUtils.createFile(filePath, json.toString());
            log.debug("Element {} selector {} is saved to file {}.",
                    elementName, selector, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not save web %s element selector %s to file: %s",
                    elementName, selector, filePath), e);
        }
    }

    /**
     * Reads element selector from JSON file or returns NULL
     * if file does not exist or element selector undefined.
     * @param folderPath The folder path.
     * @param elementName The element name.
     * @return The element selector.
     */
    private String readElementSelectorFromFile(String folderPath, String elementName) {
        String filePath = null;
        try {
            String[] nameParts = elementName.split("\\.");

            if (nameParts.length != 2) {
                throw new SmartRuntimeException(String.format(
                        "Invalid web element name: '%s'", elementName));
            }
            String fileName = String.format("%s.json", nameParts[0]);
            String fieldName = nameParts[1];
            JSONObject json;
            filePath = String.format("%s/%s", folderPath, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);

                if (jsonString.trim().isEmpty()) {
                    return null;
                }
                json = new JSONObject(jsonString);
                try {
                    String selector = json.get(fieldName).toString();
                    log.debug("Element {} selector {} is read from file {}",
                            elementName, selector, fileName);
                    return selector;
                }
                catch (JSONException e) {
                    log.debug("File {} has invalid JSON object format: {}",
                            filePath, jsonString);
                    return null;
                }
            }
            else {
                log.debug("Page object {} file {} does not exist.",
                        nameParts[0], filePath);
                return null;
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not read web %s element selector from file: %s",
                    elementName, filePath), e);
        }
    }

    private void setElementName() {
        elementName = String.format("%s.%s",
                page.getClass().getSimpleName(),
                ClassUtils.getObjectFieldName(page, this));
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
                selector = readElementSelectorFromFile(
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
                saveElementSelectorToFile(
                        config.getPagesFolderPath(), elementName, selectorTemplate);
            }
        }
        catch (Throwable e) {
            throw new SmartRuntimeException(String.format(
                    "Can not set element selector %s.", by), e);
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
            saveElementSelectorToFile(
                    config.getPagesFolderPath(), elementName, selectorTemplate);
            elementSelectorMap.put(elementName, selector);
        }
    }

    private void validatePage() {

        if (page == null) {
            throw new SmartRuntimeException("""
                            Please add method initialize(); to page class constructor like this:
                            
                            public YourPage() {
                                super();
                                initialize();
                            }
                            """.stripIndent());
        }
    }
}
