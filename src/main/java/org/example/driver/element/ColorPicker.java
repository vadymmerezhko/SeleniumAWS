package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.example.driver.robust.RobustWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ColorPicker extends BaseElement {
    public ColorPicker(By by) {
        super(by);
    }

    public void pickColor(String color) {
        WebElement colorPicker = getElement();
        if (colorPicker instanceof PlaywrightElement) {
            ((PlaywrightElement)colorPicker).setValue(color);
            return;
        }
        else if (colorPicker instanceof RobustWebElement) {
            ((RobustWebElement)colorPicker).setValue(color);
            return;
        }
        colorPicker.sendKeys(color);
    }

    public String gwtPickedColor() {
        return getElement().getDomProperty("value");
    }
}
