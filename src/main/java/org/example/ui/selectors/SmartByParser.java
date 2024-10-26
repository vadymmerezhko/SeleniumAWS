package org.example.ui.selectors;

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
    private static final String CSS_FORMAT = "%s[%s='%s']";
    private static final String XML_TEXT_EQUALS_FORMAT = "//%s[text()='%s']";
    private static final String XML_TEXT_CONTAINS_FORMAT = "//%s[contains(text(), '%s']";

    /**
     * Converts native By selector to smart By selector.
     * @param nativeBy The native By seelctor.
     * @return The smart By selector.
     */
    public static SmartBy fromBy(By nativeBy) {
        DataValidationUtils.validateNotNull(nativeBy, "nativeBy");
        SmartBy smartBy;

        try {
            if (nativeBy instanceof SmartBy wrappedSmartBy) {
                 smartBy = wrappedSmartBy;
            }
            else {
                String byString = nativeBy.toString();
                int delimiter1Index = byString.indexOf(".");
                int delimiter2Index = byString.indexOf(":");
                SmartByType type = SmartByType.fromString(byString.substring(
                        delimiter1Index + 1, delimiter2Index));

                if (type == SmartByType.LINK_TEXT && WebUtils.isPngImageSelector(byString)) {
                    type = SmartByType.IMAGE;
                }
                smartBy = new SmartBy(type, nativeBy);
            }
            log.debug("""
                    By selector converted to smart By selector.
                    By selector: {}
                    Smart By:
                    {}
                    """.stripIndent(),
                    nativeBy, smartBy);
            return smartBy;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get smart By type from By selector.
                    By selector: %s
                    """.stripIndent(),
                    nativeBy));
        }
    }

    /**
     * Converts selector value to smart By.
     * @param selectorValue The selector value;
     * @return The smart by;
     */
    public static SmartBy fromSelectorValue(String selectorValue) {
        DataValidationUtils.validateNotBlank(selectorValue, "selectorValue");
        selectorValue = selectorValue.trim();
        SmartBy smartBy;

        try {
            if (WebUtils.isXpath(selectorValue)) {
                smartBy = new SmartBy(SmartBy.xpath(selectorValue));
            }
            else if (WebUtils.isPngImageSelector(selectorValue)) {
                smartBy = new SmartBy(SmartBy.linkText(selectorValue));
            }
            else {
                smartBy = new SmartBy(SmartBy.cssSelector(selectorValue));
            }
            log.debug("""
                    Selector value string converted to smart By selector.
                    Selector value: {}
                    Smart By: {}
                    """.stripIndent(),
                    selectorValue, smartBy);
            return smartBy;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get smart By from selector value string.
                    Selector value: %s
                    """.stripIndent(),
                    selectorValue));
        }
    }

    public static String selectorValueFromBy(By nativeBy) {

        try {
            if (nativeBy instanceof SmartBy smartBy) {
                nativeBy = smartBy.getBy();
            }
            if (nativeBy == null) {
                return null;
            }
            SmartByType type = SmartByParser.fromBy(nativeBy).getSmartByType();
            String byString = nativeBy.toString();
            int delimiter2Index = byString.indexOf(":");
            String value = byString.substring(delimiter2Index + 1).trim();
            String selectorValue;

            switch(type) {
                case CLASS_NAME ->  selectorValue = String.format(CSS_FORMAT, "*", "class", value);
                case ID ->  selectorValue = String.format(CSS_FORMAT, "*", "id", value);
                case LINK_TEXT ->  selectorValue = String.format(XML_TEXT_EQUALS_FORMAT, "a", value);
                case NAME ->  selectorValue = String.format(CSS_FORMAT, "*", "name", value);
                case PARTIAL_LINK_TEXT ->  selectorValue = String.format(XML_TEXT_CONTAINS_FORMAT, "a", value);
                case CSS, TAG_NAME, XPATH, URL, IMAGE ->  selectorValue = value;
                default -> throw new SmartRuntimeException(String.format(
                        "Cannot convert %s smart by to selector string.", type));
            }
            log.debug("""
                    By selector converted to selector value string.
                    By selector: {}
                    Selector value: {}
                    """.stripIndent(),
                    nativeBy, selectorValue);
            return selectorValue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get selector value string from By selector.
                    By selector: %s
                    """.stripIndent(),
                    nativeBy));
        }
    }
}
