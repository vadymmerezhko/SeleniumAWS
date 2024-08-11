package org.example.data;

import lombok.Getter;

/**
 * The Web Form input data class.
 */
public class WebFormPageInput extends SmartDataObject {
    @Getter
    private final SmartType texInput = SmartType.auto();
    @Getter
    private final SmartType textareaInput = SmartType.auto();
    @Getter
    private final SmartType dropdownSelectedOption = SmartType.auto();
    @Getter
    private final SmartType dataListSelectOption = SmartType.auto();
    @Getter
    private final SmartType filePath = SmartType.auto();
    @Getter
    private final SmartType checkbox1Value = SmartType.auto();
    @Getter
    private final SmartType checkbox2Value = SmartType.auto();
    @Getter
    private final SmartType radiobutton1Value = SmartType.auto();
    @Getter
    private final SmartType radiobutton2Value = SmartType.auto();
    @Getter
    private final SmartType color = SmartType.auto();
    @Getter
    private final SmartType date = SmartType.auto();
    @Getter
    private final SmartType range = SmartType.auto();

    public  WebFormPageInput() {
        super();
        initialize();
    }
}
