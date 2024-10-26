package org.example.ui.selectors;

import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;

/**
 * The By locator type names.
 */
public enum SmartByType {
    ID("id"),
    NAME("name"),
    CLASS_NAME("className"),
    TAG_NAME("tagName"),
    LINK_TEXT("linkText"),
    PARTIAL_LINK_TEXT("partialLinkText"),
    CSS("cssSelector"),
    XPATH("xpath"),
    IMAGE("image"),
    URL("url"),
    AUTO("auto");

    SmartByType(String selectorTypeName) {
        DataValidationUtils.validateNotBlank(selectorTypeName, "selectorTypeName");
        name = selectorTypeName;
    }

    @Override
    public String toString() {
        return name;
    }

    private final String name;

    public static SmartByType fromString(String selectorType) {
        DataValidationUtils.validateNotBlank(selectorType, "selectorType");

        for (SmartByType value : SmartByType.values()) {
            if (value.name.equalsIgnoreCase(selectorType)) {
                return value;
            }
        }
        throw new SmartRuntimeException(String.format(
                "Cannot convert '%s' to SmartByType enum item.", selectorType));
    }
}
