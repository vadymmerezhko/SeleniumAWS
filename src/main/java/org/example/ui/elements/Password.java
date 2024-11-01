package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.openqa.selenium.By;

/**
 * The password element class.
 */
@Slf4j
public class Password extends SingleLineTextInput {

    /**
     * The password element constructor with auto selector.
     */
    public Password() {
    }

    /**
     * The password element constructor by its selector.
     * @param by The element selector.
     */
    public Password(By by) {
        super(by);
    }

    /**
     * Gets password smart value.
     * @return The password smart value.
     */
    @Override
    public SmartValue getValue() {
        String value = getElement().getAttribute("value");
        // Do not show actual password value in the log
        log.debug("Password value is returned.");
        return new SmartValue(value);
    }
}
