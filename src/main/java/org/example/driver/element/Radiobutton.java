package org.example.driver.element;

import org.openqa.selenium.By;

public class Radiobutton extends BaseElement {

    public Radiobutton(By by) {
        super(by);
    }

    public void select() {
        if (!getElement().isSelected()) {
            getElement().click();
        }
    }

    public boolean getValue() {
        return isSelected();
    }
}
