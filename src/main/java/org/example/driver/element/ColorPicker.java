package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.example.driver.robust.RobustWebElement;
import org.example.utils.DataValidator;
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
        DataValidator.validateColorFormat(color, this.getClass().getSimpleName(), getElement());
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
