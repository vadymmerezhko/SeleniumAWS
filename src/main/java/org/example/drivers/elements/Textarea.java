package org.example.drivers.elements;

import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

/**
 * The textarea element class.
 */
public class Textarea extends TextInput {

    /**
     * The textarea element constructor with auto selector.
     */
    public Textarea() {
        super();
    }

    /**
     * The textarea element constructor by its selector.
     * @param by The element selector.
     */
    public Textarea(By by) {
        super(by);
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
