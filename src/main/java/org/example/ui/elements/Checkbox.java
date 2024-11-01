package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.interfaces.WritableObject;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.example.constants.Settings.*;

/**
 * The checkbox element class.
 */
@Slf4j
public class Checkbox extends SmartElement implements ReadableObject, WritableObject {

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
     * Selects (sets true value) the checkbox.
     */
    @RunAlone
    public void select() {
        WebElement element = getElement();

        if (!element.isSelected()) {
            element.click();
            synchro.waitForCheckboxOrRadioButton(element, true, WAIT_ELEMENT_TIMEOUT_SECONDS);
            log.debug("Checkbox {} is checked.", elementName);
        }
    }

    /**
     * Unselects (sets false value) the checkbox.
     */
    @RunAlone
    public void unselect() {
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
     * @param <T> The value type.
     */
    @Override
    public <T> void setValue(T value) {
        DataValidationUtils.validateNotNull(value, "value");
        SmartValue smartValue = new SmartValue(value);

        if (smartValue.toBoolean()) {
            select();
        }
        else {
            unselect();
        }
        log.debug("{} checkbox value is set to: {}", elementName, value);
    }

    /**
     * Returns true - checked or false - unchecked checkbox value.
     * @return The true/false value.
     */
    @Override
    public boolean isSelected() {
        boolean value = getElement().isSelected();
        log.debug("Checkbox {} value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Returns the checkbox boolean smart value.
     * @return The boolean smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(isSelected());
        log.debug("Checkbox {} smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
