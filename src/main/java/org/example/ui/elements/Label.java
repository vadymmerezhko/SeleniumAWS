package org.example.ui.elements;

import org.example.ui.wrappers.SmartElement;
import org.openqa.selenium.By;

public class Label extends SmartElement {

    /**
     * Label element constructor with auto selector.
     */
    public Label() {
    }

    /**
     * Label element constructor by its selector.
     * @param by   The element selector.
     */
    public Label(By by) {
        super(by);
    }
}
