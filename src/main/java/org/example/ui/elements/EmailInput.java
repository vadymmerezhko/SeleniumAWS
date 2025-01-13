package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

/**
 * Email input smart element class.
 */
@Slf4j
public class EmailInput extends TextInput {

    /**
     * The email input constructor with auto selector.
     */
    public EmailInput() {
    }

    /**
     * The email input constructor by its selector.
     * @param by The element selector.
     */
    public EmailInput(By by) {
        super(by);
    }
}
