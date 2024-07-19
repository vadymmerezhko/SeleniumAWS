package org.example.drivers.wrappers;

import org.openqa.selenium.*;


public abstract class BaseWebElement implements WebElement {

    protected WebElement element;
    protected By by;
    protected final WebDriver driver;

    public BaseWebElement(WebElement element,
                          By by,
                          WebDriver driver) {
        this.element = element;
        this.by = by;
        this.driver = driver;
    }

    /**
     * Returns web element selector By.
     */
    public By getBy() {
        return by;
    }
}
