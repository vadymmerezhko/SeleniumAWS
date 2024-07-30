package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.configs.TestConfig;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.WebUtils;
import org.openqa.selenium.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base page class.
 * Contains common functionality for all page classes.
 */
@Slf4j
public abstract class BasePage {
    private static final ConcurrentMap<String, String> pageUrlMap = new ConcurrentHashMap<>();

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
                url = handlePageOPenError();
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

    private String getPageUrl() {
        String url;
        String pageName = this.getClass().getSimpleName();
        String siteHost = TestConfig.getInstance().getSiteHost();
        String pagesFolderPath = Config.getInstance().getPagesFolderPath();
        String logError =
                "\n///////////////////////////////////////////////////////////\n\n" +
                "User made hard system exit on page URL confirm popup.\n\n" +
                "///////////////////////////////////////////////////////////";

        if (pageUrlMap.containsKey(pageName)) {
            url = pageUrlMap.get(pageName);
        }
        else {
            url = WebUtils.readPageUrlFromFile(pagesFolderPath, pageName, siteHost);

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
                        "PAGE\n\n" +
                        "Please enter %s URL or just click OK to save it",
                        pageName);

                url = WebUtils.showPrompt(promptMessage, currentUrl);

                try {
                    WebDriverFactory.getDriver().get(url);
                }
                catch (WebDriverException e) {
                    String confirmMessage = String.format(
                            "PAGE\n\nInvalid %s URL: %s\n\n" +
                            "Click OK to enter the valid URL.\n" +
                            "Or click CANCEL to terminate the test.",
                            pageName, url);

                    if (!WebUtils.showConfirm(confirmMessage)) {
                        log.error(logError);
                        WebDriverFactory.hardSystemExit();
                    }
                    url = null;
                    continue;
                }

                if (!url.trim().startsWith(siteHost)) {
                    String wrongHostMessage = String.format(
                            "PAGE\n\n%s URL %s does not start with site host %s.\n\n" +
                            "Click OK to enter valid URL.\n" +
                            "Or click CANCEL to terminate the test.",
                            pageName, url, siteHost);

                    if (!WebUtils.showConfirm(wrongHostMessage)) {
                        log.error(logError);
                        WebDriverFactory.hardSystemExit();
                    }
                    url = null;
                    continue;
                }

                WebUtils.savePageUrlToFile(pagesFolderPath, pageName, url, siteHost);
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

    private String handlePageOPenError() {
        String pageName = getClass().getSimpleName();
        String siteHost = TestConfig.getInstance().getSiteHost();
        String folderPath = Config.getInstance().getPagesFolderPath();
        // Save empty URL to fix it in debug mode.
        WebUtils.savePageUrlToFile(folderPath, pageName, "", siteHost);
        pageUrlMap.remove(pageName);
        return getPageUrl();
    }
}
