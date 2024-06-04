package org.example.driver.element;

import org.openqa.selenium.By;

/**
 * Button element class.
 */
public class Button extends BaseElement {

    /**
     * Button element constructor by its locator.
     * @param by The button locator.
     */
    public Button(By by) {
        super(by);
    }
}
