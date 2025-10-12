package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.annotations.RunAlone;
import com.smarte2e.data.SmartValue;
import com.smarte2e.interfaces.ReadableObject;
import com.smarte2e.interfaces.WritableObject;
import com.smarte2e.ui.playwright.PlaywrightElement;
import com.smarte2e.ui.wrappers.SmartWebElement;
import com.smarte2e.ui.wrappers.SmartElement;
import com.smarte2e.utils.ConvertUtils;
import com.smarte2e.utils.DataValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.awt.*;

import static com.smarte2e.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * The color picker element class.
 */
@Slf4j
public class ColorInput extends SmartElement implements ReadableObject, WritableObject {

    /**
     * The color picker element constructor with auto selector.
     */
    public ColorInput() {
    }

    /**
     * The color picker element constructor by its selector.
     * @param by The element selector.
     */
    public ColorInput(By by) {
        super(by);
    }

    /**
     * Picks color value in format "#RRGGBB" like "#0088ff".
     * @param value The color value.
     * @param <T> The value type.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "value");
        SmartValue smartValue = new SmartValue(value);

        String colorString = smartValue.toString();
        WebElement colorPicker = getElement();

        if (colorPicker instanceof PlaywrightElement) {
            ((PlaywrightElement)colorPicker).setValue(colorString);
        }
        else if (colorPicker instanceof SmartWebElement smartWebElement) {
            smartWebElement.setValue(colorString);
        }
        else {
            colorPicker.clear();
            colorPicker.sendKeys(colorString);
            synchro.waitForAttributeValue(colorPicker,"value",
                    colorString, WAIT_ELEMENT_TIMEOUT_SECONDS);

        }
        log.debug("{} color picker value is set to: {}", elementName, value);
    }

    /**
     * Returns current color picker value in format like "#0088ff".
     * @return The color value.
     */
    public Color getColor() {
        String colorString = getValueDomProperty();
        Color color = ConvertUtils.stringToColor(colorString);
        log.debug("Color picker {} value is returned: {}", elementName, color);
        return color;
    }

    /**
     * Returns the picked color smart value in format like "#0088ff".
     * @return The color smart value.
     */
    @Override
    public SmartValue getValue() {
        String colorString = getValueDomProperty();
        SmartValue smartValue = new SmartValue(colorString);
        log.debug("Color picker {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
