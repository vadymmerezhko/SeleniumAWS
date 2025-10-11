package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.interfaces.ReadableObject;
import org.example.interfaces.WritableObject;
import org.example.utils.DataValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The data list element class.
 */
@Slf4j
public class DataList extends BaseTextElement implements ReadableObject, WritableObject {

    /**
     * The data list element constructor with auto selector.
     */
    public DataList() {
    }

    /**
     * The data list element constructor by its selector.
     * @param by The element selector.
     */
    public DataList(By by) {
        super(by);
    }

    /**
     * Selects data list option by its text.
     * @param option The text of the option to select.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOption(String option) {
        DataValidator.notBlank(option, "option");
        enterText(option);
        log.debug("Data list {} option is selected: {}", elementName, option);
    }

    /**
     * Sets data list option value by its text.
     * @param value The text of the option to select.
     * @param <T> The value type.
     */
    @Override
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "option");
        SmartValue smartValue = new SmartValue(value);

        if (smartValue.isNumeric()) {
            selectOptionByIndex(smartValue.toInteger());
        }
        else if (value instanceof String stringValue) {
            selectOption(stringValue);
        }
        else if (value instanceof SmartValue smartValue2) {
            setValue(smartValue2.getValue());
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "%s data list invalid value type: %s",
                    elementName, value.getClass().getName()));
        }
        log.debug("{} data list value is set to: {}", elementName, value);
    }

    /**
     * Selects data list option by its index.
     * @param index The data list option index.
     */
    public void selectOptionByIndex(SmartValue index) {
        DataValidator.notNull(index, "index");
        selectOptionByIndex(index.toInteger());
    }

    /**
     * Selects data list option by its index.
     * @param index The data list option index.
     */
    @RunAlone // Run this method when other methods wait to prevent dropdown closing by other thread
    public void selectOptionByIndex(int index) {
        WebElement option = findElement(By.xpath(String.format("..//option[%d]", index)));
        String optionText = option.getText();

        if (optionText == null || optionText.isEmpty()) {
            SmartValue optionValue = new SmartValue(option.getDomProperty("value"));
            setValue(optionValue);
        }
        else {
            setValue(new SmartValue(optionText));
        }
        log.debug("Data list {} option {} is selected by index: {}", elementName, option, index);
    }

    /**
     * Returns the data list selected option string.
     * @return The selected option string.
     */
    public String getValueString() {
        String value = getValueDomProperty();
        log.debug("Data list {} value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Returns the date list selected option smart value.
     * @return The selected option smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(getValueString());
        log.debug("Data list {} selected option smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
