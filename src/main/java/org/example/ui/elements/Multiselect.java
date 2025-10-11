package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.interfaces.ReadableObject;
import org.example.interfaces.WritableObject;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The multiselect element class.
 */
@Slf4j
public class Multiselect extends SmartElement implements ReadableObject, WritableObject {
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
                    Cannot get multiselect web element by:
                    %s
                    """.stripIndent(),
                    smartBy), e);
        }
    }

    /**
     * Selects option by its text.
     * @param optionString The option text of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOption(String optionString) {
        DataValidator.notBlank(optionString, "optionString");
        DataValidator.notMultiline(optionString, "optionString");
        getElement();

        try {
            select.selectByVisibleText(optionString);
            log.debug("{} multiselect option is selected: {}",
                    elementName, optionString);
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
     * Selects options by collection of options.
     * @param options The collection to select.
     */
    public void selectOptions(Collection<String> options) {
        DataValidator.notNull(options, "options");
        getElement();

        try {
            for (String option : options) {
                selectOption(option);
            }
            log.debug("{} multiselect options are selected:\n{}",
                    elementName, options);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot select multiselect options.
                    Multiselect:
                    %s
                    Options:
                    %s
                    """.stripIndent(),
                    smartBy, options), e);
        }
    }

    /**
     * Selects options by collection of values.
     * @param values The values.
     */
    public void selectOptionsByValue(Collection<String> values) {
        DataValidator.notNull(values, "options");
        getElement();

        try {
            for (String value : values) {
                selectOptionByValue(value);
            }
            log.debug("{} multiselect options are selected by values:\n{}",
                    elementName, values);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot select multiselect options by values.
                    Multiselect:
                    %s
                    Values:
                    %s
                    """.stripIndent(),
                    smartBy, values), e);
        }
    }

    /**
     * Selects options by collection of indexes.
     * @param indexes The indexes.
     */
    public void selectOptionsByIndexes(Collection<Integer> indexes) {
        DataValidator.notNull(indexes, "indexes");
        getElement();

        try {
            for (Integer index : indexes) {
                selectOptionByIndex(index);
            }
            log.debug("{} multiselect options are selected by indexes:\n{}",
                    elementName, indexes);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot select multiselect options by indexes.
                    Multiselect:
                    %s
                    Indexes:
                    %s
                    """.stripIndent(),
                    smartBy, indexes), e);
        }
    }

    /**
     * Deselects option by its text.
     * @param optionString The text of the option to deselect.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOption(String  optionString) {
        DataValidator.notBlank(optionString, "optionString");
        DataValidator.notMultiline(optionString, "optionString");
        getElement();

        try {
            select.deselectByVisibleText(optionString);
            log.debug("{} multiselect option is deselected: {}", elementName, optionString);
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
     * Deselects options by collection of options.
     * @param options The options.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptions(Collection<String>  options) {
        DataValidator.notNull(options, "options");
        getElement();

        try {
            for (String option : options) {
                select.deselectByVisibleText(option);
            }
            log.debug("{} multiselect options are deselected:\n{}", elementName, options);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                   Cannot deselect multiselect options.
                   Multiselect:
                   %s
                   Options:
                   %s
                   """.stripIndent(),
                    smartBy, options), e);
        }
    }

    /**
     * Deselects options by list of values.
     * @param values The list of values.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptionsByValues(Collection<String>  values) {
        DataValidator.notNull(values, "values");
        getElement();

        try {
            for (String option : values) {
                select.deselectByValue(option);
            }
            log.debug("{} multiselect options are deselected by values:\n{}", elementName, values);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                   Cannot deselect multiselect options by values.
                   Multiselect:
                   %s
                   Values:
                   %s
                   """.stripIndent(),
                    smartBy, values), e);
        }
    }

    /**
     * Deselects options by list of indexes.
     * @param indexes The list of indexes.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptionsByIndexes(Collection<Integer>  indexes) {
        DataValidator.notNull(indexes, "indexes");
        getElement();

        try {
            for (Integer index : indexes) {
                select.deselectByIndex(index);
            }
            log.debug("{} multiselect options are deselected by indexes:\n{}", elementName, indexes);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                   Cannot deselect multiselect options by values.
                   Multiselect:
                   %s
                   Values:
                   %s
                   """.stripIndent(),
                    smartBy, indexes), e);
        }
    }

    /**
     * Selects all options.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectAllOptions() {
        getElement();

        try {
            int size = select.getOptions().size();

            for (int i = 0; i < size; i++) {
                selectOptionByIndex(i);
            }
            log.debug("All {} multiselect option are selected.", elementName);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                   Cannot select all multiselect options.
                   Multiselect:
                   %s
                   Options:
                   %s
                   """.stripIndent(),
                    smartBy), e);
        }
    }

    /**
     * Deselects all options.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectAllOptions() {
        List<String> options = getAllSelectedOptions();

        try {
            for (String option : options) {
                select.deselectByVisibleText(option);
            }
            log.debug("All {} multiselect option are deselected.", elementName);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                   Cannot deselect multiselect options.
                   Multiselect:
                   %s
                   Options:
                   %s
                   """.stripIndent(),
                    smartBy, options), e);
        }
    }

    /**
     * Selects option by its value.
     * @param valueString The value of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOptionByValue(String valueString) {
        DataValidator.notBlank(valueString, "valueString");
        DataValidator.notMultiline(valueString, "valueString");
        getElement();

        try {
            select.selectByValue(valueString);
            log.debug("{} multiselect option is selected by value: {}", elementName, valueString);
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
     * @param valueString The value of the option to deselect.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptionByValue(String  valueString) {
        DataValidator.notBlank(valueString, "value");
        DataValidator.notMultiline(valueString, "valueString");
        getElement();
        try {
            select.deselectByValue(valueString);
            log.debug("{} multiselect option is deselected by value: {}", elementName, valueString);
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
        DataValidator.notNull(index, "index");
        selectOptionByIndex(index.toInteger());
    }

    /**
     * Selects option by its index.
     * @param index The index of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOptionByIndex(int index) {
        DataValidator.min(index, 0, "index");
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
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void deselectOptionByIndex(int index) {
        DataValidator.min(index, 0, "index");
        getElement();
        try {
            select.deselectByIndex(index);
            log.debug("{} multiselect option is deselected by index: {}", elementName, index);
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
    public String getFirstSelectedOption() {
        getElement();
        try {
            String text = select.getFirstSelectedOption().getText();
            log.debug("{} multiselect first selected option text is returned: {}", elementName, text);
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
            log.debug("{} multiselect first selected option value is returned: {}", elementName, value);
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
     * Returns list of selected options.
     * @return The list of the selected options.
     */
    public List<String> getAllSelectedOptions() {
        getElement();

        try {
            List<String> options = select.getAllSelectedOptions().stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
            log.debug("All {} multiselect selected option text values are returned:\n{}", elementName, options);
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
     * Returns text list of selected option values.
     * @return The list of the selected option values.
     */
    public List<String> getAllSelectedOptionValues() {
        getElement();

        try {
            List<String> options = select.getAllSelectedOptions().stream()
                    .map(element -> element.getAttribute("value"))
                    .collect(Collectors.toList());
            log.debug("All {} multiselect selected option values are returned:\n{}", elementName, options);
            return options;
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

    /**
     * Returns list of selected option indexes.
     * @return The list of the selected option indexes.
     */
    public List<Integer> getAllSelectedOptionIndexes() {
        getElement();

        try {
            List<Integer> indexes = new ArrayList<>();
            List<WebElement> options = select.getOptions();
            int size = options.size();

            for (int i = 0; i < size; i++) {

                if (options.get(i).isSelected()) {
                    indexes.add(i);
                }
            }
            log.debug("All {} multiselect selected option indexes are returned:\n{}", elementName, options);
            return indexes;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get all multiselect selected option indexes.
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
            log.debug("All {} multiselect selected option values are returned:\n{}", elementName, values);
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

    /**
     * Gets multiselect smart value - the list of selected options.
     * @return The smart value.
     */
    @Override
    public SmartValue getValue() {
        return new SmartValue(getAllSelectedOptions());
    }

    /**
     * Sets multiselect value - the list of selected options.
     * @param value The value.
     * @param <T> The list type.
     */
    @Override
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "value");
        SmartValue smartValue = new SmartValue(value);

        if (smartValue.isCollectable()) {

            try {
                List<String> options = smartValue.toList();
                selectOptions(options);
            }
            catch (Exception e1) {
                List<String> options = smartValue.toList();
                try {
                    selectOptionsByValue(options);
                }
                catch (Exception e2) {
                    List<Integer> indexes = smartValue.toList();
                    selectOptionsByIndexes(indexes);
                }
            }
        }
        else if (value instanceof String stringValue) {
            try {
                selectOption(stringValue);
            }
            catch (Exception e) {
                selectOptionByValue(stringValue);
            }
        }
        else if (smartValue.isNumeric()) {
            selectOptionByIndex(smartValue.toInteger());
        }
        else if (value instanceof SmartValue smartValue2) {
            setValue(smartValue2.getValue());
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "%s multiselect invalid options item type: %s",
                    elementName, value));
        }
    }
}
