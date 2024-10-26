package org.example.data;

import lombok.Getter;

/**
 * The Web Form input data class.
 */
@SuppressWarnings("unused")
@org.example.annotations.SmartValue
@Getter
public class WebFormPageInput extends SmartData {
    private SmartValue textInput;
    private SmartValue textareaInput;
    private SmartValue dropdownSelectedOption;
    private SmartValue dataListSelectOption;
    private SmartValue filePath;
    private SmartValue checkbox1Value;
    private SmartValue checkbox2Value;
    private SmartValue radiobutton1Value;
    private SmartValue radiobutton2Value;
    private SmartValue color;
    private SmartValue date;
    private SmartValue range;
}
