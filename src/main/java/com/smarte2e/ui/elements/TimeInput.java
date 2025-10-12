package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.annotations.RunAlone;
import com.smarte2e.data.SmartLocalTime;
import com.smarte2e.data.SmartValue;
import com.smarte2e.interfaces.ReadableObject;
import com.smarte2e.interfaces.WritableObject;
import com.smarte2e.ui.wrappers.SmartElement;
import com.smarte2e.utils.ConvertUtils;
import com.smarte2e.utils.DataValidator;
import org.openqa.selenium.By;


/**
 * The time input element class.
 */
@Slf4j
public class TimeInput extends SmartElement implements ReadableObject, WritableObject {

    /**
     * The time input element constructor with auto selector.
     */
    public TimeInput() {
    }

    /**
     * The time input element constructor by its selector.
     * @param by The element selector.
     */
    public TimeInput(By by) {
        super(by);
    }

    /**
     * Sets the time input value.
     * @param value The time to pick.
     * @param <T> The value type.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "value");
        SmartValue smartValue = new SmartValue(value);
        SmartLocalTime smartLocalTime = smartValue.toSmartLocalTime();
        String timeString = smartLocalTime.toString();
        clear();
        sendKeys(timeString);
        log.debug("{} time input value is set to: {}", elementName, value);
    }

    /**
     * Returns the smart local time value.
     * @return The picked date.
     */
    public SmartLocalTime getTime() {
        String valueString = getValueDomProperty();
        SmartLocalTime smartLocalTime = ConvertUtils.stringToSmartLocalTime(valueString);
        log.debug("Time input {} smart local time is returned: {}", elementName, smartLocalTime);
        return smartLocalTime;
    }

    /**
     * Returns the time input smart value in format.
     * @return The picked time smart value.
     */
    @Override
    public SmartValue getValue() {
        String valueString = getValueDomProperty();
        SmartValue smartValue = new SmartValue(valueString);
        log.debug("Date input {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
