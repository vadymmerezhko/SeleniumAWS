package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.playwright.PlaywrightElement;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * The dropdown element class.
 */
@Slf4j
public class Dropdown extends SmartElement {

    /**
     * The dropdown constructor with auto selector.
     */
    public Dropdown() {
    }

    /**
     * The dropdown constructor by its selector.
     * @param by The element selector.
     */
    public Dropdown(By by) {
        super(by);
    }

    /**
     * Selects option by its text.
     * @param optionString The text of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOption(String optionString) {
        DataValidationUtils.validateNotBlank(optionString, "optionString");
        WebElement dropdown = getElement();

        try {
            if (dropdown instanceof PlaywrightElement) {
                click();
                ((PlaywrightElement) dropdown).selectOptionByText(optionString);
            }
            else {
                Select select = new Select(dropdown);
                select.selectByVisibleText(optionString);
            }
            log.debug("{} dropdown option is selected by option string: {}", elementName, optionString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot select %s option by option string: %s",
                    elementName, optionString), e);

        }
    }

    /**
     * Selects option by its text smart value.
     * @param optionSmartValue The text smart value of the option to select.
     */
    public void selectOption(SmartValue optionSmartValue) {
        DataValidationUtils.validateNotNull(optionSmartValue, "optionSmartValue");
        selectOption(optionSmartValue.toString());
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
     * @param valueString The value of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOptionByValue(String valueString) {
        DataValidationUtils.validateNotBlank(valueString, "valueString");
        WebElement dropdown = getElement();

        try {
            if (dropdown instanceof PlaywrightElement) {
                click();
                ((PlaywrightElement) dropdown).selectOptionByText(valueString);
            } else {
                Select select = new Select(dropdown);
                select.selectByValue(valueString);
            }
            log.debug("{} dropdown option is selected by value: {}", elementName, valueString);
        }
         catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot select %s option by value string: %s",
                    elementName, valueString), e);

        }
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
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOptionByIndex(int index) {
        try {
            Select select = new Select(getElement());
            select.selectByIndex(index);
            log.debug("{} dropdown option is selected by index: {}", elementName, index);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot select %s option by index: %d",
                    elementName, index), e);
        }
    }

    /**
     * Returns value of the selected option.
     * @return The value of the selected option.
     */
    public String getSelectedOptionValue() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getAttribute("value");
    }

    /**
     * Returns index of the selected option.
     * @return The index of the selected option.
     */
    public int getSelectedOptionIndex() {
        Select select = new Select(getElement());
        String selectedOption = getSelectedOption();
        List<WebElement> options = select.getOptions();
        int selectedIndex = -1;

        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getText().equals(selectedOption)) {
                selectedIndex = i;
                break;
            }
        }
        log.debug("{} dropdown selected index is returned: {}", elementName, selectedIndex);
        return selectedIndex;
    }

    /**
     * Returns text of the selected option.
     * @return The text of the selected option.
     */
    public String getSelectedOption() {
        Select select = new Select(getElement());
        return select.getFirstSelectedOption().getText();
    }

    /**
     * Returns the dropdown text selected option smart value.
     * @return The selected option text smart value.
     */
    public SmartValue getSmartValue() {
        SmartValue smartValue = new SmartValue(getSelectedOption());
        log.debug("{} dropdown selected option smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
