package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class Dropdown extends BaseElement {

    public Dropdown(By by) {
        super(by);
    }

    public void selectOptionByText(String text) {
        Select select = new Select(getElement());
        select.selectByVisibleText(text);
    }

    public void selectOptionByValue(String value) {
        Select select = new Select(getElement());
        select.selectByValue(value);
    }

    public void selectOptionByIndex(int index) {
        Select select = new Select(getElement());
        select.selectByIndex(index);
    }

    public String getSelectedOptionText() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }
}
