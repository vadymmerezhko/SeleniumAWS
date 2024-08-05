package org.example.data;

/**
 * The Web Form output data class.
 */
public class WebFormPageOutput extends SmartDataObject {
    private SmartType textInput;
    private SmartType textareaInput;
    private SmartType dropdownSelectedOption;
    private SmartType dataListSelectOption;
    private SmartType filePath;
    private SmartType checkbox1Value;
    private SmartType checkbox2Value;
    private SmartType radiobutton1Value;
    private SmartType radiobutton2Value;
    private SmartType color;
    private SmartType date;
    private SmartType range;

    public WebFormPageOutput initialize() {
        textInput = new SmartType("Text input", this);
        textareaInput = new SmartType(this);
        dropdownSelectedOption = new SmartType(this);
        dataListSelectOption = new SmartType(this);
        filePath = new SmartType(this);
        checkbox1Value = new SmartType(this);
        checkbox2Value = new SmartType(this);
        radiobutton1Value = new SmartType(this);
        radiobutton2Value = new SmartType(this);
        color = new SmartType(this);
        date = new SmartType(this);
        range = new SmartType(this);
        return this;
    }

    public WebFormPageOutput setTexInput(String value) {
        textInput.setValue(value);
        return this;
    }

    public WebFormPageOutput setTextareaInput(String value) {
        textareaInput.setValue(value);
        return this;
    }

    public WebFormPageOutput setDropdownSelectedOption(String value) {
        dropdownSelectedOption.setValue(value);
        return this;
    }

    public WebFormPageOutput setDataListSelectOption(String value) {
        dataListSelectOption.setValue(value);
        return this;
    }

    public WebFormPageOutput setFilePath(String value) {
        filePath.setValue(value);
        return this;
    }

    public WebFormPageOutput setCheckbox1Value(boolean value) {
        checkbox1Value.setValue(value);
        return this;
    }

    public WebFormPageOutput setCheckbox2Value(boolean value) {
        checkbox2Value.setValue(value);
        return this;
    }

    public WebFormPageOutput setRadiobutton1Value(boolean value) {
        radiobutton1Value.setValue(value);
        return this;
    }

    public WebFormPageOutput setRadiobutton2Value(boolean value) {
        radiobutton2Value.setValue(value);
        return this;
    }

    public WebFormPageOutput setColor(String value) {
        color.setValue(value);
        return this;
    }

    public String getDate() {
        return date.toString();
    }

    public WebFormPageOutput setDate(String value) {
        date.setValue(value);
        return this;
    }

    public int getRange() {
        return range.toInteger();
    }

    public WebFormPageOutput setRange(int value) {
        range.setValue(value);
        return this;
    }
}
