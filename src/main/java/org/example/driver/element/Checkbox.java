package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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

    public boolean isChecked() {
        return getElement().isSelected();
    }
}
