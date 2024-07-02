package org.example.drivers.elements;

import org.openqa.selenium.By;

/**
 * The checkbox element class.
 */
public class Checkbox extends BaseElement {

    /**
     * The checkbox element constructor by its locator.
     * @param by The checkbox locator.
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
        }
    }

    /**
     * Unchecks (sets false value) the checkbox.
     */
    public void uncheck() {
        if (getElement().isSelected()) {
            getElement().click();
        }
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
    }

    public boolean isChecked() {
        return getDomProperty("value").equals("true");
    }
}
