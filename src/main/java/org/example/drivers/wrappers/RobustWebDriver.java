package org.example.drivers.wrappers;

import lombok.extern.slf4j.Slf4j;
import org.example.data.Config;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.constants.Settings.*;

/**
 * The robust WebDriver wrapper class.
 * This class wapps WebDriver, adds auto wait and retry on error
 * to make WebDriver more reliable.
 */
@Slf4j
public class RobustWebDriver implements WebDriver, JavascriptExecutor, TakesScreenshot {
    static protected final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
    private static final int PAGE_LOAD_TIMEOUT_SEC = 15;
    private final WebDriver driver;
    private final RobustWebDriverWaiter waiter;

    /**
     * Robust WebDriver constructor.
     * @param driver The wrapped WebDriver instance.
     */
    public RobustWebDriver(WebDriver driver) {
        this.driver = driver;
        waiter = new RobustWebDriverWaiter(driver);
        log.debug("RobustWebDriver object is created by WebDriver.");
    }

    /**
     * Opens browser page by its URL.
     * @param url The page URL.
     */
    @Override
    public void get(String url) {
        driver.get(url);
        waiter.waitForPageLoad(PAGE_LOAD_TIMEOUT_SEC);
        log.debug("Web page {} is open.", url);
    }

    /**
     * Returns current page URL.
     * @return The current page URL.
     */
    @Override
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        log.debug("The current page url is: {}.", url);
        return url;
    }

    /**
     * Returns the page title.
     * @return The page title.
     */
    @Override
    public String getTitle() {
        String title = driver.getTitle();
        log.debug("The current page title is: {}.", title);
        return title;
    }

    /**
     * Finds web elements by element locator.
     * @param by The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = driver.findElements(by);
        List<WebElement> robustElements = elements.stream().map(element ->
                new RobustWebElement(element, null, by, driver, waiter))
                .collect(Collectors.toList());
        log.debug("{} web elements are found by selector{}:\n{}.",
                robustElements.size(), by, robustElements);
        return robustElements;
    }

    /**
     * Finds web element by its locator.
     * @param by The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By by) {
        for (int i = 1; i <= RETRY_COUNT; i++) {
            try {
                WebElement element = new RobustWebElement(
                        driver.findElement(by), null, by, driver, waiter);
                log.debug("Web element {} is found by selector {}.", element, by);
                return element;
            }
            catch (NoSuchElementException e) {
                if (i == RETRY_COUNT || config.getDebugMode()) {
                    throw e;
                }
            }
            WaiterUtils.waitMilliSeconds(RETRY_WAIT_MILLISECONDS);
            log.debug("Retry {} to find WebElement by selector {}", i, by);
        }
        log.debug("null WebElement is found by selector {}.", by);
        return null;
    }

    /**
     * Returns the page source.
     * @return The page source.
     */
    @Override
    public String getPageSource() {
        String pageSource = driver.getPageSource();
        log.debug("Current page source is:]\n{}", pageSource);
        return pageSource;
    }

    /**
     * Closes the browser window.
     */
    @Override
    public void close() {
        String title = getTitle();
        driver.close();
        log.debug("Browser window {} is closed.", title);
    }

    /**
     * Quits the browser.
     */
    @Override
    public void quit() {
        if (driver != null) {
            driver.quit();
            log.debug("Browser quit.");
        }
    }

    /**
     * Returns the browser window handles.
     * @return The set of window handles.
     */
    @Override
    public Set<String> getWindowHandles() {
        Set<String>  windowHandles = driver.getWindowHandles();
        log.debug("Current browser window handles are:\n{}", windowHandles);
        return windowHandles;
    }

    /**
     * Returns the browser window handle.
     * @return The window handle.
     */
    @Override
    public String getWindowHandle() {
        String windowHandle = driver.getWindowHandle();
        log.debug("Current browser window handle is: {}", windowHandle);
        return windowHandle;
    }

    /**
     * Switches to browser window.
     * @return The target locator.
     */
    @Override
    public TargetLocator switchTo() {
        TargetLocator targetLocator = driver.switchTo();
        log.debug("Target locator is: {}.", targetLocator);
        return targetLocator;
    }

    /**
     * Returns Navigate instance.
     * @return The Navigate instance.
     */
    @Override
    public Navigation navigate() {
        Navigation navigation = driver.navigate();
        log.debug("Navigation is: {}.", navigation);
        return navigation;
    }

    /**
     * Returns Manage instance.
     * @return The Manage instance.
     */
    @Override
    public Options manage() {
        Options options = driver.manage();
        log.debug("Options are: {}.", options);
        return options;
    }

    /**
     * Executes JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeScript(String script, Object... args) {
        Object result = ((JavascriptExecutor)driver).executeScript(script, args);
        log.debug("JavaScript executor result: {}.\nScript:\n{}", result, script);
        return result;
    }

    /**
     * Executes asynchronous JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        Object result = ((JavascriptExecutor)driver).executeAsyncScript(script, args);
        log.debug("JavaScript asynchronous executor result: {}.\nScript:\n{}", result, script);
        return result;
    }

    /**
     * Returns screenshot data.
     * @param target The screenshot target.
     * @return The screenshot data.
     * @param <X> The screenshot data type.
     * @throws WebDriverException The exception in case of error.
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        X screenshot = ((TakesScreenshot)driver).getScreenshotAs(target);
        log.debug("Screenshot is taken: {}", screenshot);
        return screenshot;
    }
}
