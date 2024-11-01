package org.example.ui.wrappers;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.ui.factories.WebDriverFactory;
import org.example.ui.selectors.*;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;


/**
 * Base web element class.
 */
@Slf4j
public abstract class SmartElement implements WebElement, WrapsElement {
    private static final String PAGES_PACKAGE_NAME = "org.example.pages";
    private static final ConcurrentMap<Long, WebElement> handledElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> elementSelectorMap =
            readAllElementSelectorsFromFiles(Config.getInstance().getPagesFolderPath());

    static protected final Config config = Config.getInstance();
    private WebElement element = null;
    protected SmartBy smartBy;
    protected WebDriver driver;
    protected String elementName;
    protected final WebSynchronizer synchro;
    protected final JavascriptExecutor jsExecutor;

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
     * Constructs base smart element.
     */
    public SmartElement() {
        setElementName();
        smartBy = SmartBy.auto();
        synchro = new WebSynchronizer();
        jsExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Base element constructor by its selector.
     * @param by The element selector.
     */
    public SmartElement(By by) {
        setElementName();

        if (by instanceof SmartBy smartBySelector) {
            this.smartBy = smartBySelector;
        }
        else {
            this.smartBy = SmartBy.selector(by);
        }
        synchro = new WebSynchronizer();
        jsExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Sets keyword.
     * @param keyword The keyword.
     */
    public void setKeyword(Object keyword) {
        smartBy.setKeyword(keyword);
        log.debug("Element keyword is set: {}", keyword);
        log.debug("""
                Smart element {} updated with keyword.
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
     * Returns true is element is present
     * or false otherwise.
     * @return The present flag.
     */
    public boolean isPresent() {
        boolean isPresent = !driver.findElements(smartBy.getBy()).isEmpty();
        log.debug("Element {} is present: {}", elementName, isPresent);
        return isPresent;
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
     * Returns smart By selector.
     * @return The smart By selector.
     */
    public SmartBy getSmartBy() {
        log.debug("Smart By selector is returned:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Converts smart element to string.
     * @return The value string.
     */
    @Override
    public String toString() {
        getElement();
        String valueString = String.format("%s:\n%s\n%s",
                elementName, smartBy, element);
        log.debug("Smart element is converted to string:\n{}", valueString);
        return valueString;
    }

    /**
     * Compares this smart element to other object.
     * @param object The object.
     * @return The result: equals - true, otherwise - false.
     */
    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart element equals() called. The actual object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart element equals() called. The actual and expected are the same object.");
            return true;
        }
        if (object instanceof SmartElement actualSmartElement) {
            try {
                getElement();
                // Do not compare WebElements on purpose for comparing SmartPage fields
                boolean result = elementName.equals(actualSmartElement.elementName) &&
                        smartBy.equals(actualSmartElement.smartBy);
                log.debug("""
                        Two smart elements objects are compared.
                        Expected:
                        {}
                        Actual:
                        {}
                        Result:
                        {}
                        """.stripIndent(),
                        this, object, result);
                return result;
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                        Cannot compare two smart element objects.
                        Expected:
                        %s
                        Actual:
                        %s
                        """.stripIndent(),
                        this,
                        object), e);
            }
        }
        log.debug("Smart element equals() returned false.\n" +
                "The actual object is not a smart element.");
        return false;
    }

    @Override
    public int hashCode() {
        getElement();
        return Objects.hash(elementName, smartBy, element);
    }

    /**
     * Returns WebElement instance.
     * @return The WebElement instance.
     */
    protected WebElement getElement() {

        try {
            if (driver == null) {
                driver = WebDriverFactory.getDriver();
            }
            if (element == null) {

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
            TimerUtils.waitMilliSeconds(config.getStepDelay());
        }
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
                        WebUtils.isPngImageSelector(byString)) {
                        // TODO - Fix image selector
                        // Parse By string to get PNG image file path
                        String imageFilePath = byString.substring(byString.indexOf(":"));
                        smartBy = SmartBy.image(imageFilePath);
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

    synchronized private void setElementName() {

        try {
            // Set element name from declaring class name and the current source code line number.
            String declaringClassName = ClassUtils.getDeclaringClassName(PAGES_PACKAGE_NAME);
            String sourceCode = ClassUtils.getClassSourceCode(declaringClassName);
            int lineNumber = ClassUtils.getInvocationCodeLineNumber(declaringClassName);
            String fieldName = ClassUtils.getObjectNameFromSourceCode(sourceCode, lineNumber);
            String pageName = ClassUtils.getSimpleClassName(declaringClassName);

            elementName = String.format("%s.%s", pageName, fieldName);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot set smart element name for element %s.", element), e);
        }
    }
}
