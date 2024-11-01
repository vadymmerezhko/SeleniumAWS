package org.example.data;

import lombok.Getter;
import org.example.annotations.SmartValueField;


/**
 * The Web Form input data class.
 */
@SuppressWarnings("unused")
@SmartValueField
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
