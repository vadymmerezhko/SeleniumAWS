package org.example.data;

import lombok.Getter;

/**
 * The Web Form input data class.
 */
public class WebFormPageInput extends SmartDataObject {
    @Getter
    private final SmartClass texInput = new SmartClass();
    @Getter
    private final SmartClass textareaInput = new SmartClass();
    @Getter
    private final SmartClass dropdownSelectedOption = new SmartClass();
    @Getter
    private final SmartClass dataListSelectOption = new SmartClass();
    // TODO: fix file element for remote web driver
    //@Getter
    //private final SmartClass filePath = new SmartClass();
    @Getter
    private final SmartClass checkbox1Value = new SmartClass();
    @Getter
    private final SmartClass checkbox2Value = new SmartClass();
    @Getter
    private final SmartClass radiobutton1Value = new SmartClass();
    @Getter
    private final SmartClass radiobutton2Value = new SmartClass();
    @Getter
    private final SmartClass color = new SmartClass();
    @Getter
    private final SmartClass date = new SmartClass();
    @Getter
    private final SmartClass range = new SmartClass();

    public WebFormPageInput() {
        super();
        initialize();
    }
}
