package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.data.SmartValue;
import org.openqa.selenium.By;

/**
 * The password element class.
 */
@Slf4j
public class PasswordInput extends SingleLineTextInput {

    /**
     * The password element constructor with auto selector.
     */
    public PasswordInput() {
    }

    /**
     * The password element constructor by its selector.
     * @param by The element selector.
     */
    public PasswordInput(By by) {
        super(by);
    }

    /**
     * Gets password smart value.
     * @return The password smart value.
     */
    @Override
    public SmartValue getValue() {
        String value = getValueDomProperty();
        // Do not show actual password value in the log
        log.debug("Password value is returned.");
        return new SmartValue(value);
    }
}
