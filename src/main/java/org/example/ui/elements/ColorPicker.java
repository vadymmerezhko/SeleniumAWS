package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.interfaces.WritableObject;
import org.example.ui.playwright.PlaywrightElement;
import org.example.ui.wrappers.SmartWebElement;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.ConvertUtils;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.awt.*;

import static org.example.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * The color picker element class.
 */
@Slf4j
public class ColorPicker extends SmartElement implements ReadableObject, WritableObject {

    /**
     * The color picker element constructor with auto selector.
     */
    public ColorPicker() {
    }

    /**
     * The color picker element constructor by its selector.
     * @param by The element selector.
     */
    public ColorPicker(By by) {
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
        DataValidationUtils.validateNotNull(value, "color");
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
        String colorString = getElement().getDomProperty("value");
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
        SmartValue smartValue = new SmartValue(getColor());
        log.debug("Color picker {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
