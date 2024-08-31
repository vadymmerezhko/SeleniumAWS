package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.drivers.playwright.PlaywrightElement;
import org.example.pages.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * The range slider element class.
 */
@Slf4j
public class RangeSlider extends SmartElement {

    /**
     * The range slider constructor by itsselector.
     */
    public RangeSlider() {
        super();
    }

    /**
     * The range slider constructor by itsselector.
     * @param by The element selector.
     */
    public RangeSlider(By by) {
        super(by);
    }

    /**
     * Sets range value from 0 to 10.
     * @param value The range value.
     */
    public void setValue(SmartValue value) {
        DataValidationUtils.validateNotNull(value, "value");
        setValue(value.toInteger());
    }

    /**
     * Sets range value from 0 to 10.
     * @param value The range value.
     */
    public void setValue(int value) {
        WebElement slider = getElement();

        if (slider instanceof PlaywrightElement) {
            ((PlaywrightElement)slider).setValue(String.valueOf(value));
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
        log.debug("Range slider {} value is set to: {}", elementName, value);
    }

    /**
     * Returns the current range value.
     * @return The range value.
     */
    public int getValue() {
        int value = Integer.parseInt(getElement().getDomProperty("value"));
        log.debug("Range slider {} value is returned: {}", elementName, value);
        return value;
    }
}
