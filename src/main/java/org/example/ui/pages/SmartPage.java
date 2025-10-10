package org.example.ui.pages;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import org.example.configs.Config;
import org.example.configs.TestConfig;
import org.example.data.SmartData;
import org.example.data.SmartObject;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.interfaces.WritableObject;
import org.example.ui.factories.WebDriverFactory;
import org.example.ui.selectors.Selector;
import org.example.ui.selectors.SelectorType;
import org.example.ui.wrappers.SmartElement;
import org.example.ui.wrappers.WebSynchronizer;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;

/**
 * Base page class.
 * Contains common functionality for all page classes.
 */
// TODO: add unit tests
@Slf4j
public abstract class SmartPage extends SmartObject {
    private static final ConcurrentMap<String, String> pageUrlsMap = readPageURLsFromFiles();
     private static final String UNDEFINED = "undefined";

    protected WebDriver driver;
    protected SmartValue url = new SmartValue();
    protected WebSynchronizer synchro;

    /**
     * Reads asynchronously all pages URLs from JSON files
     * to URL map.
     * If no files or they are empty then empty map is returned.
     * @return  The element name - selector map.
     */
    private static ConcurrentMap<String, String> readPageURLsFromFiles() {
        ConcurrentMap<String, String> pageUrlsMap = new ConcurrentHashMap<>();
        Set<String> fileNames = FileSystemUtils.getFileNamesInFolder(PAGE_OBJECTS_FOLDER_PATH);

        Thread thread = new Thread(() -> {
            try {
                log.debug("Asynchronous page object element selectors reading began.");

                for (String fileName : fileNames) {
                    getUrlFromPageFile(fileName, pageUrlsMap);
                }
                log.debug("Asynchronous page object element selectors reading finished.");
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read all web page files from: %s", PAGE_OBJECTS_FOLDER_PATH), e);
            }
        });
        thread.start();
        return pageUrlsMap;
    }

    /**
     * Base page constructor.
     */
    public SmartPage() {
        driver = WebDriverFactory.getDriver();
        synchro = new WebSynchronizer();
        log.debug("Smart page is constructed:\n{}", this);
    }

    /**
     * Sets a keyword.
     * @param keyword The keyword.
     */
    public void setKeyword(String keyword) {
        url.setKeyword(keyword);
        log.debug("Page keyword is set: '{}'", keyword);
    }

    /**
     * Gets a keyword.
     * @ keyword The keyword.
     */
    public Object getKeyword() {
        Object keyword = url.getKeyword();
        log.debug("Page keyword is returned: '{}'", keyword);
        return keyword;
    }

    /**
     * Opens web page by its URL.
     */
    public void open() {

        try {
            if (url.getValue() == null) {
                url.setValue(getPageUrl());
            }
            driver.get(url.toString());
            return;
        }
        catch (Exception e) {
            if (Config.getInstance().getDebugMode()) {
                url.setValue(handlePageOpenError());
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot open %s smart page with URL: %s",
                        getClass().getName(), url), e);
            }
        }
        driver.get(url.toString());
    }

    /**
     * Opens web page by its URL.
     * @param url The page URL.
     */
    public void open(String url) {

        try {
            driver.get(url);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot open %s smart page with URL: %s",
                    getClass().getName(), url), e);
        }
    }

    /**
     * Returns page URL.
     * @return The page URL.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Refreshes the page.
     */
    public void refresh() {
        driver.navigate().refresh();
    }

    /**
     * Navigates back.
     */
    public void back() {
        driver.navigate().back();
    }

    /**
     * Navigates forward.
     */
    public void forward() {
        driver.navigate().forward();
    }

    /**
     * Scrolls page down.
     */
    public void scrollDown() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.END);
    }

    /**
     * Scrolls page up.
     */
    public void scrollUp() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.UP);
    }

    /**
     * Scrolls page right.
     */
    public void scrollRight() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.RIGHT);
    }

    /**
     * Scrolls page left.
     */
    public void scrollLeft() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.LEFT);
    }

    /**
     * Scrolls page to the bottom.
     */
    public void scrollToBottom() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.END);
    }

    /**
     * Scrolls page to the top.
     */
    public void scrollToTop() {
        driver.findElement(By.cssSelector("body")).sendKeys(Keys.HOME);
    }

    /**
     * Scrolls page to web element.
     * @param element The element to scroll to.
     */
    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Sets all page input elements from input data values
     * when input element and value have the same name
     * in the order as it goes in the input data.
     * @param inputData The input data.
     */
    public void setAllInputs(SmartData inputData) {

        try {
            Field[] inputFields = inputData.getClass().getDeclaredFields();
            Field[] pageFields = getClass().getDeclaredFields();

            for (Field iputField : inputFields) {
                iputField.setAccessible(true);
                Object inputValue = iputField.get(inputData);

                if (!(inputValue instanceof SmartValue)) {
                    continue;
                }
                for (Field pageField : pageFields) {
                    pageField.setAccessible(true);
                    Object pageElement = pageField.get(this);

                    if (!(pageElement instanceof SmartElement) &&
                        !(pageElement instanceof WritableObject)) {
                        continue;
                    }
                    if (iputField.getName().equals(pageField.getName())) {
                        ((WritableObject) pageElement).setValue(inputValue);
                    }
                }
            }
            log.debug("All {} page input elements are set", getClass().getSimpleName());
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot set all %s page input elements.",
                    getClass().getSimpleName()), e);
        }
    }

    /**
     * Sets all page output data values from page elements
     * when page element and value have the same name
     * in the order as it goes in the output data.
     * @param outputData The output data.
     */
    public void setAllOutputs(SmartData outputData) {

        try {
            Field[] outputFields = outputData.getClass().getDeclaredFields();
            Field[] pageFields = getClass().getDeclaredFields();

            for (Field outputField : outputFields) {
                outputField.setAccessible(true);
                Object outputValue = outputField.get(outputData);

                if (!(outputValue instanceof SmartValue)) {
                    continue;
                }
                for (Field pageField : pageFields) {
                    pageField.setAccessible(true);
                    Object pageElement = pageField.get(this);

                    if (!(pageElement instanceof SmartElement) &&
                            !(pageElement instanceof ReadableObject)) {
                        continue;
                    }

                    if (outputField.getName().equals(pageField.getName())) {
                        SmartValue actualSmartValue = ((ReadableObject) pageElement).getValue();
                        outputField.set(outputData, actualSmartValue);
                    }
                }
            }
            log.debug("All {} page output values are set", getClass().getSimpleName());
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot set all %s page output values.",
                    getClass().getSimpleName()), e);
        }
    }

    /**
     * Converts smart page object to string.
     * @return The string.
     */
    @Override
    public String toString() {
        String string = String.format("%s: %s", getClass().getName(), url);
        log.debug("Smart by page is converted to string: {}", string);
        return string;
    }

    /**
     * Compares this smart page to other object.
     * @param object The object.
     * @return The result: equals - true, otherwise - false.
     */
    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart page equals() called. The actual object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart page equals() called. The actual and expected are the same object.");
            return true;
        }
        if (object instanceof SmartPage actualSmartPage) {
            try {
                boolean result = url.equals(actualSmartPage.url);
                log.debug("""
                        Two smart pages objects are compared.
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
                        Cannot compare two smart page objects.
                        Expected:
                        %s
                        Actual:
                        %s
                        """.stripIndent(),
                        this,
                        object), e);
            }
        }
        log.debug("Smart page equals() returned false.\n" +
                "The actual object is not a smart page.");
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }


    /**
     * Generates getters for SmartElement fields
     * declared in the class derived from SmartPage class.
     */
    private void generateGetters() {
        Class<?> pageClass = getClass();

        try {
            DynamicType.Builder<?> builder = new ByteBuddy().subclass(pageClass);
            Field[] fields = pageClass.getDeclaredFields();

            for (Field field : fields) {

                if (SmartElement.class.isAssignableFrom(field.getType())) {
                    String fieldName = field.getName();
                    String getterName = String.format("get%s%s",
                            fieldName.substring(0, 1).toUpperCase(),
                            fieldName.substring(1));
                    // Dynamically generate getter method using ByteBuddy
                    builder = builder.defineMethod(getterName, field.getType(), Modifier.PUBLIC)
                            .intercept(FieldAccessor.ofField(fieldName));  // Intercept the getter to return the field
                }
            }
            // Load the dynamically generated class
            Class<?> dynamicClass = builder.make()
                    .load(pageClass.getClassLoader())
                    .getLoaded();
            // Replace the current instance with the dynamically generated one
            SmartPage newPage = (SmartPage) dynamicClass.getDeclaredConstructor().newInstance();
            // Copy all fields from the dynamically generated class to the current instance
            copyFields(newPage, this);
            log.debug("Smart element getter methods are dynamically generated.");
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot generate getter methods dynamically for %s smart page.",
                    pageClass.getName()), e);
        }
    }

    /**
     * Saves element selector to JSON file.
     * @param pageName The page name.
     * @param url The page URL.
     */
    private void savePageUrlToFile(String pageName, String url, String siteHost) {
        String filePath = null;

        try {
            String fileName = String.format("%s.json", pageName);
            JSONObject json;
            filePath = String.format("%s/%s", PAGE_OBJECTS_FOLDER_PATH, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);
                json = new JSONObject(jsonString);
            } else {
                json = new JSONObject();
            }
            String urlTemplate = url.replace(siteHost, SITE_HOST_PLACEHOLDER);
            Selector selector = new Selector(SelectorType.URL, urlTemplate, null);
            json.put(PAGE_URL_FIELD_NAME, selector.toString());
            FileSystemUtils.createFile(filePath, json.toString(JSON_LAYOUT_SPACES));
            log.debug("Page {} URL {} is saved to file {}.",
                    pageName, url, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not save web page %s URL %s to file: %s",
                    pageName, url, filePath), e);
        }
    }

    /**
     * Reads page URL from file.
     * @param pageName The page name.
     * @return The page URL.
     */
    private String readPageUrlFromFile(String pageName) {
        String filePath = null;
        try {
            String fileName = String.format("%s.json", pageName);

            getUrlFromPageFile(fileName, pageUrlsMap);
            return pageUrlsMap.get(pageName);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not read web page %s URL from file: %s",
                    pageName, filePath), e);
        }
    }

    private String getPageUrl() {
        String url;
        String pageName = this.getClass().getSimpleName();

        if (pageUrlsMap.containsKey(pageName)) {
            url = pageUrlsMap.get(pageName);
        }
        else {
            url = readPageUrlFromFile(pageName);

            if (url != null) {
                String siteHost = TestConfig.getInstance().getSiteHost();

                if (!url.trim().startsWith(siteHost)) {
                    log.debug("{} URL {} does not start with site host {}.", pageName, url, siteHost);
                    url = null;
                }
            }
        }
        while (url == null) {
            String currentUrl = getCurrentUrl();
            String siteHost = TestConfig.getInstance().getSiteHost();

            if (Config.getInstance().getDebugMode()) {
                String promptMessage = String.format(
                       """
                       PAGE

                       Please enter %s URL or just click OK to save it
                       """.stripIndent(),
                        pageName);

                url = WebUtils.showPrompt(promptMessage, currentUrl);

                try {
                    WebDriverFactory.getDriver().get(url);
                }
                catch (WebDriverException e) {
                    String confirmMessage = String.format("""
                        "PAGE
                        
                        Invalid %s URL: %s
                       
                        Click OK to enter the valid URL.
                        Or click CANCEL to exit the test.
                        """.stripIndent(),
                        pageName, url);

                    if (!WebUtils.showConfirm(confirmMessage)) {
                        log.error("User made hard system exit on page URL confirm popup.");
                        WebDriverFactory.hardSystemExit();
                    }
                    url = null;
                    continue;
                }
                if (!url.trim().startsWith(siteHost)) {
                    String wrongHostMessage = String.format("""
                        PAGE

                        %s URL %s does not start with site host %s.

                        Click OK to enter valid URL.
                        Or click CANCEL to exit the test.
                        """.stripIndent(),
                        pageName, url, siteHost);

                    if (!WebUtils.showConfirm(wrongHostMessage)) {
                        log.error("User made hard system exit on page URL confirm popup.");
                        WebDriverFactory.hardSystemExit();
                    }
                    url = null;
                    continue;
                }
                savePageUrlToFile(pageName, url, siteHost);
                pageUrlsMap.put(pageName, url);
                break;
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Page %s URL is undefined.", pageName));
            }
        }
        log.debug("{} URL: {}.", pageName, url);
        return url;
    }

    private String handlePageOpenError() {
        String pageName = getClass().getSimpleName();
        String siteHost = TestConfig.getInstance().getSiteHost();
        // Save wrong URL to fix it in debug mode.
        savePageUrlToFile(pageName, UNDEFINED, siteHost);
        pageUrlsMap.remove(pageName);
        return getPageUrl();
    }

    private static void getUrlFromPageFile(String fileName, Map<String, String> urlsMap) {
        String filePath = String.format("%s/%s", PAGE_OBJECTS_FOLDER_PATH, fileName);
        String fileContent = FileSystemUtils.readFile(filePath);
        String pageName = FileSystemUtils.getFileNameWithoutExtension(fileName);
        String siteHost = TestConfig.getInstance().getSiteHost();

        if (!fileContent.trim().isEmpty()) {
            JSONObject json = new JSONObject(fileContent);

            if (json.has(PAGE_URL_FIELD_NAME)) {
                String urlTemplate = json.get(PAGE_URL_FIELD_NAME).toString();
                urlTemplate = urlTemplate.replace(SITE_HOST_PLACEHOLDER, siteHost);
                Selector selector = Selector.fromString(urlTemplate);
                log.debug("{} page URL {} is read from file {}.",
                        pageName, urlTemplate, filePath);
                String url = selector.totValueString();
                urlsMap.put(pageName, url);
                log.debug("{} page URL: {}", pageName, url);
            }
        }
    }

    private void copyFields(Object source, Object target) throws IllegalAccessException {
        Field[] fields = source.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);  // Make the private fields accessible

            // Copy the field's value from source to target
            field.set(target, field.get(source));
        }
    }
}
