package org.example.drivers.elements;

import org.example.pages.BasePage;
import org.openqa.selenium.By;

/**
 * File input element class that extents text input class.
 */
public class FileInput extends TextInput {

    /**
     * File input element constructor by its page and selector.
     * @param page The element page.
     * @param by The element selector.
     */
    public FileInput(BasePage page, By by) {
        super(page, by);
    }
}
