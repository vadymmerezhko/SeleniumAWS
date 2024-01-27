package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ColorPicker extends BaseElement {
    public ColorPicker(By by) {
        super(by);
    }

    public void pickColor(String color) {
        WebElement element = getElement();
        element.sendKeys(color);
    }

    public String gwtPickedColor() {
        return getElement().getAttribute("value");
    }
}
