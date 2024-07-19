package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

/**
 * The textarea element class.
 */
public class Textarea extends TextInput {

    /**
     * The textarea element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public Textarea(BasePage page, By by) {
        super(page, by);
    }

    /**
     * Enters multiline element text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
        getElement().clear();
        DataValidationUtils.validateNotNull(text, this.getClass().getSimpleName());
        getElement().sendKeys(text);
    }
}
