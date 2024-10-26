package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.example.constants.Settings.*;

/**
 * The checkbox element class.
 */
@Slf4j
public class Checkbox extends SmartElement {

    /**
     * The checkbox element constructor with auto selector.
     */
    public Checkbox() {
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
    @RunAlone
    public void check() {
        WebElement element = getElement();

        if (!element.isSelected()) {
            element.click();
            synchro.waitForCheckboxOrRadioButton(element, true, WAIT_ELEMENT_TIMEOUT_SECONDS);
            log.debug("Checkbox {} is checked.", elementName);
        }
    }

    /**
     * Unchecks (sets false value) the checkbox.
     */
    @RunAlone
    public void uncheck() {
        WebElement element = getElement();

        if (element.isSelected()) {
            element.click();
            synchro.waitForCheckboxOrRadioButton(element, false, WAIT_ELEMENT_TIMEOUT_SECONDS);
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
     * Returns true - checked or false - unchecked checkbox value.
     * @return The true/false value.
     */
    public boolean getValue() {
        boolean value = getElement().isSelected();
        log.debug("Checkbox {} value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Returns the checkbox boolean smart value.
     * @return The boolean smart value.
     */
    public SmartValue getSmartValue() {
        SmartValue smartValue = new SmartValue(getValue());
        log.debug("Checkbox {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
