package org.example.utils;

import org.example.drivers.wrappers.RobustWebElement;
import org.example.exceptions.DataValidationException;
import org.openqa.selenium.WebElement;

public final class DataValidationUtils {

    private DataValidationUtils() {}

    /**
     * Validates that data value is not null.
     * @param value The data value.
     * @param dataName The data name.
     * @param element The web element (optional).
     */
    public static void validateNotNull(String value, String dataName, WebElement element) {
        if (value == null) {
            handleError(String.format("%s has NULL value.", dataName), element);
        }
    }

    /**
     * Validates that data value is not empty.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotEmpty(String value, String dataName, WebElement element) {
        validateNotNull(value, dataName, element);
        if (value.isEmpty()) {
            handleError(String.format("%s has empty value.", dataName), element);
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotBlank(String value, String dataName, WebElement element) {
        validateNotNull(value, dataName, element);
        if (value.trim().isEmpty()) {
            handleError(String.format("%s has blank value: '%s'", dataName, value), element);
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotMultiline(String value, String dataName, WebElement element) {
        validateNotNull(value, dataName, element);
        if (value.trim().contains("\n")) {
            handleError(String.format("%s has multiline value: '%s'", dataName, value), element);
        }
    }

    /**
     * Validates that data value has correct color format like '#FF0088'.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateColorFormat(String value, String dataName, WebElement element) {
        validateNotNull(value, dataName, element);
        if (!value.matches("^#(?:[0-9a-fA-F]{3}){1,2}$")) {
            handleError(String.format("%s has invalid color format: '%s'", dataName, value), element);
        }
    }

    /**
     * Validates that data value has correct date format like '05/23/1970'.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateMmDdYyyyDateValue(String value, String dataName, WebElement element) {
        validateNotNull(value, dataName, element);
        if (!value.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
            handleError(String.format("%s has invalid date format: '%s'", dataName, value), element);
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
        if (value < from || value > to) {
            handleError(String.format("%s has invalid [%d:%d] range value: %d",
                    dataName, from, to, value), element);
        }
    }

    private static void handleError(String errorMessage, WebElement element) {
        if (element instanceof RobustWebElement) {
            ((RobustWebElement) element).handleElement(element);
        }
        throw new DataValidationException(errorMessage);
    }
}
