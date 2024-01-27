package org.example.driver.element;

import org.example.driver.WebDriverFactory;
import org.openqa.selenium.*;

import java.util.List;

public abstract class BaseElement implements WebElement, WrapsElement {
    private WebElement element = null;
    private final By by;

    public BaseElement(By by) {
        this.by = by;
    }

    @Override
    public WebElement getWrappedElement() {
        return getElement();
    }

    @Override
    public void click() {
        getElement().click();
    }

    @Override
    public void submit() {
        getElement().submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        getElement().sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        getElement().clear();
    }

    @Override
    public String getTagName() {
        return getElement().getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return getElement().getAttribute(name);
    }

    @Override
    public String getDomProperty(String name) {
        return getElement().getDomProperty(name);
    }

    @Override
    public String getDomAttribute(String name) {
        return getElement().getDomAttribute(name);
    }

    @Override
    public String getAriaRole() {
        return getElement().getAriaRole();
    }

    @Override
    public String getAccessibleName() {
        return getElement().getAccessibleName();
    }

    @Override
    public boolean isSelected() {
        return getElement().isSelected();
    }

    @Override
    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    @Override
    public String getText() {
        return getElement().getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return getElement().findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return getElement().findElement(by);
    }

    @Override
    public boolean isDisplayed() {
        return getElement().isDisplayed();
    }

    @Override
    public Point getLocation() {
        return getElement().getLocation();
    }

    @Override
    public Dimension getSize() {
        return getElement().getSize();
    }

    @Override
    public Rectangle getRect() {
        return getElement().getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return getElement().getCssValue(propertyName);
    }

    public SearchContext getShadowRoot() {
        return getElement().getShadowRoot();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return getElement().getScreenshotAs(target);
    }

    protected WebElement getElement() {
        if (element == null) {
            element = WebDriverFactory.getDriver().findElement(by);
            return element;
        }
        return element;
    }
}
