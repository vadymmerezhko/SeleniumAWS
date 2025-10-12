package com.smarte2e.data;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.utils.ConvertUtils;
import com.smarte2e.utils.DataValidator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Smart number class.
 */
@Slf4j
public final class SmartNumber extends SmartObject implements FormattedValue {

    @Getter
    private Number number;
    @Getter @Setter
    private String format;
    private ULocale locale;

    /**
     * Parses number string to smart number.
     * @param numberString The number string.
     * @return The smart number.
     */
    public static SmartNumber fromString(String numberString) {
        DataValidator.notBlank(numberString, "numberString");

        try {
            SmartNumber smartNumber = new SmartNumber(numberString.trim());
            log.debug("""
                Number string converted to smart number.
                String: {}
                Number: {}
                Format: {}
                """.stripIndent(),
                numberString,
                smartNumber.getNumber(),
                 smartNumber.getFormat());
            return smartNumber;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot parse number string to smart number.
                    Number string: %s
                    """.stripIndent(),
                    numberString), e);
        }
    }

    /**
     * Parses locale number string to smart number
     * with different encoding by locale.
     * @param numberString The number string.
     * @param locale The locale
     * @return The smart number.
     */
    public static SmartNumber fromStringByLocale(String numberString, ULocale locale) {
        DataValidator.notBlank(numberString, "numberString");
        DataValidator.notNull(locale, "locale");

        try {
            // Convert locale string to standard Java number string
            String format = ConvertUtils.localeNumberStringToFormat(numberString, locale);
            numberString = ConvertUtils.localeStringToNumberString(numberString, locale);
            SmartNumber smartNumber = new SmartNumber(numberString.trim());
            smartNumber.setFormat(format);
            smartNumber.locale = locale;
            log.debug("""
                Number string converted to smart number.
                String: {}
                Number: {}
                Format: {}
                """.stripIndent(),
                    numberString,
                    smartNumber.getNumber(),
                    smartNumber.getFormat());
            return smartNumber;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot parse number string to smart number.
                    Number string: %s
                    """.stripIndent(),
                    numberString), e);
        }
    }

    /**
     * Converts country - language specific number string to a number.
     * It throws an exception if it cannot converts the string to a number.
     * @param numberString The country - language specific number string.
     * @return The number or null.
     */
    public static Number toNumber(String numberString) {
        DataValidator.notBlank(numberString, "numberString");

        List<ULocale> failedLocales = new ArrayList<>();
        // Iterate over all available ULocales

        for (ULocale uLocale : ULocale.getAvailableLocales()) {
            try {
                // Create a NumberFormat object for the current locale
                NumberFormat numberFormat = NumberFormat.getInstance(uLocale);
                // Attempt to parse the number string
                Number number = numberFormat.parse(numberString);
                System.out.println("Parsed successfully with locale: " + uLocale);
                return number;
            }
            catch (ParseException e) {
                // Collect failed locales
                failedLocales.add(uLocale);
                // Continue to the next locale if parsing fails
            }
        }

        // If no locale could parse the string, print failed attempts
        System.out.println("Failed to parse with these locales: " + failedLocales);
        throw new IllegalArgumentException("Cannot parse the number string: " + numberString);
    }

    SmartNumber(String numberString) {
        this.format = ConvertUtils.numberStringToFormat(numberString);
        setValue(ConvertUtils.stringToNumber(numberString));
        log.debug("""
                Smart number onject is created.
                Number: {}
                Format: {}
                """.stripIndent(),
                number, format);
    }

    /**
     * Sets number value but don't update number format.
     * @param value The value object.
     */
    public void setValue(Number value) {
        number = value;
    }

    /**
     * Converts smart number to formatted number string.
     * @return The formatted number string.
     */
    public String toFormattedString() {
        String numberString;

        if (locale != null) {
            numberString = ConvertUtils.numberToLocaleString(number, locale);
        }
        else {
            DecimalFormat formatter = new DecimalFormat(format);
            numberString = formatter.format(number);
        }
        log.debug("""
                Smart number converted to string.
                Number: {}
                Format: {}
                String: {}
                """.stripIndent(),
                number, format, numberString);
        return numberString;
    }

    /**
     * Converts smart number to number string.
     * @return The number string.
     */
    @Override
    public String toString() {
        return String.valueOf(number);
    }

    /**
     * Compares this smart number with other object.
     * @param object The object.
     * @return The true/false result.
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            log.debug("Smart number equals() called. The actual smart number object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart number equals() called. The actual is the same object as expected.");
            return true;
        }
        try {
            boolean result = false;

            if (object instanceof SmartNumber smartNumber) {
                BigDecimal expected = new BigDecimal(String.valueOf(number));
                BigDecimal actual = new BigDecimal(String.valueOf(smartNumber.getNumber()));
                result = expected.compareTo(actual) == 0 &&
                        format.equals(smartNumber.format);
            }
            log.debug("""
                    Smart number equals() called.
                    Expected: {}
                    Actual: {}
                    Result: {}
                    """.stripIndent(),
                    number, object, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Smart number %s method equals() failed for object %s.",
                    this, object), e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, format);
    }
}
