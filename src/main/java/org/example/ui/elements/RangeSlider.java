package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.ui.playwright.PlaywrightElement;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.ConvertUtils;
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
     * The range slider constructor.
     */
    public RangeSlider() {
    }

    /**
     * The range slider constructor by its selector.
     * @param by The element selector.
     */
    public RangeSlider(By by) {
        super(by);
    }

    /**
     * Sets range value from min to max.
     * The value can have any Double or Long numeric value
     * or numeric string.
     * @param value The range smart value.
     */
    public void setValue(SmartValue value) {
        DataValidationUtils.validateNotNull(value, "value");
        setValue(value.toDouble());
    }

    /**
     * Sets range value from 0 to 10.
     * The value can have any Double or Long numeric value.
     * @param value The range number value.
     */
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void setValue(Number value) {
        WebElement slider = getElement();

        if (slider instanceof PlaywrightElement) {
            ((PlaywrightElement)slider).setValue(String.valueOf(value));
            return;
        }
        Number currentValue = getValue();
        double dif = value.doubleValue() - currentValue.doubleValue();
        int compareResult = Double.compare(value.doubleValue(), currentValue.doubleValue());

        if (dif == 0.0) {
            return;
        }
        double increment = 0.0;
        String minString = getAttribute("min");
        String maxString = getAttribute("max");
        double min = minString != null && !minString.isEmpty() ? Double.parseDouble(minString) : 0.0;
        double max = maxString != null && !maxString.isEmpty() ? Double.parseDouble(maxString) : 100.0;

        DataValidationUtils.validateMin(value, min, "min");
        DataValidationUtils.validateMax(value, max, "max");
        Keys key =  dif > 0.0 ? Keys.RIGHT: Keys.LEFT;

        for (double i = currentValue.doubleValue();
             Double.compare(value.doubleValue(), i) == compareResult;
             i += increment) {
            slider.sendKeys(key);

            if (increment == 0.0) {
                increment = getValue().doubleValue() - currentValue.doubleValue();
            }
        }
        log.debug("Range slider {} value is set to: {}", elementName, value);
    }

    /**
     * Returns the current range number value.
     * @return The range number value.
     * The value can have any Double or Long numeric value.
     */
    public Number getValue() {
        String valueString = getDomProperty("value");
        Number value = ConvertUtils.stringToNumber(valueString);
        log.debug("Range slider {} value is returned: {}", elementName, value);
        return value;
    }
}
