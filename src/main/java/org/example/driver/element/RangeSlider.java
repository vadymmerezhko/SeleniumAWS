package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * The range slider element class.
 */
public class RangeSlider extends BaseElement {

    /**
     * The range slider constructor by its locator.
     * @param by The range slider locator.
     */
    public RangeSlider(By by) {
        super(by);
    }

    /**
     * Sets range value.
     * @param value The range value.
     */
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

    /**
     * Returns the current range value.
     * @return The range value.
     */
    public int getValue() {
        return Integer.parseInt(getElement().getDomProperty("value"));
    }
}
