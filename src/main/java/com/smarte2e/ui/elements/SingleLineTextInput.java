package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.utils.DataValidator;
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
    public void enterText(String text) {
        DataValidator.notMultiline(text, "text");
        super.enterText(text);
        // Do not log input text for security purpose
        log.debug("Single line text input {} value is set to", elementName);
    }
}
