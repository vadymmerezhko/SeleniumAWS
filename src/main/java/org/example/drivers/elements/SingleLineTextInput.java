package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

/**
 * The single line text input element class.
 */
public abstract class SingleLineTextInput extends BaseTextElement {

    /**
     * The single line text input constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public SingleLineTextInput(BasePage page, By by) {
        super(page, by);
    }

    /**
     * Enters single line text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        getElement();
        DataValidationUtils.validateNotMultiline(text, this.getClass().getSimpleName());
        super.enterText(text);
    }
}
