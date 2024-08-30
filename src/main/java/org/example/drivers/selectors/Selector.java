package org.example.drivers.selectors;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.openqa.selenium.By;

import static org.example.constants.Settings.SELECTOR_DELIMITER;

/**
 * Selector class.
 */
@Slf4j
public class Selector {
    private final SelectorType type;
    private final SmartValue value = new SmartValue();

    /**
     * Converts selector type:value string to selector object.
     * @param selectorString The type:value string.
     * @return The selector object.
     */
    public static Selector fromString(String selectorString) {
        DataValidationUtils.validateNotBlank(selectorString, "selectorString");
        return fromString(selectorString, null);
    }

    /**
     * Converts selector type:value string and keyword to selector object.
     * @param typeAndValue The type:value string.
     * @param keyword The keyword.
     * @return The selector object.
     */
    public static Selector fromString(String typeAndValue, Object keyword) {
        DataValidationUtils.validateNotBlank(typeAndValue, "typeAndValue");

        try {
            int delimiterIndex = typeAndValue.indexOf(SELECTOR_DELIMITER);

            if (delimiterIndex == -1) {
                throw new SmartRuntimeException(String.format(
                        "Wrong selector type and value string format: %s", typeAndValue));
            }
            String typeString = typeAndValue.substring(0, delimiterIndex);
            String valueString = typeAndValue.substring(delimiterIndex + 1);
            SelectorType selectorType = SelectorType.fromString(typeString);
            Selector selector = new Selector(selectorType, valueString, keyword);
            log.debug("""
                    Element type:value selector string with keyword parsed to selector.
                    String: {}
                    Keyword: {}
                    Selector: {}
                    """.stripIndent(),
                    typeAndValue, selector);
            return selector;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot parse selector type:value string with keyword to selector object.
                    String: %s
                    Keyword: %s
                    """.stripIndent(),
                    typeAndValue, keyword), e);
        }
    }

    /**
     * Creates selector object with type, string value and keyword object.
     * @param type The type.
     * @param value The string value.
     * @param keyword The keyword object.
     *
     */
    public Selector(SelectorType type, String value, Object keyword) {
        DataValidationUtils.validateNotBlank(value, "value");
        this.type = type;
        this.value.setValue(value);
        setKeyword(keyword);
        log.debug("""
                Selector object is created.
                Type: {}
                Value: {}
                Keyword: {}
                """.stripIndent(),
                type, value, keyword);
    }

    /**
     * Returns selector type.
     * @return The type.
     */
    public SelectorType getType() {
        log.debug("Selector type is returned: {}", type);
        return type;
    }

    /**
     * Sets keyword.
     * @param keyword The keyword.
     */
    public void setKeyword(Object keyword) {
        value.setKeyword(keyword);
        log.debug("Selector keyword is set: {}", keyword);
    }

    /**
     * Gets keyword.
     * @return The keyword.
     */
    public Object getKeyword() {
        Object keyword = value.getKeyword();
        log.debug("Selector keyword is returned: {}", keyword);
        return keyword;
    }

    /**
     * Converts selector to value string.
     * @return The value.
     */
    public String totValueString() {
        log.debug("Selector converted to value string: {}", value);
        return value.toString();
    }

    /**
     * Converts selector value template string.
     * @return The selector template string.
     */
    public String toValueTemplate() {
        String template = value.getValueTemplate();
        log.debug("Selector converted to value template: {}", template);
        return template;
    }

    @Override
    public String toString() {
        String string = String.format("%s%s%s", type, SELECTOR_DELIMITER, value);
        log.debug("Selector converted to string: {}", string);
        return string;
    }
}
