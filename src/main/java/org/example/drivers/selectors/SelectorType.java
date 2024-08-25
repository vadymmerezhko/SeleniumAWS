package org.example.drivers.selectors;

import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.example.utils.WebUtils;

/**
 * The By locator type names.
 */
public enum SelectorType {
    URL("url"),
    CSS("css"),
    XPATH("xpath"),
    IMAGE("image");

    public static SelectorType parseSelectorString(String selectorString) {
        SelectorType selectorType;

        if (WebUtils.isXpath(selectorString)) {
            selectorType = XPATH;
        } else if (WebUtils.isImageSelector(selectorString)) {
            selectorType = IMAGE;
        } else if (WebUtils.isUrl(selectorString)) {
            selectorType = URL;
        } else {
            selectorType = CSS;
        }
        return selectorType;
    }

    SelectorType(String selectorTypeName) {
        DataValidationUtils.validateNotBlank(selectorTypeName, "selectorTypeName");
        name = selectorTypeName;
    }

    @Override
    public String toString() {
        return name;
    }

    private final String name;

    public static SelectorType fromString(String selectorType) {
        DataValidationUtils.validateNotBlank(selectorType, "selectorType");

        for (SelectorType value : SelectorType.values()) {
            if (value.name.equalsIgnoreCase(selectorType)) {
                return value;
            }
        }
        throw new SmartRuntimeException(String.format(
                "Cannot convert '%s' to SmartByType enum item.", selectorType));
    }
}
