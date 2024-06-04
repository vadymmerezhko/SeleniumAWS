package org.example.driver.element;

import org.example.driver.playwright.PlaywrightElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * The dropdown element class.
 */
public class Dropdown extends BaseElement {

    /**
     * The dropdown constructor by its locator.
     * @param by The dropdown locator.
     */
    public Dropdown(By by) {
        super(by);
    }

    /**
     * SElects option by its text.
     * @param text The text of the option to select.
     */
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

    /**
     * Selects option by its value.
     * @param value The value of the option to select.
     */
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

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    public void selectOptionByIndex(int index) {
        Select select = new Select(getElement());
        select.selectByIndex(index);
    }

    /**
     * Returns text of the selected option.
     * @return The text of the selected option.
     */
    public String getSelectedOptionText() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }
}
