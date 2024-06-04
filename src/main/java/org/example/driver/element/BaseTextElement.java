package org.example.driver.element;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * Base text element class.
 */
public abstract class BaseTextElement extends BaseElement {

    /**
     * Base text element constructor by its locator.
     * @param by The element locator.
     */
    public BaseTextElement(By by) {
        super(by);
    }

    /**
     * Enters element text.
     * @param text The text to enter.
     */
    public void enterText(String text) {
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
