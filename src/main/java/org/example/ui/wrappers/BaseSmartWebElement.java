package org.example.ui.wrappers;

import org.openqa.selenium.*;


public abstract class BaseSmartWebElement implements WebElement {

    protected WebElement element;
    protected By by;
    protected final WebDriver driver;
    protected final WebSynchronizer synchro;

    public BaseSmartWebElement(WebElement element,
                               By by,
                               WebDriver driver) {
        this.element = element;
        this.by = by;
        this.driver = driver;
        synchro = new WebSynchronizer();
    }

    /**
     * Returns web element selector By.
     */
    public By getBy() {
        return by;
    }
}
