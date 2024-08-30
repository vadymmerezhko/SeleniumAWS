package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.configs.TestConfig;
import org.example.data.SmartObject;
import org.example.data.SmartValue;
import org.example.drivers.elements.SmartElement;
import org.example.drivers.factories.WebDriverFactory;
import org.example.drivers.selectors.Selector;
import org.example.drivers.selectors.SelectorType;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;

/**
 * Base page class.
 * Contains common functionality for all page classes.
 */
@Slf4j
public abstract class SmartPage extends SmartObject {
    private static final ConcurrentMap<String, String> pageUrlsMap =
            readPageURLsFromFiles();
    private static final String UNDEFINED = "undefined";

    protected WebDriver driver;
    protected SmartValue url = new SmartValue();

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
    SmartPage() {
        driver = WebDriverFactory.getDriver();
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
        catch (WebDriverException e) {
            if (Config.getInstance().getDebugMode()) {
                url.setValue(handlePageOpenError());
            }
            else {
                throw e;
            }
        }
        driver.get(url.toString());
    }

    /**
     * Opens web page by its URL.
     * @param url The page URL.
     */
    public void open(String url) {
        driver.get(url);
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

    protected void initialize() {
        try {
            Field[] fields = getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(this);

                if (fieldObject instanceof SmartElement) {
                    ((SmartElement) fieldObject).setPage(this);
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot initialize web page %s.", getClass().getSimpleName()), e);
        }
    }

    /**
     * Saves element selector to JSON file.
     * @param pageName The page name.
     * @param url The page URL.
     */
    private void savePageUrlToFile(String folderPath, String pageName, String url, String siteHost) {
        String filePath = null;

        try {
            String fileName = String.format("%s.json", pageName);
            JSONObject json;
            filePath = String.format("%s/%s", folderPath, fileName);

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
                savePageUrlToFile(PAGE_OBJECTS_FOLDER_PATH, pageName, url, siteHost);
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
        savePageUrlToFile(PAGE_OBJECTS_FOLDER_PATH, pageName, UNDEFINED, siteHost);
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
}
