package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.ui.wrappers.SmartElement;
import org.openqa.selenium.By;

/**
 * Read-only field element class.
 */
@Slf4j
public class Field extends SmartElement implements ReadableObject {

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

    /**
     * Gets field value or text.
     * @return The value or text.
     */
    public String getStringValue() {
        String value;

        try {
            value = getValueDomProperty();
        }
        catch (Exception e) {
            value = getText();
        }
        if (value == null) {
            value = getText();
        }
        log.debug("{} field value or text is returned: {}", elementName, value);
        return value;
    }

    /**
     * Gets field smart value.
     * @return The smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(getStringValue());
        log.debug("{} field smart value is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
