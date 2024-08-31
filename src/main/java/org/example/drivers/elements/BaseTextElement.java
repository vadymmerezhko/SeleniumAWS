package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.pages.SmartElement;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * Base text element class.
 */
@Slf4j
public abstract class BaseTextElement extends SmartElement {

    /**
     * Base text element constructor with auto selector.
     */
    public BaseTextElement() {
        super();
    }

    /**
     * Base text element constructor by its selector.
     * @param by The element selector.
     */
    public BaseTextElement(By by) {
        super(by);
    }

    /**
     * Enters element text.
     * @param text The text to enter.
     */
    public void enterText(SmartValue text) {
        DataValidationUtils.validateNotNull(text, "text");
        enterText(text.toString());
    }

    /**
     * Enters element text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        DataValidationUtils.validateNotNull(text, "text");
        getElement().clear();
        getElement().sendKeys(text);
        log.debug("Text element {} value is set to: {}", elementName, text);
    }

    /**
     * Pastes text from the clipboard (if any).
     */
    public void pasteText() {
        sendKeys(Keys.chord(Keys.CONTROL, "v"));
        log.debug("Text from buffer is pasted to text element {}.", elementName);
    }
}
