package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.openqa.selenium.By;

/**
 * The password element class.
 */
public class Password extends SingleLineTextInput {

    /**
     * The password elemnt constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public Password(BasePage page, By by) {
        super(page, by);
    }
}
