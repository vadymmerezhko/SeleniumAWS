package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartLocalDate;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.interfaces.WritableObject;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.ConvertUtils;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


/**
 * The date picker element class.
 */
@Slf4j
public class DatePicker extends SmartElement implements ReadableObject, WritableObject {

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
     * Picks the smart value date in format "mm/DD/YYYY".
     * @param date The date to pick.
     */
    public void setSmartValue(SmartValue date) {
        DataValidationUtils.validateNotNull(date, "date");
        setValue(date.toSmartLocalDate());
    }

    /**
     * Picks the date in format "mm/DD/YYYY".
     * @param value The date to pick.
     * @param <T> The value type.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public <T> void setValue(T value) {
        DataValidationUtils.validateNotNull(value, "dateString");
        SmartValue smartValue = new SmartValue(value);
        SmartLocalDate smartLocalDate = smartValue.toSmartLocalDate();
        String dateString = smartLocalDate.toString();
        WebElement datePickerInput = getElement();
        datePickerInput.clear();
        datePickerInput.sendKeys(dateString);
        // Clos date picker dropdown pop-up by pressing TAB button to change focus
        datePickerInput.sendKeys(Keys.TAB);
        log.debug("{} date picker value is set to: {}", elementName, value);
    }

    /**
     * Returns the picked local date object.
     * @return The picked date.
     */
    public SmartLocalDate getSmartLocalDate() {
        String valueSting = getElement().getAttribute("value");
        SmartLocalDate smartLocalDate = ConvertUtils.stringToSmartLocalDate(valueSting);
        log.debug("Date picker {} smart local date is returned: {}", elementName, smartLocalDate);
        return smartLocalDate;
    }

    /**
     * Returns the picked date smart value in format "mm/DD/YYYY".
     * @return The picked date smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(getSmartLocalDate());
        log.debug("Date picker {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
