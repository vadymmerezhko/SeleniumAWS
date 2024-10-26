package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.wrappers.SmartElement;
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
    private Select select;

    /**
     * The multiselect element constructor with auto selector.
     */
    public Multiselect() {
    }

    /**
     * The multiselect element constructor by its selector.
     * @param by The element selector.
     */
    public Multiselect(By by) {
        super(by);
    }

    @Override
    protected WebElement getElement() {
        try {
            select = new Select(super.getElement());
            return select.getWrappedElement();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get multiselect web elemnt by:
                    %s
                    """.stripIndent(),
                    smartBy), e);
        }
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
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOption(String optionString) {
        DataValidationUtils.validateNotBlank(optionString, "option");
        getElement();
        try {
            select.selectByVisibleText(optionString);
            log.debug("Multiselect {} option is selected: {}", elementName, optionString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot select multiselect option.
                    Multiselect:
                    %s
                    Option: %s
                    """.stripIndent(),
                    smartBy, optionString), e);
        }
    }

    /**
     * Deselects option by its text.
     * @param option The text of the option to deselect.
     */
    public void deselectOption(SmartValue option) {
        DataValidationUtils.validateNotNull(option, "option");
        DataValidationUtils.validateNotBlank(option.toString(), "option");
        getElement();
        deselectOption(option.toString());
    }

    /**
     * Deselects option by its text.
     * @param optionString The text of the option to deselect.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOption(String  optionString) {
        DataValidationUtils.validateNotBlank(optionString, "option");
        getElement();
        try {
            select.deselectByVisibleText(optionString);
            log.debug("Multiselect {} option is deselected: {}", elementName, optionString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
               Cannot deselect multiselect option.
               Multiselect:
               %s
               Option: %s
               """.stripIndent(),
               smartBy, optionString), e);
        }
    }

    /**
     * Selects option by its value.
     * @param value The value of the option to select.
     */
    public void selectOptionByValue(SmartValue value) {
        DataValidationUtils.validateNotNull(value, "value");
        getElement();
        selectOptionByValue(value.toString());
    }

    /**
     * Selects option by its value.
     * @param valueString The value of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOptionByValue(String valueString) {
        DataValidationUtils.validateNotBlank(valueString, "value");
        getElement();
        try {
            select.selectByValue(valueString);
            log.debug("Multiselect {} option is selected by value: {}", elementName, valueString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
               Cannot select multiselect option by value.
               Multiselect:
               %s
               Value: %s
               """.stripIndent(),
               smartBy, valueString), e);
        }
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
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptionByValue(String  valueString) {
        DataValidationUtils.validateNotBlank(valueString, "value");
        getElement();
        try {
            select.deselectByValue(valueString);
            log.debug("Multiselect {} option is deselected by value: {}", elementName, valueString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
               Cannot deselect multiselect option by value.
               Multiselect:
               %s
               Value: %s
               """.stripIndent(),
               smartBy, valueString), e);
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
        DataValidationUtils.validateMin(index, 0, "index");
        getElement();
        try {
            select.selectByIndex(index);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
               Cannot select multiselect option by index.
               Multiselect:
               %s
               Index: %s
               """.stripIndent(),
               smartBy, index), e);
        }
    }

    /**
     * Deselects option by its index.
     * @param index The index of the option to deselect.
     */
    public void deselectOptionByIndex(SmartValue index) {
        DataValidationUtils.validateNotNull(index, "index");
        DataValidationUtils.validateMin(index.toInteger(), 0, "index");
        deselectOptionByIndex(index.toInteger());
    }

    /**
     * Deselects option by its index.
     * @param index The index of the option to deselect.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptionByIndex(int index) {
        DataValidationUtils.validateMin(index, 0, "index");
        getElement();
        try {
            select.deselectByIndex(index);
            log.debug("Multiselect {} option is deselected by index: {}", elementName, index);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
               Cannot deselect multiselect option by index.
               Multiselect:
               %s
               Index: %s
               """.stripIndent(),
               smartBy, index), e);
        }
    }

    /**
     * Returns the firs selected option text.
     * @return The text of the first selected option.
     */
    public String getFirstSelectedOptionText() {
        getElement();
        try {
            String text = select.getFirstSelectedOption().getText();
            log.debug("Multiselect {} first selected option text is returned: {}", elementName, text);
            return text;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
               Cannot get first multiselect selected option.
               Multiselect:
               %s
               """.stripIndent(),
               smartBy), e);
        }
    }

    /**
     * Returns the firs selected option value.
     * @return The value of the first selected option.
     */
    public String getFirstSelectedOptionValue() {
        getElement();
        try {
            String value = select.getFirstSelectedOption().getDomProperty("value");
            log.debug("Multiselect {} first selected option value is returned: {}", elementName, value);
            return value;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                Cannot get first multiselect selected option value.
                Multiselect:
                %s
                """.stripIndent(),
                smartBy), e);
        }
    }

    /**
     * Returns text list of selected options.
     * @return The text list of the selected options.
     */
    public List<String> getAlSelectedOptions() {
        getElement();
        try {
            List<String> options = select.getAllSelectedOptions().stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
            log.debug("All multiselect {} selected option text values are returned:\n{}", elementName, options);
            return options;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                Cannot get all multiselect selected options.
                Multiselect:
                %s
                """.stripIndent(),
                smartBy), e);
        }
    }

    /**
     * Returns value list of selected values.
     * @return The value list of the selected values.
     */
    public List<String> getAlSelectedValues() {
        getElement();
        try {
            List<String> values = select.getAllSelectedOptions().stream()
                    .map(option -> option.getAttribute("value"))
                    .collect(Collectors.toList());
            log.debug("All multiselect {} selected option values are returned:\n{}", elementName, values);
            return values;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                Cannot get all multiselect selected option values.
                Multiselect:
                %s
                """.stripIndent(),
                smartBy), e);
        }
    }
}
