package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;


/**
 * URL input smart element class.
 */
@Slf4j
public class URLInput extends TextInput {

    /**
     * The URL input constructor with auto selector.
     */
    public URLInput() {
    }

    /**
     * The URL input constructor by its selector.
     * @param by The element selector.
     */
    public URLInput(By by) {
        super(by);
    }
}
