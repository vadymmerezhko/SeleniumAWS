package org.example.ui.elements;

import org.example.ui.wrappers.SmartElement;
import org.openqa.selenium.By;

public class Field extends SmartElement {

    /**
     * Field element constructor with auto selector.
     */
    public Field() {
    }

    /**
     * Field element constructor by its selector.
     * @param by The element selector.
     */
    public Field(By by) {
        super(by);
    }
}
