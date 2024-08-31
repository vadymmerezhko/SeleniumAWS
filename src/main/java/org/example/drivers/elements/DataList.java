package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The data list element class.
 */
@Slf4j
public class DataList extends BaseTextElement {

    /**
     * The data list element constructor with auto selector.
     */
    public DataList() {
        super();
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
    public void selectOption(SmartValue option) {
        DataValidationUtils.validateNotNull(option, "option");
        selectOption(option.toString());
    }

    /**
     * Selects data list option by its text.
     * @param option The text of the option to select.
     */
    public void selectOption(String option) {
        DataValidationUtils.validateNotBlank(option, "option");
        getElement();
        enterText(option);
        log.debug("Data list {} option is selected: {}", elementName, option);
    }

    /**
     * Selects data list option by its index.
     * @param index The data list option index.
     */
    public void selectOptionByIndex(SmartValue index) {
        DataValidationUtils.validateNotNull(index, "index");
        selectOptionByIndex(index.toInteger());

    }

    /**
     * Selects data list option by its index.
     * @param index The data list option index.
     */
    public void selectOptionByIndex(int index) {
        WebElement option = getElement().findElement(By.xpath(
                String.format("..//option[%d]", index)));
        String optionText = option.getText();

        if (optionText == null || optionText.isEmpty()) {
            SmartValue optionValue = new SmartValue(option.getDomProperty("value"));
            selectOption(optionValue);
        }
        else {
            selectOption(new SmartValue(optionText));
        }
        log.debug("Data list {} option {} is selected by index: {}", elementName, option, index);
    }

    /**
     * Returns the text of the dat list selected option.
     * @return The selected option text.
     */
    public String getValue() {
        String value = getElement().getDomProperty("value");
        log.debug("Data list {} value is returned: {}", elementName, value);
        return value;
    }
}
