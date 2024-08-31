package org.example.drivers.elements;

import org.example.pages.SmartElement;
import org.openqa.selenium.By;

public class Field extends SmartElement {

    /**
     * Field element constructor with auto selector.
     */
    public Field() {
        super();
    }

    /**
     * Field element constructor by its selector.
     * @param by   The element selector.
     */
    public Field(By by) {
        super(by);
    }
}
