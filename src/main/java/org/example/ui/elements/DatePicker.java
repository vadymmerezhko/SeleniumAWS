package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartLocalDate;
import org.example.data.SmartValue;
import org.example.ui.playwright.PlaywrightElement;
import org.example.ui.wrappers.SmartElement;
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
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
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

    /**
     * Returns the picked date smart value in format "mm/DD/YYYY".
     * @return The picked date smart value.
     */
    public SmartValue getSmartValue() {
        SmartValue smartValue = new SmartValue(getValue());
        log.debug("Date picker {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }

    /**
     * Returns the picked date smart value in format "mm/DD/YYYY".
     * @return The picked date smart value.
     */
    public SmartLocalDate getSmartLocalDate() {
        SmartLocalDate dateSmartValue = SmartLocalDate.fromString(getValue());
        log.debug("Date picker {} smart value is returned: {}", elementName, dateSmartValue);
        return dateSmartValue;
    }
}
