package org.example.driver.playwright;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.driver.by.ByParser;
import org.example.utils.MethodManager;
import org.example.utils.ScreenshotManager;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Playwright - Selenium WebDriver wrapper class.
 */
public class PlaywrightDriver implements WebDriver, JavascriptExecutor, TakesScreenshot {
    private Browser browser;
    private PlaywrightPage playwrightPage;
    private Page page;
    private boolean accessibilityTestEnabled = false;

    /**
     * Sets enabled accessibility test true/false flag.
     * @param enabled The enabled flag.
     */
    public void setAccessibilityTestEnabled(boolean enabled) {
        accessibilityTestEnabled = enabled;
    }

    /**
     * Playwright driver constructor by Playwright browser instance.
     * @param browser The browser instance.
     */
    public PlaywrightDriver(Browser browser) {
        this.browser = browser;
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
     * Checks page accessibility.
     * Throws runtime exception in case of accessibility issue(s).
     */
    public void checkAccessibility() {
        if (accessibilityTestEnabled) {
            // Verify page accessibility.
            AxeResults accessibilityScanResults = new AxeBuilder(page).analyze();
            if (!accessibilityScanResults.getViolations().isEmpty()) {
                throw new RuntimeException("Accessibility issues:\n" + accessibilityScanResults.getViolations());
            }
        }
    }

    /**
     * Opens browser page by its URL.
     * @param url The page URL.
     */
    @Override
    public void get(String url) {
        if (page == null) {
            page = browser.newPage();
        }
        page.navigate(url);
        page.waitForLoadState();
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
        String locatorString = ByParser.getLocatorString(by);
        List<Locator> locators;
        try {
            locators = page.locator(locatorString).all();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return locators.stream()
                .map(locator -> new PlaywrightElement(this, locator))
                .collect(Collectors.toList());
    }

    /**
     * Finds web element by its locator.
     * @param by The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By by) {
        String locatorString = ByParser.getLocatorString(by);
        try {
            Locator locator = page.locator(locatorString);
            return new PlaywrightElement(this, locator);
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
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    /**
     * Returns the window handle.
     * @return The window handle.
     */
    @Override
    public String getWindowHandle() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
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
        return page.evaluateHandle(script, args);
    }

    /**
     * Executes asynchronous JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return page.evaluate(script, args);
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
        byte[] data = page.screenshot();
        return ScreenshotManager.convertScreenshotBytes(target, data);
    }
}
