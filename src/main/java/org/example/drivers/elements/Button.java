package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.openqa.selenium.By;

/**
 * Button element class.
 */
public class Button extends BaseElement {

    /**
     * Button element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public Button(BasePage page, By by) {
        super(page, by);
    }
}
