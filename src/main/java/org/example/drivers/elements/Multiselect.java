package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.pages.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The multiselect element class.
 */
@Slf4j
public class Multiselect extends SmartElement {
    private final Select select;

    /**
     * The multiselect element constructor with auto selector.
     */
    public Multiselect() {
        super();
        select = new Select(getElement());
    }

    /**
     * The multiselect element constructor by its selector.
     * @param by The element selector.
     */
    public Multiselect(By by) {
        super(by);
        select = new Select(getElement());
    }

    /**
     * Selects option by its text.
     * @param option The option of the option to select.
     */
    public void selectOption(SmartValue option) {
        DataValidationUtils.validateNotNull(option, "option");
        selectOption(option.toString());
    }

    /**
     * Selects option by its text.
     * @param optionString The option text of the option to select.
     */
    public void selectOption(String optionString) {
        DataValidationUtils.validateNotBlank(optionString, "option");
        select.selectByVisibleText(optionString);
        log.debug("Multiselect {} option is selected: {}", elementName, optionString);
    }

    /**
     * Deselects option by its text.
     * @param option The text of the option to deselect.
     */
    public void deselectOption(SmartValue option) {
        DataValidationUtils.validateNotNull(option, "option");
        deselectOption(option.toString());
    }

    /**
     * Deselects option by its text.
     * @param optionString The text of the option to deselect.
     */
    public void deselectOption(String  optionString) {
        DataValidationUtils.validateNotBlank(optionString, "option");
        select.deselectByVisibleText(optionString);
        log.debug("Multiselect {} option is deselected: {}", elementName, optionString);
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
    public void selectOptionByValue(String valueString) {
        DataValidationUtils.validateNotBlank(valueString, "value");
        select.selectByValue(valueString);
        log.debug("Multiselect {} option is selected by value: {}", elementName, valueString);
    }

    /**
     * Deelects option by its value.
     * @param value The value of the option to deselect.
     */
    public void deselectOptionByValue(SmartValue  value) {
        DataValidationUtils.validateNotNull(value, "value");
        deselectOptionByValue(value.toString());
    }

    /**
     * Deelects option by its value.
     * @param valueString The value of the option to deselect.
     */
    public void deselectOptionByValue(String  valueString) {
        DataValidationUtils.validateNotBlank(valueString, "value");
        select.deselectByValue(valueString);
        log.debug("Multiselect {} option is deselected by value: {}", elementName, valueString);
    }

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    public void selectOptionByIndex(SmartValue index) {
        DataValidationUtils.validateNotNull(index, "index");
        select.selectByIndex(index.toInteger());
        log.debug("Multiselect {} option is selected by index: {}", elementName, index);
    }

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    public void selectOptionByIndex(int index) {
        select.selectByIndex(index);
    }

    /**
     * Deselects option by its index.
     * @param index The index of the option to deselect.
     */
    public void deselectOptionByIndex(SmartValue index) {
        DataValidationUtils.validateNotNull(index, "index");
        select.deselectByIndex(index.toInteger());
    }

    /**
     * Deselects option by its index.
     * @param index The index of the option to deselect.
     */
    public void deselectOptionByIndex(int index) {
        select.deselectByIndex(index);
        log.debug("Multiselect {} option is deselected by index: {}", elementName, index);
    }

    /**
     * Returns the firs selected option text.
     * @return The text of the first selected option.
     */
    public String getFirstSelectedOptionText() {
        String text = select.getFirstSelectedOption().getText();
        log.debug("Multiselect {} first selected option text is returned: {}", elementName, text);
        return text;
    }

    /**
     * Returns the firs selected option value.
     * @return The value of the first selected option.
     */
    public String getFirstSelectedOptionValue() {
        String value = select.getFirstSelectedOption().getDomProperty("value");
        log.debug("Multiselect {} first selected option value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Returns text list of selected options.
     * @return The text list of the selected options.
     */
    public List<String> getAlSelectedOptions() {
        List<String> options = select.getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        log.debug("All multiselect {} selected option text values are returned:\n{}", elementName, options);
        return options;
    }

    /**
     * Returns value list of selected values.
     * @return The value list of the selected values.
     */
    public List<String> getAlSelectedValues() {
        List<String> values = select.getAllSelectedOptions().stream()
                .map(option -> option.getAttribute("value"))
                .collect(Collectors.toList());
        log.debug("All multiselect {} selected option values are returned:\n{}", elementName, values);
        return values;
    }
}
