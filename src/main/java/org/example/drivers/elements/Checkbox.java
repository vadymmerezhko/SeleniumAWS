package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.openqa.selenium.By;

/**
 * The checkbox element class.
 */
public class Checkbox extends BaseElement {

    /**
     * The checkbox element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public Checkbox(BasePage page, By by) {
        super(page, by);
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
