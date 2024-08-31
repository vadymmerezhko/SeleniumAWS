package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.pages.SmartElement;
import org.openqa.selenium.By;

/**
 * The radio button element class.
 */
@Slf4j
public class Radiobutton extends SmartElement {

    /**
     * The radio button element constructor with auto selector.
     */
    public Radiobutton() {
        super();
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
    public void select() {
        if (!getElement().isSelected()) {
            getElement().click();
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
