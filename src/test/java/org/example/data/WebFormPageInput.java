package org.example.data;

/**
 * The Web Form input data class.
 */
public class WebFormPageInput extends SmartDataObject {
    private final SmartType texInput = SmartType.auto();
    private final SmartType textareaInput = SmartType.auto();
    private final SmartType dropdownSelectedOption = SmartType.auto();
    private final SmartType dataListSelectOption = SmartType.auto();
    private final SmartType filePath = SmartType.auto();
    private final SmartType checkbox1Value = SmartType.auto();
    private final SmartType checkbox2Value = SmartType.auto();
    private final SmartType radiobutton1Value = SmartType.auto();
    private final SmartType radiobutton2Value = SmartType.auto();
    private final SmartType color = SmartType.auto();
    // TODO
    //private final SmartType date = SmartType.auto();
    private final SmartType range = SmartType.auto();

    public  WebFormPageInput() {
        super();
        initialize();
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

    // TODO
/*    public String getDate(String keyword) {
        date.setKeyword(keyword);
        return date.toString();
    }*/

    public int getRange() {
        return range.toInteger();
    }
}
