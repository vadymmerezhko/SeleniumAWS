package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartNumber;
import org.example.data.SmartValue;
import org.openqa.selenium.By;

/**
 * Number input smart element class.
 */
@Slf4j
public class NumberInput extends TextInput {

    /**
     * The Number input constructor with auto selector.
     */
    public NumberInput() {
    }

    /**
     * The Number input constructor by its selector.
     * @param by The element selector.
     */
    public NumberInput(By by) {
        super(by);
    }

    @Override
    public SmartValue getValue() {
        String valueString = getValueString();
        SmartNumber smartNumber = SmartNumber.fromString(valueString);
        SmartValue smartValue = new SmartValue(smartNumber);
        log.debug("{} number input smart value number is returned: {}", elementName, smartValue);
        return smartValue;
    }
}
