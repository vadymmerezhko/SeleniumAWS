package org.example.ui.elements;

import org.openqa.selenium.By;

/**
 * The password element class.
 */
public class Password extends SingleLineTextInput {

    /**
     * The password element constructor with auto selector.
     */
    public Password() {
    }

    /**
     * The password element constructor by its selector.
     * @param by The element selector.
     */
    public Password(By by) {
        super(by);
    }
}
