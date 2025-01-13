package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.interfaces.WritableObject;
import org.example.utils.DataValidationUtils;
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
        DataValidationUtils.validateNotNull(text, "text");
        enterText(text.toString());
    }

    /**
     * Enters multiline element text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        DataValidationUtils.validateNotNull(text, "text");

        clear();
        sendKeys(text);
        // Do not log input text for security purpose
        log.debug("Text area input {} value is set.", elementName);
    }
}
