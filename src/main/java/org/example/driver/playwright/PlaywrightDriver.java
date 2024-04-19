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

public class PlaywrightDriver implements WebDriver, JavascriptExecutor, TakesScreenshot {
    private Browser browser;
    private PlaywrightPage playwrightPage;
    private Page page;
    private boolean accessibilityTestEnabled = false;

    public void setAccessibilityTestEnabled(boolean enabled) {
        accessibilityTestEnabled = enabled;
    }

    public PlaywrightDriver(Browser browser) {
        this.browser = browser;
    }

    public PlaywrightDriver(PlaywrightPage playwrightPage) {
        this.playwrightPage = playwrightPage;
        page = playwrightPage.getPage();
    }

    public void checkAccessibility() {
        if (accessibilityTestEnabled) {
            // Verify page accessibility.
            AxeResults accessibilityScanResults = new AxeBuilder(page).analyze();
            if (!accessibilityScanResults.getViolations().isEmpty()) {
                throw new RuntimeException("Accessibility issues:\n" + accessibilityScanResults.getViolations());
            }
        }
    }

    @Override
    public void get(String url) {
        if (page == null) {
            page = browser.newPage();
        }
        page.navigate(url);
        page.waitForLoadState();
        checkAccessibility();
    }

    @Override
    public String getCurrentUrl() {
        return page.url();
    }

    @Override
    public String getTitle() {
        return page.title();
    }

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

    @Override
    public String getPageSource() {
        return page.innerHTML("*");
    }

    @Override
    public void close() {
        browser.close();
    }

    @Override
    public void quit() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
    }

    @Override
    public Set<String> getWindowHandles() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public String getWindowHandle() {
        String methodName = this.getClass().getEnclosingMethod().getName();
        MethodManager.throwMethodNotImplementedException(methodName);
        return null;
    }

    @Override
    public TargetLocator switchTo() {
        return new PlaywrightTargetLocator(playwrightPage);
    }

    @Override
    public Navigation navigate() {
        return new PlaywrightNavigate(page);
    }

    @Override
    public Options manage() {
        return new PlaywrightManage(page);
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return page.evaluateHandle(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return page.evaluate(script, args);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        byte[] data = page.screenshot();
        return ScreenshotManager.convertScreenshotBytes(target, data);
    }
}
