package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.ui.wrappers.SmartElement;
import org.openqa.selenium.By;

/**
 * Button element class.
 */
@Slf4j
public class Button extends SmartElement {

    /**
     * Button element constructor with auto selector.
     */
    public Button() {
    }

    /**
     * Button element constructor by its selector.
     * @param by The element selector.
     */
    public Button(By by) {
        super(by);
    }
}
