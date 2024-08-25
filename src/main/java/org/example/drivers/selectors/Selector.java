package org.example.drivers.selectors;

import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;

public class Selector {
    private static final String DELIMITER = "=";
    private final SelectorType type;
    private final String value;

    public static Selector parseSelector(String typeAndValue) {
        DataValidationUtils.validateNotBlank(typeAndValue, "typeAndValue");
        int delimiterIndex = typeAndValue.indexOf(DELIMITER);

        if (delimiterIndex == -1) {
            throw new SmartRuntimeException(String.format(
                    "Wrong selector type and value string format: %s", typeAndValue));
        }
        String typeString = typeAndValue.trim().substring(0, delimiterIndex);
        DataValidationUtils.validateNotBlank(typeString, "typeString");
        SelectorType type = SelectorType.fromString(typeString);
        String value = typeAndValue.trim().substring(delimiterIndex + 1);
        DataValidationUtils.validateNotBlank(value, "value");

        return new Selector(type, value);
    }

    /**
     * Creates selector object with type and string value.
     * @param type The type.
     * @param value The string value.
     */
    public Selector(SelectorType type, String value) {
        DataValidationUtils.validateNotBlank(value, "value");
        this.type = type;
        this.value = value;
    }

    /**
     * Returns selector type.
     * @return The type.
     */
    public SelectorType getType() {
        return type;
    }

    /**
     * Returns selector value.
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", type.toString(), DELIMITER, value);
    }
}
