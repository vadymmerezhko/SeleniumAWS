package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class Dropdown extends BaseElement {

    private final Select select;

    public Dropdown(By by) {
        super(by);
        select = new Select(getElement());
    }

    public void selectOptionText(String text) {
        select.selectByVisibleText(text);
    }

    public void selectOptionValue(String value) {
        select.selectByValue(value);
    }

    public void selectOptionIndex(int index) {
        select.selectByIndex(index);
    }

    public String getSelectedOption() {
        return select.getFirstSelectedOption().getText();
    }
}
