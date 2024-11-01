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

import static org.example.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * The radio button element class.
 */
@Slf4j
public class Radiobutton extends SmartElement implements ReadableObject, WritableObject {

    /**
     * The radio button element constructor with auto selector.
     */
    public Radiobutton() {
    }

    /**
     * The radio button element constructor by its selector.
     * @param by The element selector.
     */
    public Radiobutton(By by) {
        super(by);
    }

    /**
     * Selects the radio button.
     */
    @RunAlone
    public void select() {
        WebElement element = getElement();

        if (!element.isSelected()) {
            element.click();
            synchro.waitForCheckboxOrRadioButton(element, true, WAIT_ELEMENT_TIMEOUT_SECONDS);
        }
        log.debug("Radio button {} is selected.", elementName);
    }

    /**
     * Returns true - checked or false - unchecked checkbox value.
     * @return The true/false value.
     */
    @Override
    public boolean isSelected() {
        boolean value = getElement().isSelected();
        log.debug("{} radiobutton value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Gets radiobutton smart value.
     * @return The smart value.
     */
    @Override
    public SmartValue getValue() {
        return new SmartValue(isSelected());
    }

    /**
     * Sets radiobutton value.
     * @param value The value.
     * @param <T> The value type.
     */
    @Override
    public <T> void setValue(T value) {
        DataValidationUtils.validateNotNull(value, "value");
        SmartValue smartValue = new SmartValue(value);

        if (smartValue.toBoolean()) {
            select();
        }
    }
}
