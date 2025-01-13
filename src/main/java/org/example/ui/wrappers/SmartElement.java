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
        log.debug("{} native element is returned: {}", elementName, element);
        return element;
    }

    /**
     * Returns wrapped web element.
     * @return The wrapped web element.
     */
    @Override
    public WebElement getWrappedElement() {
        log.debug("{} wrapped element is returned: {}", elementName, element);
        return getElement();
    }

    /**
     * Clicks web element.
     */
    @Override
    public void click() {
        getElement().click();
        log.debug("{} element is clicked.", elementName);
    }

    /**
     * Submits web element.
     */
    @Override
    public void submit() {
        getElement().submit();
        log.debug("{} element is submitted.", elementName);
    }

    /**
     * Sends keys to web element.
     * @param keysToSend The keys to send.
     */
    @Override
    public void sendKeys(CharSequence... keysToSend) {
        getElement().sendKeys(keysToSend);
        // Do not log keys sent for security purpose.
        log.debug("Keys are sent to {} element.", elementName);
    }

    /**
     * Clears the web element.
     */
    @Override
    public void clear() {
        getElement().clear();
        log.debug("{} element is cleared.", elementName);
    }

    /**
     * Returns web element tag name.
     * @return The tag name.
     */
    @Override
    public String getTagName() {
        String tagName = getElement().getTagName();
        log.debug("{} element tag name is returned: {}", elementName, tagName);
        return tagName;
    }

    /**
     * Returns the eb element attribute value by its name.
     * @param name The attribute name.
     * @return The attribute value.
     */
    @Override
    public String getAttribute(String name) {
        String value = getElement().getAttribute(name);
        log.debug("{} element '{}' attribute is returned: {}", elementName, name, value);
        return value;
    }

    /**
     * Returns the DOM property by its name.
     * @param name The DOM property name.
     * @return The DOM property value.
     */
    @Override
    public String getDomProperty(String name) {
        String value = getElement().getDomProperty(name);
        log.debug("{} element '{}' DOM property is returned: {}", elementName, name, value);
        return value;
    }

    /**
     * Returns DOM attribute value by its name.
     * @param name The DOM attribute name.
     * @return The DOM attribute value.
     */
    @Override
    public String getDomAttribute(String name) {
        String value = getElement().getDomAttribute(name);
        log.debug("{} element '{}' DOM attribute is returned: {}", elementName, name, value);
        return value;
    }

    /**
     * Returns area role.
     * @return The area role.
     */
    @Override
    public String getAriaRole() {
        String role = getElement().getAriaRole();
        log.debug("{} element aria role is returned: {}", elementName, role);
        return role;
    }

    /**
     * Returns the accessible name.
     * @return The accessible name.
     */
    @Override
    public String getAccessibleName() {
        String name = getElement().getAccessibleName();
        log.debug("{} element accessibility name is returned: {}", elementName, name);
        return name;
    }

    /**
     * Returns true/false selected flag.
     * @return The selected flag.
     */
    @Override
    public boolean isSelected() {
        boolean isSelected = getElement().isSelected();
        log.debug("{} element is selected: {}", elementName, isSelected);
        return isSelected;
    }

    /**
     * Returns true/false enabled flag.
     * @return The enabled flag.
     */
    @Override
    public boolean isEnabled() {
        boolean isEnabled = getElement().isEnabled();
        log.debug("{} element is selected: {}", elementName, isEnabled);
        return isEnabled;
    }

    /**
     * Returns true is element is present
     * or false otherwise.
     * @return The present flag.
     */
    public boolean isPresent() {
        boolean isPresent = !driver.findElements(smartBy.getBy()).isEmpty();
        log.debug("{} element is present: {}", elementName, isPresent);
        return isPresent;
    }

    /**
     * Returns element text.
     * @return The element text.
     */
    @Override
    public String getText() {
        String text = getElement().getText();
        log.debug("{} element text is returned: {}", elementName, text);
        return text;
    }

    /**
     * Finds a list of web elements by their locator.
     * @param by The element locator.
     * @return The list of found elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> webElements = getElement().findElements(by);
        log.debug("{} child elements are found: {}", elementName, webElements);
        return webElements;
    }

    /**
     * Finds the web element by its locator.
     * @param by The element locator.
     * @return The web element.
     */
    @Override
    public WebElement findElement(By by) {
        WebElement webElement = getElement().findElement(by);
        log.debug("{} element is found: {}", elementName, webElement);
        return webElement;
    }

    /**
     * Returns true/false displayed flag.
     * @return The displayed flag.
     */
    @Override
    public boolean isDisplayed() {
        boolean isDisplayed = getElement().isDisplayed();
        log.debug("{} element is present: {}", elementName, isDisplayed);
        return isDisplayed;
    }

    /**
     * Returns element location point.
     * @return The element location point.
     */
    @Override
    public Point getLocation() {
        Point location = getElement().getLocation();
        log.debug("{} element location is returned: {}", elementName, location);
        return location;
    }

    /**
     * Returns element size dimension.
     * @return The element size dimension.
     */
    @Override
    public Dimension getSize() {
        Dimension size = getElement().getSize();
        log.debug("{} element size is returned: {}", elementName, size);
        return size;
    }

    /**
     * Returns element rectangle.
     * @return The element rectangle.
     */
    @Override
    public Rectangle getRect() {
        Rectangle rectangle = getElement().getRect();
        log.debug("{} element rectangle is returned: {}", elementName, rectangle);
        return rectangle;
    }

    /**
     * Returns element CSS property value by its name.
     * @param propertyName The CSS property name.
     * @return The CSS property value.
     */
    @Override
    public String getCssValue(String propertyName) {
        String value = getElement().getCssValue(propertyName);
        log.debug("{} element CSS value returned: {}", elementName, value);
        return value;
    }

    /**
     * Returns element shadow root search context.
     * @return The search context.
     */
    public SearchContext getShadowRoot() {
        SearchContext shadowRoot = getElement().getShadowRoot();
        log.debug("{} element shadow root is returned: {}", elementName, shadowRoot);
        return shadowRoot;
    }

    /**
     * Returns element screenshot.
     * @param target The screenshot target.
     * @return The screenshot data.
     * @param <X> The screenshot data type.
     * @throws WebDriverException in case of error.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        X image = getElement().getScreenshotAs(target);
        log.debug("{} element '{}' screenshot is taken.", element, target);
        return image;
    }

    /**
     * Returns element value DOM property.
     * @return The search context.
     */
    public String getValueDomProperty() {
        String value = getDomProperty("value");
        log.debug("{} element value DOM property is returned: {}", element, value);
        return value;
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
