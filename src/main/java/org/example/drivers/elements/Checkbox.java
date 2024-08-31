package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.pages.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

/**
 * The checkbox element class.
 */
@Slf4j
public class Checkbox extends SmartElement {

    /**
     * The checkbox element constructor with auto selector.
     */
    public Checkbox() {
        super();
    }

    /**
     * The checkbox element constructor by its selector.
     * @param by The element selector.
     */
    public Checkbox(By by) {
        super(by);
    }

    /**
     * Checks (sets true value) the checkbox.
     */
    public void check() {
        if (!getElement().isSelected()) {
            getElement().click();
            log.debug("Checkbox {} is checked.", elementName);
        }
    }

    /**
     * Unchecks (sets false value) the checkbox.
     */
    public void uncheck() {
        if (getElement().isSelected()) {
            getElement().click();
            log.debug("Checkbox {} is unchecked.", elementName);
        }
    }

    /**
     * Sets the checkbox value.
     * @param value The teu/false value to set.
     */
    public void setValue(SmartValue value) {
        DataValidationUtils.validateNotNull(value, "value");
        setValue(value.toBoolean());
    }

    /**
     * Sets the checkbox value.
     * @param value The teu/false value to set.
     */
    public void setValue(boolean value) {
        if (value) {
            check();
        } else {
            uncheck();
        }
        log.debug("Checkbox {} value is set to: {}", elementName, value);
    }

    /**
     * Returns true if checkbox is checked.
     * Or false otherwise.
     * @return The true/false flag.
     */
    public boolean isChecked() {
        boolean value = getDomProperty("value").equals("true");
        log.debug("Checkbox {} value is returned: {}", elementName, value);
        return value;
    }
}
