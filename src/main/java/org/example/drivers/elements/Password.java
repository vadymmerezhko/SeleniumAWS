package org.example.drivers.elements;

import org.openqa.selenium.By;

/**
 * The password element class.
 */
public class Password extends SingleLineTextInput {

    /**
     * The password elemnt constructor with auto selector.
     */
    public Password() {
        super();
    }

    /**
     * The password elemnt constructor by its selector.
     * @param by The element selector.
     */
    public Password(By by) {
        super(by);
    }
}
