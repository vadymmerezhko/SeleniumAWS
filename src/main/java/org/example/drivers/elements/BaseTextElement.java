package org.example.drivers.elements;

import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * Base text element class.
 */
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
    public void enterText(String text) {
        DataValidationUtils.validateNotNull(text, this.getClass().getSimpleName());
        getElement().clear();
        getElement().sendKeys(text);
    }

    /**
     * Pastes text from the clipboard (if any).
     */
    public void pasteText() {
        sendKeys(Keys.chord(Keys.CONTROL, "v"));
    }
}
