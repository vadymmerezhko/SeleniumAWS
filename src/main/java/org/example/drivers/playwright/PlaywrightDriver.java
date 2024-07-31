package org.example.drivers.playwright;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.ScreenshotType;
import lombok.extern.slf4j.Slf4j;
import org.example.drivers.selectors.SmartByParser;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ClassUtils;
import org.example.utils.ScreenshotUtils;
import org.example.utils.WaiterUtils;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.constants.Settings.WAIT_ELEMENT_DELAY_MILLISECONDS;
import static org.example.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;


/**
 * The Playwright - Selenium WebDriver wrapper class.
 */
@Slf4j
public class PlaywrightDriver implements WebDriver, JavascriptExecutor, TakesScreenshot {
    private Browser browser;
    private PlaywrightPage playwrightPage;
    private final Page page;
    private boolean accessibilityTestEnabled = false;
    private boolean isPageOpen = false;

    /**
     * Playwright driver constructor by Playwright browser instance.
     * @param browser The browser instance.
     * @param page The current web page.
     */
    public PlaywrightDriver(Browser browser, Page page) {
        this.browser = browser;
        this.page = page;
    }

    /**
     * Playwright driver constructor by Playwright page instance.
     * @param playwrightPage The page instance.
     */
    public PlaywrightDriver(PlaywrightPage playwrightPage) {
        this.playwrightPage = playwrightPage;
        page = playwrightPage.getPage();
    }

    /**
     * Sets enabled accessibility test true/false flag.
     * @param enabled The enabled flag.
     */
    public void setAccessibilityTestEnabled(boolean enabled) {
        accessibilityTestEnabled = enabled;
    }

    /**
     * Checks page accessibility.
     * Throws runtime exception in case of accessibility issue(s).
     */
    public void checkAccessibility() {
        if (accessibilityTestEnabled) {
            // Verify page accessibility.
            AxeResults accessibilityScanResults = new AxeBuilder(page).analyze();
            if (!accessibilityScanResults.getViolations().isEmpty()) {
                throw new SmartRuntimeException(String.format(
                        "Accessibility issues:\n%s",
                        accessibilityScanResults.getViolations()));
            }
            log.debug("checkAccessibility()");
        }
    }

    /**
     * Waits for page load.
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        page.waitForFunction("document.readyState === 'complete'");
    }

    /**
     * Opens browser page by its URL.
     * @param url The page URL.
     */
    @Override
    public void get(String url) {
        page.navigate(url);
        waitForPageLoad();
        log.debug("get({})", url);
        isPageOpen = true;
        checkAccessibility();
    }

    /**
     * Returns the current URL.
     * @return The current URL.
     */
    @Override
    public String getCurrentUrl() {
        String currentUrl = page.url();
        log.debug("getCurrentUrl(): {}", currentUrl);
        return currentUrl;
    }

    /**
     * Returns the page title.
     * @return The page title.
     */
    @Override
    public String getTitle() {
        String title = page.title();
        log.debug("getTitle(): {}", title);
        return title;
    }

    /**
     * Finds web elements by its locator.
     * @param by The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        long startMilliseconds = System.currentTimeMillis();
        long waitTimeoutMilliseconds = (long) WAIT_ELEMENT_TIMEOUT_SECONDS * 1000;
        String locatorString = SmartByParser.getLocatorString(by);
        PlaywrightException exception = null;
        List<Locator> locators;

        while ((System.currentTimeMillis() - startMilliseconds) < waitTimeoutMilliseconds) {
            try {
                locators = page.locator(locatorString).all();
                log.debug("Elements found by {}: {}", by, locators);
                return locators.stream()
                        .map(locator -> new PlaywrightElement(by, locator, this))
                        .collect(Collectors.toList());
            }
            catch (PlaywrightException e) {
                exception = e;
            }
            WaiterUtils.waitMilliSeconds(WAIT_ELEMENT_DELAY_MILLISECONDS);
            waitForPageLoad();
        }
        throw new SmartRuntimeException(String.format(
                "Playwright web elements are not found by %s", by), exception);
    }

    /**
     * Finds web element by its locator.
     * @param by The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By by) {
        long startMilliseconds = System.currentTimeMillis();
        long waitTimeoutMilliseconds = (long) WAIT_ELEMENT_TIMEOUT_SECONDS * 1000;
        String locatorString = SmartByParser.getLocatorString(by);
        PlaywrightException exception = null;
        Locator foundLocator;

        while ((System.currentTimeMillis() - startMilliseconds) < waitTimeoutMilliseconds) {
            try {
                foundLocator = page.locator(locatorString);
                log.debug("{}.findElement by {}: {}", page, by, foundLocator);
                return new PlaywrightElement(by, foundLocator, this);
            }
            catch (PlaywrightException e) {
                exception = e;
            }
            WaiterUtils.waitMilliSeconds(WAIT_ELEMENT_DELAY_MILLISECONDS);
            waitForPageLoad();
        }
        throw new SmartRuntimeException(String.format(
                "Playwright web element is not found by %s", by), exception);
    }

    /**
     * Returns page source.
     * @return The page source.
     */
    @Override
    public String getPageSource() {
        String pageSource = page.innerHTML("*");
        log.debug("getPageSource():]\n{}", pageSource);
        return pageSource;
    }

    /**
     * Closes the browser window.
     */
    @Override
    public void close() {
        browser.close();
        log.debug("Browser is closed.");
    }

    /**\
     * Quites the browser.
     */
    @Override
    public void quit() {
        if (browser != null) {
            browser.close();
            browser = null;
            log.debug("Browse quit.");
        }
    }

    /**
     * Returns window handlers.
     * @return The list of browser handlers.
     */
    @Override
    public Set<String> getWindowHandles() {
        ClassUtils.throwMethodNotImplementedException("WebDriver.getWindowHandles()");
        return null;
    }

    /**
     * Returns the window handle.
     * @return The window handle.
     */
    @Override
    public String getWindowHandle() {
        ClassUtils.throwMethodNotImplementedException("WebDriver.getWindowHandle()");
        return null;
    }

    /**
     * Switches to the browser window.
     * @return The target locator.
     */
    @Override
    public TargetLocator switchTo() {
        return new PlaywrightTargetLocator(playwrightPage);
    }

    /**
     * REturns navigation instance.
     * @return The navigation instance.
     */
    @Override
    public Navigation navigate() {
        return new PlaywrightNavigate(page);
    }

    /**
     * Returns options instance.
     * @return The options instance.
     */
    @Override
    public Options manage() {
        return new PlaywrightManage(page);
    }

    /**
     * Executes JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeScript(String script, Object... args) {
        int length = args.length;
        List<Object> params = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof PlaywrightElement) {
                params.add(((PlaywrightElement) arg).getLocator());
            } else {
                params.add(arg);
            }
        }

        if (length == 1 && params.get(0) instanceof Locator) {
            return ((Locator) params.get(0)).evaluate(script);
        }

        Object result = page.evaluate(script, params);
        log.debug("Executed JS:\n{}\nResult: '{}'", script, result);
        return result;
    }

    /**
     * Executes synchronous JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        Object result = executeScript(script, args);
        log.debug("Executed asynchronous JS:\n{}\nResult: '{}'", script, result);
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
        if (page == null || !isPageOpen) {
            return null;
        }
        try {
            byte[] data = page.screenshot(new Page.ScreenshotOptions().setType(ScreenshotType.JPEG));
            log.debug("Screenshot was taken.");
            return ScreenshotUtils.convertScreenshotBytes(target, data);
        }
        catch (Exception e) {
            if (e.getMessage().contains("Object doesn't exist:")) {
                // Workaround to fix Playwright issue.
                return null;
            }
            throw new RuntimeException(e);
        }
    }
}
