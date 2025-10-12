package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.annotations.RunAlone;
import com.smarte2e.data.SmartLocalDate;
import com.smarte2e.data.SmartValue;
import com.smarte2e.interfaces.ReadableObject;
import com.smarte2e.interfaces.WritableObject;
import com.smarte2e.ui.wrappers.SmartElement;
import com.smarte2e.utils.ConvertUtils;
import com.smarte2e.utils.DataValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;


/**
 * The date input element class.
 */
@Slf4j
public class DateInput extends SmartElement implements ReadableObject, WritableObject {

    /**
     * The date input element constructor with auto selector.
     */
    public DateInput() {
    }

    /**
     * The date input element constructor by its selector.
     * @param by The element selector.
     */
    public DateInput(By by) {
        super(by);
    }

    /**
     * Sets the date input value
     * @param value The value to set.
     * @param <T> The value type.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "value");
        SmartValue smartValue = new SmartValue(value);
        SmartLocalDate smartLocalDate = smartValue.toSmartLocalDate();
        String dateString = smartLocalDate.toString();
        clear();
        sendKeys(dateString);
        // Closes date input dropdown pop-up by pressing TAB button to change focus
        sendKeys(Keys.TAB);
        log.debug("{} date input value is set to: {}", elementName, value);
    }

    /**
     * Returns the date input smart local date value.
     * @return The smart local date value.
     */
    public SmartLocalDate getDate() {
        String valueString = getValueDomProperty();
        SmartLocalDate smartLocalDate = ConvertUtils.stringToSmartLocalDate(valueString);
        log.debug("Date input {} smart local date is returned: {}", elementName, smartLocalDate);
        return smartLocalDate;
    }

    /**
     * Returns the date input smart value.
     * @return The smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartLocalDate localDate = getDate();
        SmartValue smartValue = new SmartValue(localDate);
        log.debug("Date input {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
