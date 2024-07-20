package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.openqa.selenium.By;

public class Label extends BaseElement {
    /**
     * Base element constructor by its page and selector.
     *
     * @param page The element page.
     * @param by   The element selector.
     */
    public Label(BasePage page, By by) {
        super(page, by);
    }
}
