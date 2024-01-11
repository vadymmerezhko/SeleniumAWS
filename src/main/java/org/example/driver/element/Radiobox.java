package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Radiobox extends BaseElement {

    public Radiobox(By by) {
        super(by);
    }

    public void select() {
        if (!getElement().isSelected()) {
            getElement().click();
        }
    }
}
