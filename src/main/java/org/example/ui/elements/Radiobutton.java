package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.ui.wrappers.SmartElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.example.constants.Settings.WAIT_ELEMENT_TIMEOUT_SECONDS;

/**
 * The radio button element class.
 */
@Slf4j
public class Radiobutton extends SmartElement {

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
     * Returns true/false radio button value.
     * @return The radio button value.
     */
    public boolean getValue() {
        boolean value = isSelected();
        log.debug("Radio button {} value is returned: {}", elementName, value);
        return value;
    }
}
