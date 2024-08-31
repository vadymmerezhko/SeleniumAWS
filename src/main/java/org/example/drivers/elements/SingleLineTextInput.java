package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

/**
 * The single line text input element class.
 */
@Slf4j
public abstract class SingleLineTextInput extends BaseTextElement {

    /**
     * The single line text input constructor with auto selector.
     */
    public SingleLineTextInput() {
        super();
    }

    /**
     * The single line text input constructor by its selector.
     * @param by The element selector.
     */
    public SingleLineTextInput(By by) {
        super(by);
    }

    /**
     * Enters single line text.
     * @param text The text to enter.
     */
    public void enterText(SmartValue text) {
        DataValidationUtils.validateNotNull(text, "text");
        String textString = text.toString();
        enterText(textString);
    }

    /**
     * Enters single line text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        DataValidationUtils.validateNotMultiline(text, "text");
        getElement();
        super.enterText(text);
        log.debug("Single line text input {} value is set to: {}", elementName, text);
    }
}
