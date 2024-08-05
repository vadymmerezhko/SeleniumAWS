package org.example.drivers.elements;

import org.openqa.selenium.By;

public class Label extends BaseElement {

    /**
     * Label element constructor with auto selector.
     */
    public Label() {
        super();
    }

    /**
     * Label element constructor by its selector.
     * @param by   The element selector.
     */
    public Label(By by) {
        super(by);
    }
}
