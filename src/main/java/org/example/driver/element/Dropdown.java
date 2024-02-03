package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class Dropdown extends BaseElement {

    public Dropdown(By by) {
        super(by);
    }

    public void selectOptionByText(String text) {
        WebElement dropdown = getElement();
        if (dropdown instanceof PlaywrightElement) {
            click();
            ((PlaywrightElement)dropdown).selectOptionByText(text);
            return;
        }
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    public void selectOptionByValue(String value) {
        WebElement dropdown = getElement();
        if (dropdown instanceof PlaywrightElement) {
            click();
            ((PlaywrightElement)dropdown).selectOptionByText(value);
            return;
        }
        Select select = new Select(dropdown);
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
