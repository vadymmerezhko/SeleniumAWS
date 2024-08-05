package org.example.drivers.elements;

import org.openqa.selenium.By;

/**
 * The radio button element class.
 */
public class Radiobutton extends BaseElement {

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
    }

    /**
     * Returns true/false radio button value.
     * @return The radio button value.
     */
    public boolean getValue() {
        return isSelected();
    }
}
