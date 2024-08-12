package org.example.data;

import lombok.Getter;

/**
 * The Web Form input data class.
 */
public class WebFormPageInput extends SmartDataObject {
    @Getter
    private final SmartType texInput = new SmartType();
    @Getter
    private final SmartType textareaInput = new SmartType();
    @Getter
    private final SmartType dropdownSelectedOption = new SmartType();
    @Getter
    private final SmartType dataListSelectOption = new SmartType();
    @Getter
    private final SmartType filePath = new SmartType();
    @Getter
    private final SmartType checkbox1Value = new SmartType();
    @Getter
    private final SmartType checkbox2Value = new SmartType();
    @Getter
    private final SmartType radiobutton1Value = new SmartType();
    @Getter
    private final SmartType radiobutton2Value = new SmartType();
    @Getter
    private final SmartType color = new SmartType();
    @Getter
    private final SmartType date = new SmartType();
    @Getter
    private final SmartType range = new SmartType();

    public  WebFormPageInput() {
        super();
        initialize();
    }
}
