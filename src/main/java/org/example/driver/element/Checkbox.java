package org.example.driver.element;

import org.openqa.selenium.By;

public class Checkbox extends BaseElement {

    public Checkbox(By by) {
        super(by);
    }

    public void check() {
        if (!getElement().isSelected()) {
            getElement().click();
        }
    }

    public void uncheck() {
        if (getElement().isSelected()) {
            getElement().click();
        }
    }

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
