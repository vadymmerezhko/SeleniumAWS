package org.example.drivers.selectors;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.example.utils.WebUtils;
import org.openqa.selenium.By;

/**
 * The By locator parser class.
 */
@Slf4j
public class SmartByParser {
    private static final String CSS_TEMPLATE = "css=%s";
    private static final String XPATH_TEMPLATE = "xpath=%s";

    /**
     * Returns selector type name.
     * @param elementSelector The By locator instance.
     * @return The locator type name.
     */
    public static SmartByType getByType(By elementSelector) {
        DataValidationUtils.validateNotNull(elementSelector, "elementSelector");

        String byString = elementSelector.toString();
        int delimiter1Index = byString.indexOf(".");
        int delimiter2Index = byString.indexOf(":");
        SmartByType type = SmartByType.fromString(byString.substring(delimiter1Index + 1, delimiter2Index));

        if (elementSelector instanceof SmartByImage && type == SmartByType.LINK_TEXT) {
            type =  SmartByType.IMAGE;
        }
        log.debug("SmartByType: {}", type);
        return type;
    }

    /**
     * Returns selector value.
     * @param elementSelector The By locator instance.
     * @return The locator value.
     */
    public static String getByValue(By elementSelector) {
        DataValidationUtils.validateNotNull(elementSelector, "elementSelector");

        String byString = elementSelector.toString();
        int delimiterIndex = byString.indexOf(":");
        return byString.substring(delimiterIndex + 1).trim();
    }

    /**
     * Convers By locator to string.
     * @param elementSelector The By locator instance.
     * @return The locator string value.
     */
    public static String getLocatorString(By elementSelector) {
        DataValidationUtils.validateNotNull(elementSelector, "elementSelector");

        SmartByType byType = SmartByParser.getByType(elementSelector);
        String byValue = SmartByParser.getByValue(elementSelector);

        return switch (byType) {
            case CSS, TAG_NAME -> String.format(CSS_TEMPLATE, byValue);
            case XPATH -> String.format(XPATH_TEMPLATE, byValue);
            case ID -> String.format("#%s", byValue);
            case CLASS_NAME -> String.format(".%s]", byValue);
            case NAME -> String.format("*[name='%s']", byValue);
            case LINK_TEXT, IMAGE -> WebUtils.isImageSelector(byValue) ?
                        // PNG image selector
                        byValue   :
                        // By link text selector
                        String.format("//a[text()='%s']", byValue);
            case PARTIAL_LINK_TEXT -> String.format("//a[contains(.,'%s')]", byValue);
            default -> throw new SmartRuntimeException(String.format(
                    "SmartByType is not supported: %s", byType));
        };
    }

    public static By getByFromStringSelector(String selector) {
        DataValidationUtils.validateNotBlank(selector, "selector");
        selector = selector.trim();

        if (WebUtils.isXpath(selector)) {
            return SmartBy.selector(By.xpath(selector));
        }
        else if (WebUtils.isImageSelector(selector)) {
            return SmartBy.selector(By.linkText(selector));
        }
        else {
            return SmartBy.cssSelector(selector);
        }
    }
}
