package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.drivers.playwright.PlaywrightElement;
import org.example.pages.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * The dropdown element class.
 */
@Slf4j
public class Dropdown extends SmartElement {

    /**
     * The dropdown constructor with auto selector.
     */
    public Dropdown() {
        super();
    }

    /**
     * The dropdown constructor by its selector.
     * @param by The element selector.
     */
    public Dropdown(By by) {
        super(by);
    }

    /**
     * SElects option by its text.
     * @param option The text of the option to select.
     */
    public void selectOption(SmartValue option) {
        WebElement dropdown = getElement();
        String optionString = option.toString();
        DataValidationUtils.validateNotBlank(optionString, this.getClass().getSimpleName());
        if (dropdown instanceof PlaywrightElement) {
            click();
            ((PlaywrightElement)dropdown).selectOptionByText(optionString);
            return;
        }
        Select select = new Select(dropdown);
        select.selectByVisibleText(optionString);
    }

    /**
     * Selects option by its value.
     * @param value The value of the option to select.
     */
    public void selectOptionByValue(SmartValue value) {
        DataValidationUtils.validateNotNull(value, "value");
        selectOptionByValue(value.toString());
    }

    /**
     * Selects option by its value.
     * @param optionString The value of the option to select.
     */
    public void selectOptionByValue(String optionString) {
        DataValidationUtils.validateNotBlank(optionString, "valueString");
        WebElement dropdown = getElement();

        if (dropdown instanceof PlaywrightElement) {
            click();
            ((PlaywrightElement)dropdown).selectOptionByText(optionString);
            return;
        }
        Select select = new Select(dropdown);
        select.selectByValue(optionString);
        log.debug("Dropdown {} option is selected: {}", elementName, optionString);
    }

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    public void selectOptionByIndex(SmartValue index) {
        DataValidationUtils.validateNotNull(index, "index");
        selectOptionByIndex(index.toInteger());
    }

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    public void selectOptionByIndex(int index) {
        Select select = new Select(getElement());
        select.selectByIndex(index);
        log.debug("Dropdown {} option is selected by index: {}", elementName, index);
    }

    /**
     * Returns text of the selected option.
     * @return The text of the selected option.
     */
    public String getValue() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }
}
