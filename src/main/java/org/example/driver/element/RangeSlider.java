package org.example.driver.element;

import org.openqa.selenium.By;

public class RangeSlider extends BaseElement {

    public RangeSlider(By by) {
        super(by);
    }

    public void setValue(int value) {
        getElement().sendKeys(String.valueOf(value));
    }

    public int getValue() {
        return Integer.parseInt(getElement().getAttribute("value"));
    }
}
