package org.example.drivers.elements;

import org.example.drivers.playwright.PlaywrightElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * The date picker element class.
 */
public class DatePicker extends BaseElement {

    /**
     * The date picker element constructor with auto selector.
     */
    public DatePicker() {
        super();
    }

    /**
     * The date picker element constructor by its selector.
     * @param by The element selector.
     */
    public DatePicker(By by) {
        super(by);
    }

    /**
     * Picks the date in format "mm/DD/YYYY".
     * @param date The date to pick.
     */
    public void pickDate(String date) {
        DataValidationUtils.validateDateValue(date, "date");
        WebElement element = getElement();

        if (element instanceof PlaywrightElement) {
            ((PlaywrightElement)element).setValue(date);
            return;
        }
        element.sendKeys(date, Keys.ESCAPE);
    }

    /**
     * Returns the picked date in format "mm/DD/YYYY".
     * @return The picked date.
     */
    public String getPickedDate() {
        return getElement().getDomProperty("value");
    }
}
