package org.example.utils;

import org.example.exception.DataValidationException;
import org.example.factory.WebDriverFactory;
import org.openqa.selenium.WebElement;

public class DataValidator {

    /**
     * Validates that data value is not null.
     * @param value The data value.
     * @param dataName The data name.
     * @param element The web element (optional).
     */
    public static void validateNotNull(String value, String dataName, WebElement element) {
        highlightElement(element);
        if (value == null) {
            throw new DataValidationException(String.format("%s has NULL value.", dataName));
        }
    }

    /**
     * Validates that data value is not empty.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotEmpty(String value, String dataName, WebElement element) {
        highlightElement(element);
        validateNotNull(value, dataName, element);
        if (value.isEmpty()) {
            throw new DataValidationException(String.format("%s has empty value.", dataName));
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotBlank(String value, String dataName, WebElement element) {
        highlightElement(element);
        validateNotNull(value, dataName, element);
        if (value.trim().isEmpty()) {
            throw new DataValidationException(String.format("%s has blank value: '%s'", dataName, value));
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotMultiline(String value, String dataName, WebElement element) {
        highlightElement(element);
        validateNotNull(value, dataName, element);
        if (value.trim().contains("\n")) {
            throw new DataValidationException(String.format("%s has multiline value: '%s'", dataName, value));
        }
    }

    /**
     * Validates that data value has correct color format like '#FF0088'.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateColorFormat(String value, String dataName, WebElement element) {
        highlightElement(element);
        validateNotNull(value, dataName, element);
        if (!value.matches("^#(?:[0-9a-fA-F]{3}){1,2}$")) {
            throw new DataValidationException(String.format(
                    "%s has invalid color format: '%s'", dataName, value));
        }
    }

    /**
     * Validates that data value has correct date format like '05/23/1970'.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateMmDdYyyyDateValue(String value, String dataName, WebElement element) {
        highlightElement(element);
        validateNotNull(value, dataName, element);
        if (!value.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
            throw new DataValidationException(String.format(
                    "%s has invalid date format: '%s'", dataName, value));
        }
    }

    /**
     * Validates that numeric range is correct.
     * @param value The data value.
     * @param from The range beginning.
     * @param to The range ending.
     * @param dataName The data name.
     */
    public static void validateRange(long value, long from, long to, String dataName, WebElement element) {
        highlightElement(element);
        if (value < from || value > to) {
            throw new DataValidationException(String.format(
                    "%s has invalid [%d:%d] range value: %d", dataName, from, to, value));
        }
    }

    private static void highlightElement(WebElement element) {
        if (element != null) {
            WebDriverFactory.setCurrentElement(element);
        }
    }
}
