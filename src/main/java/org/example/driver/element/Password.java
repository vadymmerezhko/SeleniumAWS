package org.example.driver.element;

import org.example.utils.DataValidator;
import org.openqa.selenium.By;

/**
 * The password element class.
 */
public class Password extends SingleLineTextInput {

    /**
     * The password elemnt constructor by its locator.
     * @param by The password element locator.
     */
    public Password(By by) {
        super(by);
    }
}
