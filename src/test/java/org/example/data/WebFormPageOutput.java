package org.example.data;

import lombok.Getter;

/**
 * The Web Form output data class.
 */
public class WebFormPageOutput extends SmartData {
    @Getter
    private final SmartValue textInput = new SmartValue();
    @Getter
    private final SmartValue textareaInput = new SmartValue();
    @Getter
    private final SmartValue dropdownSelectedOption = new SmartValue();
    @Getter
    private final SmartValue dataListSelectOption = new SmartValue();
    // TODO: fix file element for remote web driver
    //@Getter
    //private final SmartValue filePath = new SmartValue();
    @Getter
    private final SmartValue checkbox1Value = new SmartValue();
    @Getter
    private final SmartValue checkbox2Value = new SmartValue();
    @Getter
    private final SmartValue radiobutton1Value = new SmartValue();
    @Getter
    private final SmartValue radiobutton2Value = new SmartValue();
    @Getter
    private final SmartValue color = new SmartValue();
    @Getter
    private final SmartValue date = new SmartValue();
    @Getter
    private final SmartValue range = new SmartValue();
    
    public WebFormPageOutput()  {
        super();
        initialize();
    }
}
