package org.example.drivers.elements;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * The text input element class.
 */
@Slf4j
public class TextInput extends SingleLineTextInput {

    /**
     * The text input constructor with auto selector.
     */
    public TextInput() {
        super();
    }

    /**
     * The text input constructor by its selector.
     * @param by The element selector.
     */
    public TextInput(By by) {
        super(by);
    }

    /**
     * Returns text input value.
     * @return The text input value.
     */
    public String getValue() {
        String value = getElement().getDomProperty("value");
        log.debug("Text input {} value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Selects all text.
     */
    public void selectAllText() {
        sendKeys(Keys.chord(Keys.CONTROL, "a"));
        log.debug("Text input {} all text is selected.", elementName);
    }

    /**
     * Copies input text to the clipboard.
     */
    public void copyText() {
        sendKeys(Keys.chord(Keys.CONTROL, "c"));
        log.debug("Text input {} selected text is copied to buffer.", elementName);
    }

    /**
     * Cuts text from text input to the clipboard.
     */
    public void cutText() {
        sendKeys(Keys.chord(Keys.CONTROL, "x"));
        log.debug("Text input {} selected text is cut to buffer.", elementName);
    }
}
