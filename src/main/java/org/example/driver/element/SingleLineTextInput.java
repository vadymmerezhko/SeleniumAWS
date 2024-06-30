package org.example.driver.element;

import org.example.utils.DataValidator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * The single line text input element class.
 */
public abstract class SingleLineTextInput extends BaseTextElement {

    /**
     * The single line text input constructor by its locator.
     * @param by The text input locator.
     */
    public SingleLineTextInput(By by) {
        super(by);
    }

    /**
     * Enters single line text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        DataValidator.validateNotMultiline(text, this.getClass().getSimpleName(), getElement());
        super.enterText(text);
    }
}
