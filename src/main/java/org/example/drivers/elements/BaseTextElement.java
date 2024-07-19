package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * Base text element class.
 */
public abstract class BaseTextElement extends BaseElement {

    /**
     * Base text element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public BaseTextElement(BasePage page, By by) {
        super(page, by);
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
