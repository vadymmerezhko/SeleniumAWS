package org.example.driver.robust;

import org.example.driver.waiter.RobustWebDriverWaiter;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The robust WebDriver wrapper class.
 * This class wapps WebDriver, adds auto wait and retry on error
 * to make WebDriver more reliable.
 */
public class RobustWebDriver implements WebDriver, JavascriptExecutor, TakesScreenshot {
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
    }

    /**
     * Opens browser page by its URL.
     * @param url The page URL.
     */
    @Override
    public void get(String url) {
        driver.get(url);
        waiter.waitForPageLoad(PAGE_LOAD_TIMEOUT_SEC);
    }

    /**
     * Returns current page URL.
     * @return The current page URL.
     */
    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Returns the page title.
     * @return The page title.
     */
    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Finds web elements by element locator.
     * @param by The element locator.
     * @return The list of found web elements.
     */
    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = driver.findElements(by);
        return elements.stream().map(element ->
                new RobustWebElement(element, null, by, driver, waiter))
                .collect(Collectors.toList());
    }

    /**
     * Finds web element by its locator.
     * @param by The element locator.
     * @return The found web element.
     */
    @Override
    public WebElement findElement(By by) {
        try {
            return new RobustWebElement(
                    driver.findElement(by), null, by, driver, waiter);
        }
        catch (NoSuchElementException e) {
            return new RobustWebElement(
                    driver.findElement(by), null, by, driver, waiter);
        }
    }

    /**
     * Returns the page source.
     * @return The page source.
     */
    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    /**
     * Closes the browser window.
     */
    @Override
    public void close() {
        driver.close();
    }

    /**
     * Quits the browser.
     */
    @Override
    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Returns the browser window handles.
     * @return The set of window handles.
     */
    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    /**
     * Returns the browser window handle.
     * @return The window handle.
     */
    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    /**
     * Switches to browser window.
     * @return The target locator.
     */
    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    /**
     * Returns Navigate instance.
     * @return The Navigate instance.
     */
    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    /**
     * Returns Manage instance.
     * @return The Manage instance.
     */
    @Override
    public Options manage() {
        return driver.manage();
    }

    /**
     * Executes JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor)driver).executeScript(script, args);
    }

    /**
     * Executes asynchronous JavaScript code.
     * @param script The JavaScript code.
     * @param args The list of arguments.
     * @return The JavaScript return object.
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return ((JavascriptExecutor)driver).executeAsyncScript(script, args);
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
        return ((TakesScreenshot)driver).getScreenshotAs(target);
    }
}
