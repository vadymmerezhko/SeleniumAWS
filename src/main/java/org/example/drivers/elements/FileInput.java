package org.example.drivers.elements;

import org.openqa.selenium.By;

/**
 * File input element class that extents text input class.
 */
public class FileInput extends TextInput {

    /**
     * File input element constructor by its locator.
     * @param by The element locator.
     */
    public FileInput(By by) {
        super(by);
    }
}
