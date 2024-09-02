package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.selectors.*;
import org.example.drivers.wrappers.SmartWebElement;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ClassUtils;
import org.example.utils.FileSystemUtils;
import org.example.utils.WebUtils;
import org.example.utils.WaiterUtils;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;


/**
 * Base web element class.
 */
@Slf4j
public abstract class SmartElement implements WebElement, WrapsElement {
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> elementSelectorMap =
            readAllElementSelectorsFromFiles(Config.getInstance().getPagesFolderPath());

    static protected final Config config = Config.getInstance();
    private WebElement element = null;
    protected SmartBy smartBy;
    protected SmartPage page;
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
        ConcurrentMap<String, String> elementsMap = new ConcurrentHashMap<>();
        Set<String> fileNames = FileSystemUtils.getFileNamesInFolder(folderPath);

        Thread thread = new Thread(() -> {
            try {
                log.debug("Asynchronous page object element selectors reading began.");

                for (String fileName : fileNames) {
                    if (FileSystemUtils.getFileExtension(fileName).equals("json")) {
                        readSelectorTypeAndValueFromFile(fileName, elementsMap);
                    }
                }
                log.debug("Asynchronous page object element selectors reading finished.");
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read all web page files from: %s", folderPath), e);
            }
        });
        thread.start();
        return elementsMap;
    }

    /**
     * Base element constructor by its page and auto selector.
     */
    public SmartElement() {
        smartBy = SmartBy.auto();
        driver = WebDriverFactory.getDriver();
    }

    /**
     * Base element constructor by its selector.
     * @param by The element selector.
     */
    public SmartElement(By by) {
        this.smartBy = SmartBy.selector(by);
        driver = WebDriverFactory.getDriver();
    }

    /**
     * Sets keyword.
     * @param keyword The keyword.
     */
    public void setKeyword(Object keyword) {
        smartBy.setKeyword(keyword);
        log.debug("Element keyword is set: {}", keyword);
        log.debug("""
                Smart element {} updated with keyword and saved to file.
                Keyword: {}
                Smart element:
                {}
                """.stripIndent(),
                keyword, this);
    }

    /**
     * Gets native web element.
     * @return The web element.
     */
    public WebElement getNativeElement() {
        log.debug("Native web element returned: {}", element);
        return element;
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
                setElementName();

                if (smartBy == null || smartBy.getSmartByType() == null) {
                    setElementSelector();
                }
                element = driver.findElement(smartBy.getBy());
                handleElement();
            }
            return element;
        }
        catch (NoSuchElementException |
               InvalidSelectorException |
               NoSuchFrameException e) {

            if (config.getDebugMode()) {
                fixElementSelector();
            }
            else {
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
    void setPage(SmartPage page) {
        this.page = page;
    }

    /**
     * Highlights the element.
     */
    protected void highlightElement() {
        WebElement webElement = element;

        if (element instanceof SmartWebElement smartWebElement) {
            webElement = smartWebElement.getNativeElement();
        }
        WebUtils.highlightElement(webElement);
    }

    /**
     * Saves element selector to JSON file.
     */
    private void saveElementSelectorToFile() {
        String filePath = null;
        String  selectorTemplate = null;

        try {
            String[] nameParts = elementName.split("\\.");

            if (nameParts.length != 2) {
                throw new SmartRuntimeException(String.format(
                        "Invalid web element name: '%s'", elementName));
            }
            String fileName = String.format("%s.json", nameParts[0]);
            String fieldName = nameParts[1];
            JSONObject json;
            filePath = String.format("%s/%s", PAGE_OBJECTS_FOLDER_PATH, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);
                json = new JSONObject(jsonString);
            }
            else {
                json = new JSONObject();
            }
            selectorTemplate = smartBy.toSelectorTemplate();
            json.put(fieldName, selectorTemplate);
            FileSystemUtils.createFile(filePath, json.toString(JSON_LAYOUT_SPACES));
            log.debug("Element {} selector {} is saved to file {}.",
                    elementName, selectorTemplate, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not save web %s element selector %s to file: %s",
                    elementName, selectorTemplate, filePath), e);
        }
    }

    /**
     * Reads element selector from JSON file or returns null
     * if file does not exist or element selector undefined.
     * @param elementName The element name.
     * @return The element selector.
     */
    private String readElementSelectorFromFile(String elementName) {
        String filePath = null;
        try {
            String[] nameParts = elementName.split("\\.");

            if (nameParts.length != 2) {
                throw new SmartRuntimeException(String.format(
                        "Invalid web element name: '%s'", elementName));
            }
            String fileName = String.format("%s.json", nameParts[0]);
            readSelectorTypeAndValueFromFile(fileName, elementSelectorMap);
            return elementSelectorMap.get(elementName);
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
        log.debug("Element name is set: {}", elementName);
    }

    private void setElementSelector() {
        boolean isSelectorUpdated = false;
        boolean isReadFromFile = true;

        try {
            smartBy.setElementName(elementName);
            String selectorTemplate;

            if (elementSelectorMap.containsKey(elementName)) {
                selectorTemplate = elementSelectorMap.get(elementName);
                Selector selector = Selector.fromString(selectorTemplate, smartBy.getKeyword());
                selectorTemplate = selector.totValueString();
            }
            else {
                selectorTemplate = readElementSelectorFromFile(elementName);
                isSelectorUpdated = true;
            }
            if (selectorTemplate == null) {

                if (config.getDebugMode()) {
                    String byString = null;

                    if (smartBy.getSmartByType() != null) {
                        byString = smartBy.getBy().toString();
                    }
                    if (byString != null && byString.endsWith(String.format(": %s", NULL_VALUE_STRING))) {
                        byString = null;
                    }
                    if (byString != null && smartBy.getSmartByType() == SmartByType.LINK_TEXT &&
                        WebUtils.isPngImage(byString)) {
                        smartBy = new SmartBy(SmartByType.IMAGE, smartBy.getBy());
                    }
                    Object keyword = smartBy.getKeyword();
                    SmartByType selectorType = smartBy.getSmartByType();
                    selectorTemplate = WebUtils.selectElementAndGetSelector(
                            elementName, keyword, selectorType, "");
                    isSelectorUpdated = true;
                    isReadFromFile = false;
                }
            }
            if (selectorTemplate == null) {
                throw new SmartRuntimeException(String.format(
                        "'%s' element selector is not defined.", elementName));
            }
            By bySelector = WebUtils.convertSelectorTemplateToBy(selectorTemplate, smartBy.getKeyword());
            smartBy.setBy(bySelector);

            if (isSelectorUpdated) {
                elementSelectorMap.put(elementName, smartBy.toSelectorTemplate());
            }
            if (!isReadFromFile) {
                saveElementSelectorToFile();
            }
        }
        catch (Throwable e) {
            throw new SmartRuntimeException(String.format(
                    "Can not set element selector from smart by:\n%s.", smartBy), e);
        }
    }

    private void fixElementSelector() {
        Object keyword = smartBy.getKeyword();
        SmartByType selectorType = smartBy.getSmartByType();
        String selectorValue = SmartByParser.selectorValueFromBy(smartBy);
        String selectorString = WebUtils.selectElementAndGetSelector(
                elementName, keyword, selectorType, selectorValue);

        if (selectorString == null) {
            return;
        }
        try {
            element = WebUtils.getElementBySelector(selectorString, keyword);
        }
        catch (Exception e) {
            // Ignore exception
        }
        By bySelector = WebUtils.convertSelectorTemplateToBy(selectorString, keyword);
        smartBy.setBy(bySelector);
        saveElementSelectorToFile();
        elementSelectorMap.put(elementName, selectorString);
    }

    private void validatePage() {

        if (page == null) {
            throw new SmartRuntimeException("""
                            ///////////////////////////////////////////////////////////////////////////
                            Please use @SmartElement annotation to initialize your page object like this:
                            
                            @SuppressWarnings("unused")
                            @SmartElement
                            @Getter
                            public class YourPage extends SmartPage {
                            
                                private TextInput yourTextInput;
                                private Button yourButton;
                            }
                            ///////////////////////////////////////////////////////////////////////////
                            """.stripIndent());
        }
    }

    private static void readSelectorTypeAndValueFromFile(String fileName, Map<String, String> elementMap) {
        String filePath = String.format("%s/%s", PAGE_OBJECTS_FOLDER_PATH, fileName);

        if (!FileSystemUtils.fileExists(filePath)) {
            log.debug("Page object file does not exist: {}", filePath);
            return;
        }
        String pageName = FileSystemUtils.getFileNameWithoutExtension(fileName);
        String fileContent = FileSystemUtils.readFile(filePath);

        if (!fileContent.trim().isEmpty()) {
            JSONObject json = new JSONObject(fileContent);

            for (String fieldName : json.keySet()) {
                String selectorTypeAndValue = json.get(fieldName).toString();
                String elementName = String.format("%s.%s", pageName, fieldName);
                elementMap.put(elementName, selectorTypeAndValue);
                log.debug("Element {} selector {} is asynchronously read from file {}",
                        elementName, selectorTypeAndValue, filePath);
            }
        }
    }
}
