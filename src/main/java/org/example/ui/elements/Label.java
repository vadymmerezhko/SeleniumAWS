package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.ui.wrappers.SmartElement;
import org.openqa.selenium.By;

@Slf4j
public class Label extends SmartElement implements ReadableObject {

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

    /**
     * Gets label text value.
     * @return The text value.
     */
    public String getValueString() {
        getElement();
        String value;
        value = getText();
        log.debug("{} label text value is returned: {}", elementName, value);
        return value;
    }

    /**
     * Gets label smart value.
     * @return The smart value.
     */
    @Override
    public SmartValue getValue() {
        return new SmartValue(getValueString());
    }
}
