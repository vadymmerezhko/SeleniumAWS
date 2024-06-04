package org.example.driver.by;

import org.openqa.selenium.By;

/**
 * The By locator parser class.
 */
public class ByParser {
    private static final String CSS_TEMPLATE = "css=%s";
    private static final String XPATH_TEMPLATE = "xpath=%s";

    /**
     * Returns selector type name.
     * @param by The By locator instance.
     * @return The locator type name.
     */
    public static String getByType(By by) {
        String byString = by.toString();
        int delimiter1Index = byString.indexOf(".");
        int delimiter2Index = byString.indexOf(":");
        return byString.substring(delimiter1Index + 1, delimiter2Index);
    }

    /**
     * Returns selector value.
     * @param by The By locator instance.
     * @return The locator value.
     */
    public static String getByValue(By by) {
        String byString = by.toString();
        int delimiterIndex = byString.indexOf(":");
        return byString.substring(delimiterIndex + 1).trim();
    }

    /**
     * Convers By locator to string.
     * @param by The By locator instance.
     * @return The locator string value.
     */
    public static String getLocatorString(By by) {
        String byType = ByParser.getByType(by);
        String byValue = ByParser.getByValue(by);

        return switch (byType) {
            case ByTypes.CSS, ByTypes.TAG_NAME -> String.format(CSS_TEMPLATE, byValue);
            case ByTypes.XPATH -> String.format(XPATH_TEMPLATE, byValue);
            case ByTypes.ID -> String.format("#%s", byValue);
            case ByTypes.CLASS_NAME -> String.format(".%s]", byValue);
            case ByTypes.NAME -> String.format("*[name='%s']", byValue);
            case ByTypes.LINK_TEXT -> String.format("//a[text()='%s']", byValue);
            case ByTypes.PARTIAL_LINK_TEXT -> String.format("//a[contains(.,'%s')]", byValue);
            default -> throw new RuntimeException(String.format("Wrong locator type: %s", byType));
        };
    }
}
