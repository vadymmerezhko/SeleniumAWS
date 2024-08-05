package org.example.data;

/**
 * The Web Form output data class.
 */
public class WebFormPageOutput extends SmartDataObject {
    private final SmartType textInput = SmartType.auto();
    private final SmartType textareaInput = SmartType.auto();
    private final SmartType dropdownSelectedOption = SmartType.auto();
    private final SmartType dataListSelectOption = SmartType.auto();
    private final SmartType filePath = SmartType.auto();
    private final SmartType checkbox1Value = SmartType.auto();
    private final SmartType checkbox2Value = SmartType.auto();
    private final SmartType radiobutton1Value = SmartType.auto();
    private final SmartType radiobutton2Value = SmartType.auto();
    private final SmartType color = SmartType.auto();
    private final SmartType date = SmartType.auto();
    private final SmartType range = SmartType.auto();

    public WebFormPageOutput() {
        super();
        initialize();
    }

    public WebFormPageOutput setTexInput(String value) {
        textInput.setString(value);
        return this;
    }

    public WebFormPageOutput setTextareaInput(String value) {
        textareaInput.setString(value);
        return this;
    }

    public WebFormPageOutput setDropdownSelectedOption(String value) {
        dropdownSelectedOption.setString(value);
        return this;
    }

    public WebFormPageOutput setDataListSelectOption(String value) {
        dataListSelectOption.setString(value);
        return this;
    }

    public WebFormPageOutput setFilePath(String value) {
        filePath.setString(value);
        return this;
    }

    public WebFormPageOutput setCheckbox1Value(boolean value) {
        checkbox1Value.setBoolean(value);
        return this;
    }

    public WebFormPageOutput setCheckbox2Value(boolean value) {
        checkbox2Value.setBoolean(value);
        return this;
    }

    public WebFormPageOutput setRadiobutton1Value(boolean value) {
        radiobutton1Value.setBoolean(value);
        return this;
    }

    public WebFormPageOutput setRadiobutton2Value(boolean value) {
        radiobutton2Value.setBoolean(value);
        return this;
    }

    public WebFormPageOutput setColor(String value) {
        color.setString(value);
        return this;
    }

    public String getDate() {
        return date.toString();
    }

    public WebFormPageOutput setDate(String value) {
        date.setString(value);
        return this;
    }

    public int getRange() {
        return range.toInteger();
    }

    public WebFormPageOutput setRange(int value) {
        range.setInteger(value);
        return this;
    }
}
