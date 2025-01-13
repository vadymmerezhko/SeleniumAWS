package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.openqa.selenium.By;

/**
 * Telephone input smart element class.
 */
@Slf4j
public class TelephoneInput extends TextInput {

    /**
     * The telephone input constructor with auto selector.
     */
    public TelephoneInput() {
    }

    /**
     * The telephone input constructor by its selector.
     * @param by The element selector.
     */
    public TelephoneInput(By by) {
        super(by);
    }

    @Override
    public SmartValue getValue() {
        String valueString = getValueString();
        SmartValue smartValue = new SmartValue(valueString);
        log.debug("{} smart value telephone number is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
