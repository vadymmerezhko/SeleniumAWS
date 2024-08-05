package org.example.drivers.elements;

import org.openqa.selenium.By;

/**
 * File input element class that extents text input class.
 */
public class FileInput extends TextInput {

    /**
     * File input element constructor with auto selector.
     */
    public FileInput() {
        super();
    }

    /**
     * File input element constructor by its selector.
     * @param by The element selector.
     */
    public FileInput(By by) {
        super(by);
    }
}
