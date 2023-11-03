package org.example.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RobustWebDriver implements WebDriver {
    private static final int PAGE_LOAD_TIMEOUT_SEC = 15;
    private static final int FIND_ELEMENT_TIMEOUT_SEC = 5;

    private final WebDriver driver;
    private final RobustWebDriverWaiter waiter;

    public RobustWebDriver(WebDriver driver) {
        this.driver = driver;
        waiter = new RobustWebDriverWaiter(driver);
    }

    @Override
    public void get(String url) {
        driver.get(url);
        waiter.waitForPageLoad(PAGE_LOAD_TIMEOUT_SEC);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = driver.findElements(by);
        return elements.stream().map(element ->
                new RobustWebElement(element, null, by, driver))
                .collect(Collectors.toList());
    }

    @Override
    public WebElement findElement(By by) {
        return new RobustWebElement(
                waiter.waitForElementPresenceBy(by, FIND_ELEMENT_TIMEOUT_SEC), null, by, driver);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }
}
