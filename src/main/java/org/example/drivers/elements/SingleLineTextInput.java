package org.example.drivers.elements;

import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

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
        DataValidationUtils.validateNotMultiline(text, this.getClass().getSimpleName(), getElement());
        super.enterText(text);
    }
}
