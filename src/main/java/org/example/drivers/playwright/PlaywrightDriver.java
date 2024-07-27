package org.example.drivers.playwright;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import org.example.drivers.selectors.SmartByParser;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ClassUtils;
import org.example.utils.ScreenshotUtils;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Playwright - Selenium WebDriver wrapper class.
 */
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
        }
    }

    /**
     * Opens browser page by its URL.
     * @param url The page URL.
     */
    @Override
    public void get(String url) {
        page.navigate(url);
        page.waitForLoadState();
        isPageOpen = true;
        checkAccessibility();
    }

    /**
     * Returns the current URL.
     * @return The current URL.
     */
    @Override
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Returns the page title.
     * @return The page title.
     */
    @Override
    public String getTitle() {
        return page.title();
    }

    /**
     * Finds web elements by its locator.
     * @param by The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        String locatorString = SmartByParser.getLocatorString(by);
        List<Locator> locators;
        try {
            locators = page.locator(locatorString).all();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return locators.stream()
                .map(locator -> new PlaywrightElement(by, locator, this))
                .collect(Collectors.toList());
    }

    /**
     * Finds web element by its locator.
     * @param by The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By by) {
        String locatorString = SmartByParser.getLocatorString(by);
        try {
            Locator locator = page.locator(locatorString);
            return new PlaywrightElement(by, locator,this);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns page source.
     * @return The page source.
     */
    @Override
    public String getPageSource() {
        return page.innerHTML("*");
    }

    /**
     * Closes the browser window.
     */
    @Override
    public void close() {
        browser.close();
    }

    /**\
     * Quites the browser.
     */
    @Override
    public void quit() {
        if (browser != null) {
            browser.close();
            browser = null;
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

        return page.evaluate(script, params);
    }

    /**
     * Executes synchronous JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return executeScript(script, args);
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
