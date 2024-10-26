package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.ui.playwright.PlaywrightElement;
import org.example.ui.wrappers.SmartWebElement;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.example.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * The color picker element class.
 */
@Slf4j
public class ColorPicker extends SmartElement {

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
     * @param color The color value.
     */
    public void pickColor(SmartValue color) {
        DataValidationUtils.validateNotNull(color, "color");
        pickColor(color.toString());
    }

    /**
     * Picks color value in format "#RRGGBB" like "#0088ff".
     * @param color The color value.
     */
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void pickColor(String color) {
        DataValidationUtils.validateColorFormat(color, "color");
        WebElement colorPicker = getElement();

        if (colorPicker instanceof PlaywrightElement) {
            ((PlaywrightElement)colorPicker).setValue(color);
        }
        else if (colorPicker instanceof SmartWebElement smartWebElement) {
            smartWebElement.setValue(color);
        }
        else {
            colorPicker.sendKeys(color);
            synchro.waitForAttributeValue(colorPicker,"value", color, WAIT_ELEMENT_TIMEOUT_SECONDS);

        }
        log.debug("Color picker {} value is picked: {}", elementName, color);
    }

    /**
     * Returns current color picker value in format like "#0088ff".
     * @return The color value.
     */
    public String getValue() {
        String color = getElement().getDomProperty("value");
        log.debug("Color picker {} value is returned: {}", elementName, color);
        return color;
    }

    /**
     * Returns the picked color smart value in format like "#0088ff".
     * @return The color smart value.
     */
    public SmartValue getSmartValue() {
        SmartValue smartValue = new SmartValue(getValue());
        log.debug("Color picker {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
