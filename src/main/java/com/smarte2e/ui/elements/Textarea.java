package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.data.SmartValue;
import com.smarte2e.interfaces.WritableObject;
import com.smarte2e.utils.DataValidator;
import org.openqa.selenium.By;

/**
 * The textarea element class.
 */
@Slf4j
public class Textarea extends TextInput implements WritableObject {

    /**
     * The textarea element constructor with auto selector.
     */
    public Textarea() {
    }

    /**
     * The textarea element constructor by its selector.
     * @param by The element selector.
     */
    public Textarea(By by) {
        super(by);
    }

    /**
     * Enters multiline element text.
     * @param text The text to enter.
     */
    public void enterText(SmartValue text) {
        DataValidator.notNull(text, "text");
        enterText(text.toString());
    }

    /**
     * Enters multiline element text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        DataValidator.notNull(text, "text");

        clear();
        sendKeys(text);
        // Do not log input text for security purpose
        log.debug("Text area input {} value is set.", elementName);
    }
}
