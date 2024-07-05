package org.example.drivers.wrappers;

import org.openqa.selenium.*;

public abstract class BaseWebElement implements WebElement {

    protected WebElement element;
    protected final By by;
    protected final WebDriver driver;

    public BaseWebElement(WebElement element,
                          By by,
                          WebDriver driver) {
        this.element = element;
        this.by = by;
        this.driver = driver;
    }

    abstract public String getStyle(String propertyName);

    abstract public void setStyle(String propertyName, String propertyValue);
}
