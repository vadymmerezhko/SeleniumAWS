package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConverterUtils;
import org.example.utils.DataValidationUtils;

/**
 * Smart value class.
 */
@Slf4j
public class SmartValue {
    private static final String KEYWORD_PLACEHOLDER = "#KEYWORD#";
    private static final String STRING_TYPE = "String";
    private String valueString;
    private String typeName;
    private String valueTemplate;
    private String keywordString;
    private Object value;
    private Object keyword;

    public SmartValue(String valueString) {
        this.valueString = valueString;
        valueTemplate = valueString;
        typeName = STRING_TYPE;
    }

    public SmartValue(String valueString, String keywordString) {
        this.valueString = valueString;
        this.keywordString = keywordString;
        valueTemplate = valueString;
        typeName = getTypeName(valueString);
    }

    public void setValue(Object value) {
        this.value = value;
        valueString = ConverterUtils.objectToSting(value);
        valueTemplate = valueString;
        typeName = getTypeName(value);
    }

    public Object getValue() {
        replaceKeywordPlaceholderIfDefined();
        return value;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
        value = valueString;
        valueTemplate = valueString;
    }

    public String getValueString() {
        replaceKeywordPlaceholderIfDefined();
        return valueString;
    }

    public void setKeyword(Object keyword) {
        this.keyword = keyword;
        keywordString = ConverterUtils.objectToSting(keyword);
    }

    public Object getKeyword() {
        return keyword;
    }

    public void setKeywordString(String keywordString) {
        this.keywordString = keywordString;
        String keywordTypeName = getTypeName(keyword);
        keyword = ConverterUtils.stringToObject(keywordTypeName, keywordString);
    }

    public String getTypeName() {
        return typeName;
    }

    public String getKeywordString() {
        return keywordString;
    }

    private void replaceKeywordPlaceholderIfDefined() {
        if (keywordString != null) {
            if (valueTemplate.contains(KEYWORD_PLACEHOLDER)) {
                valueString = valueTemplate.replace(KEYWORD_PLACEHOLDER, keywordString);
                value = ConverterUtils.stringToObject(typeName, valueString);
            }
        }
    }

    private String getTypeName(Object object) {
        return object.getClass().getSimpleName();
    }

    void validateKeyword() {
        if (keywordString != null && keywordString.isEmpty()) {
            throw new SmartRuntimeException("Keyword string is empty.");
        }
        else if (keywordString != null && keyword == null) {
            throw new SmartRuntimeException(String.format(
                    "Keyword object is null when keyword string in not null.\nKeyword: '%s'.", keywordString));
        }
        else if (keyword != null && keywordString == null) {
            throw new SmartRuntimeException(String.format(
                    "Keyword string is null when keyword string.\nKeyword: '%s'.", keyword));
        }
        else if (keywordString != null && valueContainsMoreThanOneKeyword()) {
            throw new SmartRuntimeException(String.format(
                    "Value string has more than one keyword string.\nValue:  %s\nKeyword: %s.",
                    valueString, keywordString));
        }
        else if (keywordString != null && valueDoesNotContainKeyword()) {
            throw new SmartRuntimeException(String.format(
                    "Value string does not contain keyword string.\nValue:  %s\nKeyword: %s.",
                    valueString, keywordString));
        }
    }

    private boolean valueContainsMoreThanOneKeyword() {
        return numberOfKeywordsInValue() > 1;
    }

    private boolean valueDoesNotContainKeyword() {
        return numberOfKeywordsInValue() == 0;
    }

    private void validateValue() {
        DataValidationUtils.validateNotNull(value, "value");
        DataValidationUtils.validateNotNull(valueString, "valueString");

        if (keywordString != null) {
            if (valueContainsMoreThanOneKeyword()) {
                throw new SmartRuntimeException(String.format(
                        "String value '%s' contains more that one keyword '%s'.",
                        valueString, keywordString));
            }
            else if (valueDoesNotContainKeyword()) {
                throw new SmartRuntimeException(String.format(
                        "String value '%s' does not contain keyword '%s'.",
                        valueString, keywordString));
            }
        }
    }

    public int numberOfKeywordsInValue() {
        if (valueString == null || keywordString == null || valueString.isEmpty() || keywordString.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;

        while ((index = valueString.indexOf(keywordString, index)) != -1) {
            count++;
            index += keywordString.length();
        }
        return count;
    }

    private boolean valueTemplateContainsKeywordPlaceholder() {
        return valueTemplate.contains(KEYWORD_PLACEHOLDER);
    }
}
