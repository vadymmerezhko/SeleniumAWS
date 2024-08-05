package org.example.drivers.elements;

import org.openqa.selenium.By;

/**
 * Button element class.
 */
public class Button extends BaseElement {

    /**
     * Button element constructor with auto selector.
     */
    public Button() {
        super();
    }

    /**
     * Button element constructor by its selector.
     * @param by The element selector.
     */
    public Button(By by) {
        super(by);
    }
}
