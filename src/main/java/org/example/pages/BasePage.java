package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.configs.TestConfig;
import org.example.drivers.elements.BaseElement;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.FileSystemUtils;
import org.example.utils.WebUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.PAGE_URL_FIELD_NAME;

/**
 * Base page class.
 * Contains common functionality for all page classes.
 */
@Slf4j
public abstract class BasePage {
    private static final ConcurrentMap<String, String> pageUrlMap = new ConcurrentHashMap<>();
    static private final String SITE_HOST_PLACEHOLDER = "#SITE_HOST#";

    protected WebDriver driver;

    /**
     * Base page constructor.
     */
    BasePage() {
        driver = WebDriverFactory.getDriver();
    }

    /**
     * Opens web page by its URL.
     */
    public void open() {
        String url = getPageUrl();
        try {
            driver.get(url);
            return;
        }
        catch (WebDriverException e) {
            if (Config.getInstance().getDebugMode()) {
                url = handlePageOpenError();
            }
            else {
                throw e;
            }
        }
        driver.get(url);
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

                if (fieldObject instanceof BaseElement) {
                    ((BaseElement) fieldObject).setPage(this);
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
            String urlFormat = url.replace(siteHost, SITE_HOST_PLACEHOLDER);
            json.put(PAGE_URL_FIELD_NAME, urlFormat);
            FileSystemUtils.createFile(filePath, json.toString());
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
     * @param folderPath The folder path.
     * @param pageName The page name.
     * @param siteHost The site host.
     * @return The page URL.
     */
    private String readPageUrlFromFile(String folderPath, String pageName, String siteHost) {
        String filePath = null;
        try {
            String fileName = String.format("%s.json", pageName);
            JSONObject json;
            filePath = String.format("%s/%s", folderPath, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);

                if (jsonString.trim().isEmpty()) {
                    return null;
                }
                json = new JSONObject(jsonString);
                try {
                    String urlFormat = json.get(PAGE_URL_FIELD_NAME).toString();
                    log.debug("Page URL format {} is read from file {}.",
                            urlFormat, fileName);
                    String url = urlFormat.replace(SITE_HOST_PLACEHOLDER, siteHost);
                    log.debug("Page URL {} is read from file {}.",
                            url, fileName);

                    if (url.trim().isEmpty()) {
                        return null;
                    }
                    return url;
                }
                catch (JSONException e) {
                    log.debug("File {} has invalid JSON object format: {}",
                            filePath, jsonString);
                    return null;
                }
            }
            else {
                log.debug("Page object {} file {} does not exist.",
                        pageName, filePath);
                return null;
            }
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
        String siteHost = TestConfig.getInstance().getSiteHost();
        String pagesFolderPath = Config.getInstance().getPagesFolderPath();

        if (pageUrlMap.containsKey(pageName)) {
            url = pageUrlMap.get(pageName);
        }
        else {
            url = readPageUrlFromFile(pagesFolderPath, pageName, siteHost);

            if (url != null) {
                if (!url.trim().startsWith(siteHost)) {
                    log.debug("{} URL {} does not start with site host {}.", pageName, url, siteHost);
                    url = null;
                }
            }
        }

        while (url == null) {

            if (Config.getInstance().getDebugMode()) {
                String currentUrl = getCurrentUrl();
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
                savePageUrlToFile(pagesFolderPath, pageName, url, siteHost);
                pageUrlMap.put(pageName, url);
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
        String folderPath = Config.getInstance().getPagesFolderPath();
        // Save empty URL to fix it in debug mode.
        savePageUrlToFile(folderPath, pageName, "", siteHost);
        pageUrlMap.remove(pageName);
        return getPageUrl();
    }
}
