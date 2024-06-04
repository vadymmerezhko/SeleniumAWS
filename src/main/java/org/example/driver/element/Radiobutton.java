package org.example.driver.element;

import org.openqa.selenium.By;

/**
 * The radio button element class.
 */
public class Radiobutton extends BaseElement {

    /**
     * The radio button element constructor.
     * @param by The radio button locator.
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
