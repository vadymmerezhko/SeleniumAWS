package org.example.drivers.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * The text input element class.
 */
public class TextInput extends SingleLineTextInput {

    /**
     * The text input constructor by its locator.
     * @param by The text input locator.
     */
    public TextInput(By by) {
        super(by);
    }

    /**
     * Returns text input value.
     * @return The text input value.
     */
    public String getValue() {
        return getElement().getDomProperty("value");
    }

    /**
     * Selects all text.
     */
    public void selectAllText() {
        sendKeys(Keys.chord(Keys.CONTROL, "a"));
    }

    /**
     * Copies input text to the clipboard.
     */
    public void copyText() {
        sendKeys(Keys.chord(Keys.CONTROL, "c"));
    }

    /**
     * Cuts text from text input to the clipboard.
     */
    public void cutText() {
        sendKeys(Keys.chord(Keys.CONTROL, "x"));
    }
}
