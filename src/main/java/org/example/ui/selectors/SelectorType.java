package org.example.ui.selectors;

import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidator;

/**
 * The By locator type names.
 */
public enum SelectorType {
    URL("url"),
    CSS("css"),
    XPATH("xpath"),
    IMAGE("image");

    SelectorType(String selectorTypeName) {
        DataValidator.notBlank(selectorTypeName, "selectorTypeName");
        name = selectorTypeName;
    }

    @Override
    public String toString() {
        return name;
    }

    private final String name;

    public static SelectorType fromString(String selectorType) {
        DataValidator.notBlank(selectorType, "selectorType");

        for (SelectorType value : SelectorType.values()) {
            if (value.name.equalsIgnoreCase(selectorType)) {
                return value;
            }
        }
        throw new SmartRuntimeException(String.format("""
                        Cannot convert selector type string to selector type enum item.
                        Selector type: %s
                        """.stripIndent(),
                        selectorType));
    }
}
