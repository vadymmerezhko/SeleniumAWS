package org.example.data;

/**
 * The Web Form input data class.
 */
public class WebFormPageInput extends SmartDataObject {
    private SmartType texInput;
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

    public WebFormPageInput initialize() {
        texInput = new SmartType(this);
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

    public String getTextInput() {
        return texInput.toString();
    }

    public String getTextareaInput() {
        return textareaInput.toString();
    }

    public String getDropdownSelectedOption() {
        return dropdownSelectedOption.toString();
    }

    public String getDataListSelectOption() {
        return dataListSelectOption.toString();
    }

    public String getFilePath() {
        return filePath.toString();
    }

    public boolean getCheckbox1Value() {
        return checkbox1Value.toBoolean();
    }

    public boolean getCheckbox2Value() {
        return checkbox2Value.toBoolean();
    }

    public boolean getRadiobutton1Value() {
        return radiobutton1Value.toBoolean();
    }

    public boolean getRadiobutton2Value() {
        return radiobutton2Value.toBoolean();
    }

    public String getColor() {
        return color.toString();
    }

    public String getDate() {
        return date.toString();
    }

    public int getRange() {
        return range.toInteger();
    }
}
