package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class RangeSlider extends BaseElement {

    public RangeSlider(By by) {
        super(by);
    }

    public void setValue(int value) {
        WebElement slider = getElement();
        if (slider instanceof PlaywrightElement) {
            ((PlaywrightElement)slider).setValue(Integer.toString(value));
            return;
        }

        int currentValue = getValue();
        if (value == currentValue) {
            return;
        }

        int increment = value > currentValue ? 1 : -1;
        Keys key =  value > currentValue ? Keys.RIGHT: Keys.LEFT;

        for (int i = currentValue; i != value; i += increment) {
            slider.sendKeys(key);
        }
    }

    public int getValue() {
        return Integer.parseInt(getElement().getDomProperty("value"));
    }
}
