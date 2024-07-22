package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.playwright.PlaywrightElement;
import org.example.helpers.GlobalKeyboardListener;
import org.example.helpers.TimeOut;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Point;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.example.constants.Settings.OPEN_AI_API_KEY_NAME;
import static org.example.constants.Settings.OPEN_AI_API_URL;

@Slf4j
public class WebUtils {
    static private final AtomicReference<GlobalKeyboardListener> keyboardListener = new AtomicReference<>();
    static private final int SELECT_ELEMENT_TIMEOUT_SECONDS = 10 * 60;
    static private final int SHOW_POPUP_TIMEOUT_SECONDS = 10 * 60;
    static private final int MAX_OPEN_AI_REQUEST_REPEATS = 10;
    private static final String HIGHLIGHT_BORDER_STYLE = "3px solid red";
    private static final List<String> reliableAttributes = Arrays.asList(
            "id", "name", "type", "class", "alt", "placeholder", "title",
            "accesskey", "tabindex", "value", "myprop", "list", "label");

    private static final List<String> textAttributes = Arrays.asList(
            "alt", "placeholder", "title", "value", "label");
    static private final String OPEN_AI_REQUEST_FORMAT =
            "{\"model\": \"gpt-3.5-turbo\"," +
            "\"messages\": [{" +
            "\"role\": \"user\"," +
            "\"content\": \"%s\"}]}";

    static private final String ELEMENT_CSS_SELECTOR_AI_PROMPT_FORMAT =
            "You are a web automation assistant. Given the following HTML page source and " +
            "an HTML element snippet, extract either a CSS selector or an XPath selector for " +
            "the specified element.\n\n" +
            "1. **Selector Restrictions**:\n" +
            "   - Exclude these selectors: %s.\n" +
            "   - Do not use element style attributes.\n\n" +
            "2. **Allowed Attributes**:\n" +
            "   - Use only these attributes: `id`, `name`, `type`, `class`, `alt`,\n" +
            "    `placeholder`, `title`, `accesskey`, `tabindex`, `value`, `myprop`,\n" +
            "    `list`, `label`.\n\n" +
            "3. **Validation**:\n" +
            "   - Ensure that the selector uniquely identifies the provided element\n" +
            "     and is valid.\n\n" +
            "4. **Output**:\n" +
            "   - If no valid selector is found, return an empty string.\n" +
            "   - Do not include any additional text or description; return only\n" +
            "     the selector or an empty string.\n\n" +
            "HTML page source:\n%s.\n" +
            "HTML element snippet:\n%s.";
    static private final String ELEMENT_XPATH_SELECTOR_AI_PROMPT_FORMAT =
            "You are a web automation assistant. Given the following HTML page source and " +
            "an HTML element snippet, extract an XPath selector for " +
            "the specified element.\n\n" +
            "1. **Text Matching**:\n" +
            "   - The following attribute values may exactly match or contain the unique text:\n" +
            "     `text()`, `.`, `alt`, `placeholder`, `title`, `value`, `label`.\n\n" +
            "2. **Text Matching**:\n" +
            "   - Prioritize an exact text match in the following attributes:\n" +
            "    `text()`, `alt`, `placeholder`, `title`, `value`, `label`.\n" +
            "   - If an exact match is not found, use `contains()` for a partial\n" +
            "     match in these attributes.\n\n" +
            "3. **Selector Restrictions**:\n" +
            "   - Exclude selectors matching these patterns: %s.\n" +
            "   - Do not use element style attributes.\n\n" +
            "4. **Allowed Attributes**:\n" +
            "   - Only use these attributes for constructing the XPath:\n" +
            "     `id`, `name`, `type`, `class`, `alt`, `placeholder`, `title`,\n" +
            "     `accesskey`, `tabindex`, `value`, `myprop`, `list`, `label`.\n" +
            "   - These attributes values may equal unique text or contain the text:\n" +
            "     `text()`, `.`, `alt`, `placeholder`, `title`, `value`, `label`.\n\n" +
            "5. **Validation**:\n" +
            "   - Ensure the XPath is as short as possible.\n" +
            "   - Ensure the XPath selector is valid, uniquely identifies the element,\n" +
            "     but not its child or sibling.\n" +
            "   - Ensure the XPath selector is valid, uniquely identifies the\n" +
            "     provided element, and does not include unwanted patterns.\n\n" +
            "6. **Output**:\n" +
            "   - Return only the XPath selector or an empty string if no valid selector is found.\n" +
            "   - Do not include additional text or descriptions.\n\n" +
            "HTML page source:\n%s.\n" +
            "HTML element snippet:\n%s.\n" +
            "Unique text: '%s'.";
    private static final int MAX_ELEMENT_NESTING = 10;
    private static final ConcurrentMap<Long, Boolean> listenerSetupMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, WebElement> highlightedElementMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, String> prevElementStyleMap = new ConcurrentHashMap<>();

    private WebUtils(){}

    /**
     * Returns mouse point.
     * @return The mouse point.
     */
    public static Point getMousePoint() {
        long threadId = Thread.currentThread().threadId();

        try {
            JavascriptExecutor js = (JavascriptExecutor) WebDriverFactory.getDriver();

            if (!listenerSetupMap.containsKey(threadId)) {
                js.executeScript(
                        "document.addEventListener('mousemove', function(event) { " +
                                "   window.mouseX = event.clientX; " +
                                "   window.mouseY = event.clientY; " +
                                "});"
                );
                listenerSetupMap.put(threadId, true);
            }
            Long x = (Long) js.executeScript("return window.mouseX;");
            Long y = (Long) js.executeScript("return window.mouseY;");

            if (x == null || y == null) {
                return new Point(-1, -1);
            } else {
                return new Point(x.intValue(), y.intValue());
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get mouse point coordinates:\n%s", e.getMessage()));
        }
    }

    /**
     * Returns the current web element under the mouse cursor
     * or NULL if element not found.
     * @return The web element or NULL.
     */
    public static WebElement getWebElementUnderMouse() {
        JavascriptExecutor js = (JavascriptExecutor) WebDriverFactory.getDriver();
        Point mousePoint = getMousePoint();

        if (mousePoint.getX() == -1 || mousePoint.getY() == -1) {
            return null;
        }
        try {
            return (WebElement) js.executeScript(
                    "return document.elementFromPoint(arguments[0], arguments[1]);",
                    mousePoint.getX(), mousePoint.getY());
        }
        catch (Exception e) {
            // Returns null if no element found.
            return null;
        }
    }

    /**
     * Returns web element style value by property name.
     * @param element The web element.
     * @param propertyName The property value.
     * @return The style value.
     */
    public static String getElementStyle(WebElement element, String propertyName) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) WebDriverFactory.getDriver();
            String scriptFormat = element instanceof PlaywrightElement ?
                    "element => getComputedStyle(element).%s":
                    "return window.getComputedStyle(arguments[0]).%s;";
            String style = (String) js.executeScript(String.format(scriptFormat, propertyName), element);
            log.debug("{} element property {} style: {}", element, propertyName, style);
            return style;
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get web element style '%s':\n%s.",
                    propertyName, e.getMessage()));
        }
    }

    /**
     * Sets web element style value by its property name.
     * @param element The web element.
     * @param propertyName The property name.
     * @param propertyValue The property value.
     */
    public static void setElementStyle(WebElement element, String propertyName, String propertyValue) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) WebDriverFactory.getDriver();
            String scriptFormat = element instanceof PlaywrightElement ?
                    "element => element.style.%s='%s'" :
                    "arguments[0].style.%s='%s';";
            js.executeScript(String.format(scriptFormat, propertyName, propertyValue), element);
            log.debug("{} element style property {}:{} is set up.",
                    element, propertyName, propertyValue);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot set web element style '%s':\n%s.",
                    propertyName, e.getMessage()));
        }
    }

    /**
     * Highlights web element.
     * @param element The web element.
     */
    public static void highlightElement(WebElement element) {
        long threadId = Thread.currentThread().threadId();

        try {
            if (highlightedElementMap.containsKey(threadId) &&
                    prevElementStyleMap.get(threadId) != null) {
                // Restore element style.
                unhighlightElement();
            }
            // Save the current element style.
            String style;
            try {
                style = getElementStyle(element, "border");
            } catch (Exception e) {
                // Ignore exception if style is not available.
                style = "";
            }
            prevElementStyleMap.put(threadId, style);
            // Change current element border style.
            try {
                setElementStyle(element, "border", HIGHLIGHT_BORDER_STYLE);
                highlightedElementMap.put(threadId, element);
                log.debug("{} element is highlighted.", element);
            } catch (Throwable e) {
                // Ignore exception is previous element is not available.
                log.debug("Exception {} when {} element is highlighted.", e.getMessage(), element);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot highlight web element:\n%s", e.getMessage()));
        }
    }

    /**
     * Unhighlights web element.
     */
    public static void unhighlightElement() {
        long threadId = Thread.currentThread().threadId();
        WebElement element = highlightedElementMap.get(threadId);

        try {
            String style = prevElementStyleMap.get(threadId);
            setElementStyle(element, "border", style);
            log.debug("{} element is unhighlighted.", element);
        } catch (Throwable e) {
            // Ignore exception if not possible to restore style.
            log.debug("Exception {} when {} element is unhighlighted.",
                    e.getMessage(), element);
        }
    }

    /**
     * Shows alert pop-up with text.
     * @param text The text.
     */
    public static void showAlert(String text) {
        try {
            WebDriver driver = WebDriverFactory.getDriver();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(String.format("alert('%s');", escapeJS(text)));

            WebDriverWait wait = new WebDriverWait(driver,
                    Duration.ofSeconds(SHOW_POPUP_TIMEOUT_SECONDS));
            // Wait for alert to be present.
            wait.until(ExpectedConditions.alertIsPresent());
            log.debug("Alert pop-up is open with text: {}", text);
            // Wait for no alert.
            wait.until(alertIsNotPresent());
            log.debug("Alert pop-up is closed with text: {}", text);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot show alert text '%s':\n%s", text, e.getMessage()));
        }
    }

    /**
     * Shows JS prompt popup with text and optional default value on it
     * and returns input string.
     * @param text The text.
     * @param defaultValue The default value (can be null).
     * @return The input string.
     */
    public static String showPrompt(String text, String defaultValue) {
        WebDriver driver = WebDriverFactory.getDriver();

        if (defaultValue == null) {
            defaultValue = "";
        }

        // Inject a hidden input field to store the prompt result
        String injectScript = "var input = document.createElement('input');" +
                "input.setAttribute('type', 'hidden');" +
                "input.setAttribute('id', 'prompt-result');" +
                "document.body.appendChild(input);";
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript(injectScript);

        // Execute the prompt and store the result in the hidden input field
        String script = String.format(
                "var result = prompt('%s:', '%s');" +
                "document.getElementById('prompt-result').value = result;",
                escapeJS(text), escapeJS(defaultValue));
        jsExecutor.executeScript(script);

        // Wait for the alert (prompt) to be present
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(SHOW_POPUP_TIMEOUT_SECONDS));
        wait.until(ExpectedConditions.alertIsPresent());
        log.debug("Prompt pop-up is open with text '{}' and default value '{}'", text, defaultValue);

        // Wait until the alert (prompt) is no longer present
        wait.until(alertIsNotPresent());

        // Retrieve the input value from the hidden input field
        String userInput = (String) jsExecutor.executeScript(
                "return document.getElementById('prompt-result').value;");
        log.debug("Prompt pop-up is open with text '{}' and user input '{}'", text, userInput);
        return userInput;
    }

    /**
     * Returns web element attributes name:value map.
     * @param element The element.
     * @return The attributes name:value map.
     */
    public static Map<String, String> getAllAttributes(WebElement element) {
        WebDriver driver = WebDriverFactory.getDriver();
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        Map<String, Object> attributes = (Map<String, Object>) jsExecutor.executeScript(
                "var items = {}; " +
                        "for (index = 0; index < arguments[0].attributes.length; ++index) { " +
                        "    items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value " +
                        "}; " +
                        "return items;", element);
        // Convert Object values to String
        Map<String, String> stringAttributes = new HashMap<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            stringAttributes.put(entry.getKey(), entry.getValue().toString());
        }
        log.debug("Element {} attributes are returned {}", element, stringAttributes);
        return stringAttributes;
    }

    /**
     * User selects web element and returns its selector by all means:
     * by algorithm;
     * or by OpenAI request;
     * or by manual input.
     * @param elementName The element name.
     * @param text The text (optional - can eb NULL).
     * @return The element selector or NULL
     */
    public static String selectElementAndGetSelector(String elementName, String text) {
        try {
            WebElement element = WebUtils.selectWebElement(elementName);
            String selector = WebUtils.getElementSelector(element, text);
            String format = null;
            log.debug("Element {} selector with text {} is detected by algorithm: {}",
                    element, text, selector);

            if (selector == null) {
                selector = WebUtils.getSelectorWithAI(element, text);
                log.debug("Element {} selector with text '{}' is detected by OpenAI: {}",
                        element, text, selector);
            }
            if (selector == null) {
                selector = WebUtils.showPrompt(
                        String.format("Please enter '%s' element selector", elementName),
                        null);
            }
            String formatFormat =
                    "%s\nEnter the selector or just click OK to select the element.\n" +
                    "Click CANCEL to terminate the test";

            while (true) {

                if (WebUtils.isSelectorValidAndUnique(element, text, selector)) {
                    selector = selector.trim();
                    format = String.format(formatFormat, String.format(
                            "Valid %s element selector", elementName));
                    selector = showPrompt(format, selector);

                    if (selector.isEmpty()) {
                        // Terminate all tests when user clicks Cancel button.
                        terminateAllTests();
                    }
                    selector = replaceDoubleQuotesWithSingleQuotes(selector);
                }

                if (WebUtils.isSelectorValidAndUnique(element, text, selector)) {
                    log.debug("Valid element {} selector with text '{}' is: {}",
                            element, text, selector);
                    return selector;
                }
                else if (!WebUtils.isValidElementSelectorFormat(selector)) {
                    format = String.format(formatFormat, "Invalid '%s' element selector format.");
                }
                else if (WebUtils.numberOfElementsFoundBySelector(selector, text) > 1) {
                    format = String.format(formatFormat, "More than one '%s' element is found by selector.");
                }
                else if (WebUtils.numberOfElementsFoundBySelector(selector, text) == 1) {
                    format = String.format(formatFormat, "Wrong '%s' element is found by selector.");
                }
                else if (WebUtils.numberOfElementsFoundBySelector(selector, text) == 0) {
                    format = String.format(formatFormat, "No '%s' element is found by selector.");
                }

                String previousSelector = replaceDoubleQuotesWithSingleQuotes(selector);
                selector = showPrompt(String.format(format, elementName), selector);

                if (selector.isEmpty()) {
                    // Terminate all tests when user clicks Cancel button.
                    terminateAllTests();
                }
                selector = replaceDoubleQuotesWithSingleQuotes(selector);

                if (selector.equals(previousSelector)) {
                    element = WebUtils.selectWebElement(elementName);
                    selector = WebUtils.getElementSelector(element, text);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get '%s' element selector by all means.", elementName), e);
        }
    }

    /**
     * Detects web element CSS selector or Xpath selector by unique text.
     * @param element The element.
     * @param text The unique text.
     * @return The element CSS selector or Xpath selector.
     */
    public static String getElementSelector(WebElement element, String text) {
        if (text == null) {
            String cssSelector = getCssOrXpathElementSelector(element, true, true);

            if (cssSelector == null) {
                cssSelector = getElementCssSelectorByParent(element);
            }

            if (cssSelector == null) {
                cssSelector = getElementCssSelectorBySibling(element);
            }

            if (cssSelector == null) {
                cssSelector = getElementCssSelectorByChild(element);
            }
            log.debug("Element {} CSS selector with text '{}' is: {}", element, text, cssSelector);
            return cssSelector;
        }

        String xpathSelector = getElementXpathSelectorByText(element, text, false);

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorByText(element, text, true);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorByParentText(element, text, false);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorByParentText(element, text, true);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorBySiblingText(element, text, false);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorBySiblingText(element, text, true);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorByChildText(element, text, false);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorByChildText(element, text, true);
        }

        if (xpathSelector == null) {
            xpathSelector = getElementXpathSelectorByIndex(element);
        }
        log.debug("Element {} Xpath selector with text '{}' is: {}", element, text, xpathSelector);
        return xpathSelector;
    }

    public static WebElement getElementBySelector(String selector, String text) {
        By by = convertSelectorTemplateToBy(selector, text);
        List<WebElement> elements = WebDriverFactory.getDriver().findElements(by);
        int size = elements.size();

        if (size == 0) {
            throw new RuntimeException(String.format(
                    "No element found by selector %s.", selector));
        } else if (size > 1) {
            throw new RuntimeException(String.format(
                    "More than one element found by selector %s: %d", selector, size));
        }
        log.debug("Element {} found by its selector {}.", elements.get(0), selector);
        return elements.get(0);
    }

    /**
     * Gets web element CSS selector or XPath selector by text with AI
     * or NULL if unique selector is not detected.
     * @param element The element.
     * @param text The text for Xpath selector.
     * @return The element selector or selector.
     */
    public static String getSelectorWithAI(WebElement element, String text) {
        WebDriver driver = WebDriverFactory.getDriver();
        String pageHTML = driver.getPageSource();
        String elementHTML = element.getAttribute("outerHTML");
        String selector;
        StringBuilder wrongSelectors = new StringBuilder();
        int repeatCount = MAX_OPEN_AI_REQUEST_REPEATS;

        do {
            String prompt = text == null ?
                    String.format(ELEMENT_CSS_SELECTOR_AI_PROMPT_FORMAT,
                            wrongSelectors, pageHTML, elementHTML) :
                    String.format(ELEMENT_XPATH_SELECTOR_AI_PROMPT_FORMAT,
                            wrongSelectors, pageHTML, elementHTML, text);

            selector = HttpUtils.sendHttpRequest(
                    OPEN_AI_API_URL,
                    String.format(OPEN_AI_REQUEST_FORMAT, escapeJSON(prompt)),
                    System.getenv(OPEN_AI_API_KEY_NAME));

            if (selector == null || selector.trim().isEmpty()) {
                break;
            }
            if (--repeatCount == 0) {
                break;
            }
            if (!wrongSelectors.isEmpty()) {
                wrongSelectors.append(", ");
            }
            wrongSelectors.append(selector);
        }
        while (!isSelectorValidAndUnique(element, text, selector));

        log.debug("OpenAI detedted element {} selector with text '{}' is: {}",
                element, text, selector);
        return selector;
    }

    /**
     * Converts string web element selector to selector template
     * by replacing unique text with "%s" placeholder.
     * @param selector The selector string.
     * @param text The text.
     * @return The selector template.
     */
    public static String getSelectorTemplate(String selector, String text) {
        String template = selector.replace(String.format("'%s'", text), "'%s'");
        log.debug("Element selector {} with text '{}' is: {}",
                selector, text, selector);
        return template;
    }

    /**
     * Converts xpath or css selector template that may content unique text identifier.
     * @param selector The selector template (may contain text placeholder "%s").
     * @param text The text (can be NULL).
     * @return The By selector.
     */
     public static By convertSelectorTemplateToBy(String selector, String text) {
        if (text != null && selector.contains("%s")) {
            // Replace text placeholder with actual text (if any).
            // It can be more than one replacement.
            selector = selector.replace("'%s'", String.format("'%s'", text));
        }
        if (isXpath(selector)) {
            By by = By.xpath(selector);
            log.debug("Element XPATH selector {} with text '{}' converted to By {}",
                    selector, text, by);
            return by;
        } else {
            By by = By.cssSelector(selector);
            log.debug("Element CSS selector {} with text '{}' converted to By {}",
                    selector, text, by);
            return by;
        }
    }

    /**
     * Returns number elements found by selector.
     * @param selector The selector.
     * @param text The text (optional - can be NULL).
     * @return The tru/false flag.
     */
    public static int numberOfElementsFoundBySelector(String selector, String text) {
         By by = convertSelectorTemplateToBy(selector, text);
         int size = WebDriverFactory.getDriver().findElements(by).size();
         log.debug("{} element(s) found by selector {} with text '{}'.",
                size, selector, text);
         return size;
    }

    /**
     * Returns parent web element for web element.
     * @param element The web element.
     * @return The parent element.
     */
    public static WebElement getParentElement(WebElement element) {
        if (element == null || element.getTagName().equals("body")) {
            throw new RuntimeException(String.format(
                    "Cannot get parent element of element '%s'", element));
        }
        WebElement parent = element.findElement(By.xpath(".."));
        log.debug("Parent element {} is found for element {}.", parent, element);
        return parent;
    }

    /**
     * Returns sibling elements for web element.
     * @param element The element.
     * @return The list of sibling elements.
     */
    public static List<WebElement> getSiblingElements(WebElement element) {
        WebElement parent = getParentElement(element);
        List<WebElement> siblings = parent.findElements(By.xpath("/*"));
        siblings.remove(element);
        log.debug("Sibling elements of the element {}: {}.", element, siblings);
        return siblings;
    }

    /**
     * Returns all child elements for web element.
     * @param element The element.
     * @return The list of child elements.
     */
    public static List<WebElement> getAllChildElements(WebElement element) {
        List<WebElement> childElements = element.findElements(By.xpath("//*"));
        log.debug("Parent element {} is found for element {}.", childElements, element);
        return childElements;
    }

    /**
     * Selects web element when user hovers mouse over it
     * and clicks left Ctrl button.
     * @param elementName The element name.
     * @return The web element.
     */
    public static WebElement selectWebElement(String elementName) {
        TimeOut timeOut = new TimeOut("Select element", SELECT_ELEMENT_TIMEOUT_SECONDS);
        WebElement prevElement = null;
        log.debug("Element {} selection by user started.", elementName);

        WebUtils.showAlert(String.format(
                "Please select '%s' element and click left Ctrl.",
                elementName));
        initializeKeyBoardListener();
        WebElement element = null;

        while (!timeOut.getExpired()) {
            element = WebUtils.getWebElementUnderMouse();
            if (element == null) {
                continue;
            }

            if (prevElement == null ||
                    (!prevElement.getLocation().equals(element.getLocation()) &&
                            !prevElement.getSize().equals(element.getSize()))) {

                prevElement = element;
                WebUtils.highlightElement(element);
            }

            // Detect Ctrl key press
            if (GlobalKeyboardListener.leftCtrlKeyPressed.get()) {
                log.debug("Left Ctrl button is clicked to stop element selection..");
                break;
            }
        }

        if (element == null) {
            throw new RuntimeException("Web element is not found.");
        }
        WebUtils.unhighlightElement();
        log.debug("We element {} is selected by user.", element);
        return element;
    }

    /**
     * Saves element selector to JSON file.
     * @param elementName The element name.
     * @param selector The element selector.
     */
    public static void saveElementSelectorToFile(String folderPath, String elementName, String selector) {
        String filePath = null;
        try {
            String[] nameParts = elementName.split("\\.");

            if (nameParts.length != 2) {
                throw new RuntimeException(String.format(
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
            throw new RuntimeException(String.format(
                    "Can not save web %s element selector to file: %s",
                    elementName, filePath), e);
        }
    }

    /**
     * Reads element selector from JSON file or returns NULL
     * if file does not exist or element selector undefined.
     * @param folderPath The folder path.
     * @param elementName The element name.
     * @return The element selector.
     */
    public static String readElementSelectorFromFile(String folderPath, String elementName) {
        String filePath = null;
        try {
            String[] nameParts = elementName.split("\\.");

            if (nameParts.length != 2) {
                throw new RuntimeException(String.format(
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
            throw new RuntimeException(String.format(
                    "Can not read web %s element selector from file: %s",
                    elementName, filePath), e);
        }
    }

    /**
     * Reads asynchronously all element selector from JSON file
     * to "element name-selector" map.
     * If no files or they are empty then empty map is returned.
     * @param folderPath The folder path.
     * @param elementSelectorMap The element name - selector map.
     */
    public static void readAllElementSelectorsFromFiles(
            String folderPath, Map<String, String> elementSelectorMap) {
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
                            String selector = replaceDoubleQuotesWithSingleQuotes(
                                    json.get(fieldName).toString());
                            String elementName = String.format("%s.%s", pageName, fieldName);
                            elementSelectorMap.put(elementName, selector);
                            log.debug("Element {} selector {} is asynchronously read from file {}",
                                    elementName, selector, filePath);
                        }
                    }
                    log.debug("Asynchronous page object element selectors reading finished.");
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Cannot read all web page files from: %s", folderPath), e);
            }
        });
        thread.start();
    }

    /**
     * Returns if selector is unique and points the target web elemnt.
     * @param element The element.
     * @param text The text.
     * @param selector The selector.
     * @return The true/false flag.
     */
    public static boolean isSelectorValidAndUnique(WebElement element, String text, String selector) {
        try {
            WebDriver driver = WebDriverFactory.getDriver();
            By by = convertSelectorTemplateToBy(selector, text);
            List<WebElement> elements = driver.findElements(by);
            boolean result = false;

            if (elements.size() == 1) {
                result = compareWebElements(element, elements.get(0));
            }
            log.debug("Element {} selector {} is valid and unique: {}.",
                    element, selector, result);
            return result;
        }
        catch (Exception e) {
            log.debug("Seems element {} selector has invalid format: {}.",
                    element, selector);
            return false;
        }
    }

    /**
     * Returns true if two elements have the same tags, size and location.
     * @param element1 The element 1.
     * @param element2 The element 2.
     * @return The true if equals or false otherwise.
     */
    public static boolean compareWebElements(WebElement element1, WebElement element2) {
        boolean result;

        if (element1 == element2) {
            result = true;
        } else {
            result = element1.getTagName().equals(element2.getTagName()) &&
                    element1.getRect().equals(element2.getRect());
        }
        log.debug("Element 1 {} and element 2 {} comparison is: {}",
                element1, element1, result);
        return result;
    }

    /**
     * Returns true for XPATH format or false otherwise.
     * @param selector The element selector.
     * @return The true/false flag.
     */
    public static boolean isXpath(String selector) {
        if (selector == null) {
            throw new RuntimeException("Selector string is NULL.");
        }
        selector = selector.trim();
        boolean result = selector.startsWith("//") || selector.startsWith("(//");
        log.debug("Is element selector {} XPATH: {}", selector, result);
        return result;
    }

    /**
     * Validates the XPATH or CSS selector format.
     * @param selector The element selector.
     * @return The true if format is valid or false otherwise.
     */
    public static boolean isValidElementSelectorFormat(String selector) {
        if (selector == null || selector.trim().isEmpty()) {
            log.debug("Element selector in null.");
            return false;
        }
        boolean result;

        try {
            By by = convertSelectorTemplateToBy(selector, "");
            WebDriverFactory.getDriver().findElements(by);
            result = true;
        }
        catch (Exception e) {
            result = false;
        }
        log.debug("Element selector {} format is valid: {}.",
                selector, result);
        return result;
    }

    private static synchronized void initializeKeyBoardListener() {
        if (keyboardListener.get() == null) {
            keyboardListener.set(new GlobalKeyboardListener());
            keyboardListener.get().initialize();
            log.debug("Keyboard listener is initialized.");
        }
    }

    private static String getCssOrXpathElementSelector(WebElement element, boolean isCss, boolean unique) {
        Map<String, String> attributes = getAllAttributes(element);
        String elementSelector = null;

        for (String attributeName : reliableAttributes) {
            if (attributes.containsKey(attributeName)) {
                String attributeValue = attributes.get(attributeName);

                if (isCss) {
                    if (attributeName.equals("id") && !attributeValue.contains(" ")) {
                        elementSelector = String.format("#%s", attributeValue);
                    } else if (attributeName.equals("class") && !attributeValue.contains(" ")) {
                        elementSelector = String.format(".%s", attributeValue);
                    } else {
                        elementSelector = String.format("%s[%s='%s']",
                                element.getTagName(), attributeName, attributeValue);
                    }
                } else {
                    elementSelector = String.format("//%s[@%s='%s']",
                            element.getTagName(), attributeName, attributeValue);
                }
                if (isSelectorValidAndUnique(element, null, elementSelector)) {
                    break;
                }
            }
        }
        if (elementSelector == null) {
            elementSelector = getCombinedElementSelector(element, true, unique);
        }
        log.debug("Element {} unique={} CSS selector detected: {}.",
                element, unique, elementSelector);
        return elementSelector;
    }

    private static String getCombinedElementSelector(WebElement element, boolean isCss, boolean isUnique) {
        String tagName = element.getTagName();
        StringBuilder selector = isCss ?
                new StringBuilder(tagName) :
                new StringBuilder(String.format("//%s[", tagName));
        Map<String, String> attributes = getAllAttributes(element);
        String attributeFormat = isCss ? "[%s='%s']" : "@%s='%s'";
        String strSelector = null;

        for (String attributeName : reliableAttributes) {
            if (attributes.containsKey(attributeName)) {
                String attributeValue = attributes.get(attributeName);

                if (!isCss && selector.toString().contains("='")) {
                    selector.append(" and ");
                }
                selector.append(String.format(attributeFormat, attributeName, attributeValue));
                strSelector = selector.toString();

                if (!isCss) {
                    strSelector += "]";
                }
                if (isSelectorValidAndUnique(element, null, strSelector)) {
                    break;
                }
            }
        }
        String combinedCss = isUnique ? null : strSelector;
        log.debug("Element {} isCSS={} isUnique={} combined selector detected: {}.",
                element, isCss, isUnique, combinedCss);
        return combinedCss;
    }

    private static String getElementCssSelectorByParent(WebElement element) {
        WebElement parent = getParentElement(element);
        String tagName = element.getTagName();
        String combinedCss = getCombinedElementSelector(element, true, false);
        String elementCss = null;

        for (int i = 0; i < MAX_ELEMENT_NESTING; i ++) {
            if (parent.getTagName().equals("body")) {
                log.debug("body element was reached while looking element {} " +
                        "CSS selector by parent", element);
                return null;
            }
            String parentCss = getCssOrXpathElementSelector(parent, true, true);

            if (parentCss != null) {
                elementCss = String.format("%s %s",
                        parentCss, tagName);

                if (isSelectorValidAndUnique(element, null, elementCss)) {
                    break;
                }
                elementCss = String.format("%s %s",
                        parentCss, combinedCss);

                if (isSelectorValidAndUnique(element, null, elementCss)) {
                    break;
                }
            }
        }
        log.debug("Element {} CSS selector {} is detected by its parent {}.",
                element, elementCss, parent);
        return null;
    }

    private static String getElementCssSelectorBySibling(WebElement element) {
        List<WebElement> siblings = getSiblingElements(element);
        String tagName = element.getTagName();
        String combinedCss = getCombinedElementSelector(element, true, false);
        String elementCss = null;
        String siblingCss = null;

        for (WebElement sibling : siblings) {
            siblingCss = getCssOrXpathElementSelector(sibling, true, true);

            if (siblingCss != null) {
                elementCss = String.format("%s ~ %s", siblingCss, tagName);

                if (isSelectorValidAndUnique(element, null, elementCss)) {
                    break;
                }
                elementCss = String.format("%s ~ %s", siblingCss, combinedCss);

                if (isSelectorValidAndUnique(element, null, elementCss)) {
                    break;
                }
            }
        }
        log.debug("Element {} CSS selector {} is detected by sibling CSS {}.",
                element, elementCss, siblingCss);
        return elementCss;
    }

    private static String getElementCssSelectorByChild(WebElement element) {
        List<WebElement> childElements = getAllChildElements(element);
        String tagName = element.getTagName();
        String combinedCss = getCombinedElementSelector(element, true, false);
        String elementCss = null;
        String childElementCss = null;

        for (WebElement childElement : childElements) {
            childElementCss = getCssOrXpathElementSelector(childElement, true, true);

            if (childElementCss != null) {
                elementCss = String.format("%s:has(%s)",
                        tagName, childElementCss);
                if (isSelectorValidAndUnique(element, null, elementCss)) {
                    break;
                }
                elementCss = String.format("%s:has(%s)",
                        combinedCss, childElementCss);

                if (isSelectorValidAndUnique(element, null, elementCss)) {
                    break;
                }
            }
        }
        log.debug("Element {} CSS selector {} is detected by sibling CSS {}.",
                element, elementCss, childElementCss);
        return elementCss;
    }

    private static WebElement getElementByText(String text, boolean contains) {
        WebDriver driver = WebDriverFactory.getDriver();
        String format = contains ? "//*[contains(text(),'%s')]" : "//*[text()='%s']";
        String xpath = String.format(format, text);
        List<WebElement> elements = driver.findElements(By.xpath(xpath));
        WebElement elementByText = null;

        if (elements.size() == 1) {
            elementByText = elements.get(0);
        }
        else {
            for (String textAttribute : textAttributes) {
                format = contains ? "//*[contains(@%s,'%s')]" : "//*[@%s='%s']";
                xpath = String.format(format, textAttribute, text);
                elements = driver.findElements(By.xpath(xpath));

                if (elements.size() == 1) {
                    elementByText = elements.get(0);
                    break;
                }
            }
        }
        log.debug("Element {} is found by its text contains={}.",
                elementByText, contains);
        return elementByText;
    }

    private static String getElementXpathSelectorByText(String text, boolean contains) {
        String format = contains ? "//%s[contains(text(),'%s')]" : "//%s[text()='%s']";
        WebElement element = getElementByText(text, contains);
        String elementXpath = null;

        if (element != null) {
            elementXpath = String.format(format, element.getTagName(), text);

            if (!isSelectorValidAndUnique(element, text, elementXpath)) {
                format = contains ? "//%s[contains(@%s,'%s')]" : "//%s[@%s='%s']";

                for (String textAttribute : textAttributes) {
                    elementXpath = String.format(format, element.getTagName(), textAttribute, text);

                    if (isSelectorValidAndUnique(element, text, elementXpath)) {
                        break;
                    }
                }
            }
        }
        log.debug("Element {} XPATH selector {} by text '{}' contains={} is detected.",
                element, elementXpath, text, contains);
        return elementXpath;
    }

    private static String getElementXpathSelectorByText(WebElement element, String text, boolean contains) {
        String format = contains ? "//%s[contains(.,'%s')]": "//%s[text()='%s']";
        String combinedFormat = contains ? "%s and contains(.,'%s')]": "%s and text()='%s']";
        String elementXpath = String.format(format, element.getTagName(), text);
        String xpathByText = null;

        if (isSelectorValidAndUnique(element, text, elementXpath)) {
            xpathByText = elementXpath;
        }
        else {
            String combinedXpath = getCombinedElementSelector(element, false, false);
            elementXpath = String.format(combinedFormat,
                    trimSelectorLastBracket(combinedXpath), text);

            if (isSelectorValidAndUnique(element, text, elementXpath)) {
                xpathByText = elementXpath;
            }
            else {
                Map<String, String> attributes = getAllAttributes(element);
                format = contains ? "//%s[contains(@%s,'%s')]" : "//%s[@%s='%s']";

                for (String textAttribute : textAttributes) {
                    if (attributes.containsKey(textAttribute)) {
                        elementXpath = String.format(format, element.getTagName(), textAttribute, text);

                        if (isSelectorValidAndUnique(element, text, elementXpath)) {
                            xpathByText = elementXpath;
                            break;
                        }
                    }
                }

                for (String attributeName : attributes.keySet()) {
                    if (attributeName.startsWith("data-")) {
                        elementXpath = String.format(format, element.getTagName(), attributeName, text);

                        if (isSelectorValidAndUnique(element, text, elementXpath)) {
                            xpathByText = elementXpath;
                            break;
                        }
                    }
                }
            }
        }
        log.debug("Element {} XPATH selector {} by text '{}' contains={} is detected.",
                element, xpathByText, text, contains);
        return xpathByText;
    }

    private static String getElementXpathSelectorByParentText(WebElement element, String text, boolean contains) {
        String parentXpath = getElementXpathSelectorByText(text, contains);

        if (parentXpath == null) {
            log.debug("Element {} XPATH selector by parent text '{}' contains={} is null.",
                    element, text, contains);
            return null;
        }
        String elementXpath = String.format("%s//%s", parentXpath, element.getTagName());
        String xpathByParentText = null;

        if (isSelectorValidAndUnique(element, text, elementXpath)) {
            log.debug("Element {} XPATH selector {} by parent text '{}' contains={} is detected.",
                    element, text, contains, elementXpath);
            xpathByParentText = elementXpath;
        }
        else {
            String combinedXpath = getCombinedElementSelector(element, false, false);
            elementXpath = String.format("%s%s", parentXpath, combinedXpath);

            if (isSelectorValidAndUnique(element, text, elementXpath)) {
                xpathByParentText = elementXpath;
            }
        }
        log.debug("Element {} XPATH selector {} by parent text '{}' contains={} is detected.",
                element, xpathByParentText, text, contains);
        return xpathByParentText;
    }

    private static String getElementXpathSelectorBySiblingText(WebElement element, String text, boolean contains) {
        String siblingXpath = getElementXpathSelectorByText(text, contains);

        if (siblingXpath == null) {
            log.debug("Element {} XPATH selector by sibling text '{}' contains={} is null.",
                    element, text, contains);
            return null;
        }
        String elementTag = element.getTagName();
        String elementCombinedXpath = getCombinedElementSelector(element, false, false);
        WebElement parent = getParentElement(element);
        String elementXpath = null;

        for (int i = 0; i < MAX_ELEMENT_NESTING; i++) {
            if (parent.getTagName().equals("body")) {
                break;
            }
            String parentXpath = getCssOrXpathElementSelector(parent, false, true);
            if (parentXpath != null) {
                elementXpath = String.format("%s[%s]//%s",
                        parentXpath, trimXpathRootSlashes(siblingXpath), elementTag);

                if (isSelectorValidAndUnique(element, text, elementXpath)) {
                    break;
                }
                elementXpath = String.format("%s[%s]%s",
                        parentXpath, trimXpathRootSlashes(siblingXpath), elementCombinedXpath);

                if (isSelectorValidAndUnique(element, text, elementXpath)) {
                    break;
                }
            }
            parent = getParentElement(parent);
        }
        log.debug("Element {} XPATH selector {} by sibling text '{}' contains={} is detected.",
                element, elementXpath, text, contains);
        return null;
    }

    private static String getElementXpathSelectorByChildText(WebElement element, String text, boolean contains) {
        String childXpath = getElementXpathSelectorByText(text, contains);
        String xpathByChildText = null;

        if (childXpath == null) {
            log.debug("Element {} XPATH selector by child text '{}' contains={} is null.",
                    element, text, contains);
            return null;
        }
        String elementXpath = String.format("//%s[%s]", element.getTagName(), trimXpathRootSlashes(childXpath));

        if (isSelectorValidAndUnique(element, text, elementXpath)) {
            xpathByChildText = elementXpath;
        }
        else {
            String combineXpath = getCombinedElementSelector(element, false, false);
            elementXpath = String.format("%s[%s]", combineXpath, trimXpathRootSlashes(childXpath));

            if (isSelectorValidAndUnique(element, text, elementXpath)) {
                xpathByChildText = elementXpath;
            }
        }
        log.debug("Element {} XPATH selector {} by child text '{}' " +
                "contains={} is detected.", element, xpathByChildText, text, contains);
        return xpathByChildText;
    }

    private static String getElementXpathSelectorByIndex(WebElement element) {
        WebDriver driver = WebDriverFactory.getDriver();
        String combinedSelector = getCombinedElementSelector(element, false, false);
        String selectorByIndex = combinedSelector;

        if (!isSelectorValidAndUnique(element, null, combinedSelector)) {
            By by = convertSelectorTemplateToBy(combinedSelector, null);
            List<WebElement> foundElements = driver.findElements(by);
            int size = foundElements.size();

            for (int i = 1; i <= size; i++) {
                selectorByIndex = String.format("(%s)[%d]", combinedSelector, i);
                by = convertSelectorTemplateToBy(selectorByIndex, null);
                foundElements = driver.findElements(by);

                if (foundElements.size() == 1) {
                    if (compareWebElements(element, foundElements.get(0))) {
                        break;
                    }
                }
            }
        }
        log.debug("Element {} XPATH selector {} by id is detected.",
                element, selectorByIndex);
        return selectorByIndex;
    }

    private static String trimXpathRootSlashes(String selector) {
        if (selector == null) {
            return null;
        }
        selector = selector.trim();
        if (isXpath(selector)) {
            String trimmedSelector = selector.substring(2);
            log.debug("XPATH {} without leading // is returned: {}.",
                    selector, trimmedSelector);
            return trimmedSelector;
        }
        return selector;
    }

    private static String trimSelectorLastBracket(String selector) {
        if (selector == null) {
            return null;
        }
        selector = selector.trim();
        if (!selector.isEmpty()) {
            String trimmedSelector =  selector.substring(0, selector.length() - 1);
            log.debug("Selector {} without ending ] is returned: {}",
                    selector, trimmedSelector);
        }
        return selector;
    }

    private static ExpectedCondition<Boolean> alertIsNotPresent() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    driver.switchTo().alert();
                    return false; // Alert is still present
                } catch (NoAlertPresentException e) {
                    return true; // Alert is not present
                }
            }
        };
    }

    private static String escapeJS(String script) {
        String escapedScript = script.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n");
        log.debug("JS {} after escape: {}.", script, escapedScript);
        return escapedScript;
    }

    private static String escapeJSON(String json) {
        String escapedJson = json.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        log.debug("JS {} after escape: {}.", json, escapedJson);
        return escapedJson;
    }

    private static String replaceDoubleQuotesWithSingleQuotes(String selector) {
        String updatedSelector = selector.replace("\\\"", "#ESCAPED_QUOTE#")
                .replace("\"", "'")
                .replace("#ESCAPED_QUOTE#", "\"");
        log.debug("Selector {} was updated - double quotes replaced with single quotes: {}.",
                selector, updatedSelector);
        return updatedSelector;
    }

    private static void terminateAllTests() {
        log.info("Test was terminated by user in debug mode.");
        WebDriverFactory.closeAllDrivers();
        ServerUtils.terminateAllSeleniumServers();
        ServerUtils.terminateAwsRmiServer();
        System.exit(-1);
    }
}
