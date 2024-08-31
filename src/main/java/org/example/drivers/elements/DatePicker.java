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
 * The date picker element class.
 */
@Slf4j
public class DatePicker extends SmartElement {

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
    public void pickDate(SmartValue date) {
        DataValidationUtils.validateNotNull(date, "date");
        pickDate(date.toString());
    }

    /**
     * Picks the date in format "mm/DD/YYYY".
     * @param dateString The date to pick.
     */
    public void pickDate(String dateString) {
        DataValidationUtils.validateDateValue(dateString, "dateString");
        WebElement element = getElement();

        if (element instanceof PlaywrightElement) {
            ((PlaywrightElement)element).setValue(dateString);
            return;
        }
        element.sendKeys(dateString, Keys.ESCAPE);
        log.debug("Date picker {} value is picked: {}", elementName, dateString);
    }

    /**
     * Returns the picked date in format "mm/DD/YYYY".
     * @return The picked date.
     */
    public String getValue() {
        String dateString = getElement().getDomProperty("value");
        log.debug("Date picker {} value is returned: {}", elementName, dateString);
        return dateString;
    }
}
