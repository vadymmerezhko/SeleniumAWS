package org.example.drivers.elements;

import org.example.drivers.playwright.PlaywrightElement;
import org.example.drivers.wrappers.RobustWebElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The color picker element class.
 */
public class ColorPicker extends BaseElement {

    /**
     * The color picker element constructor by its locator.
     * @param by The color picker locator.
     */
    public ColorPicker(By by) {
        super(by);
    }

    /**
     * Picks color value in format "#RRGGBB" like "#0088ff".
     * @param color The color value.
     */
    public void pickColor(String color) {
        DataValidationUtils.validateColorFormat(color, this.getClass().getSimpleName(), getElement());
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

    /**
     * Returns current color picker value in format "#RRGGBB" like "#0088ff".
     * @return The color value.
     */
    public String getPickedColor() {
        return getElement().getDomProperty("value");
    }
}
