package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.openqa.selenium.By;

/**
 * The radio button element class.
 */
public class Radiobutton extends BaseElement {

    /**
     * The radio button element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public Radiobutton(BasePage page, By by) {
        super(page, by);
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
