package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.interfaces.ReadableObject;
import org.example.ui.wrappers.SmartElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Hidden input element class.
 */
@Slf4j
public class HiddenInput extends SmartElement implements WebElement, ReadableObject {

    /**
     * Hidden input element constructor.
     */
    public HiddenInput() {
    }

    /**
     * Hidden input element constructor by its selector.
     * @param by The element selector.
     */
    public HiddenInput(By by) {
        super(by);
    }

    /**
     * Gets hidden input element text smart value.
     * @return The text smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(getValueString());
        // Do not log hidden input value for security purpose.
        log.debug("{} hidden input smart value is returned.", elementName);
        return smartValue;
    }

    /**
     * Gets Hidden input element text value string.
     * @return The text value string.
     */
    public String getValueString() {
        String value = getValueDomProperty();
        // Do not log hidden input value for security purpose.
        log.debug("{} hidden input value is returned.", elementName);
        return value;
    }
}
