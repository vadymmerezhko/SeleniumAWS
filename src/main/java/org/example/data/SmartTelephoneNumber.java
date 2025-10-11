package org.example.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.CompareUtils;
import org.example.utils.ConvertUtils;
import org.example.utils.DataValidator;

import java.util.Objects;

import static org.example.constants.Settings.MAX_PHONE_NUMBER;
import static org.example.constants.Settings.MIN_PHONE_NUMBER;

/**
 * Smart phone number class.
 */
@Slf4j
public final class SmartTelephoneNumber extends SmartObject implements FormattedValue {

    @Getter
    private final Number number;
    @Getter
    private String format;

    /**
     * Parses number string to smart phone number.
     * @param phoneNumberString The number string.
     * @return The smart number.
     */
    public static SmartTelephoneNumber fromString(String phoneNumberString) {
        DataValidator.notBlank(phoneNumberString, "phoneNumberString");

        try {
            Number phoneNumber = ConvertUtils.phoneNumberStingToNumber(phoneNumberString);
            SmartTelephoneNumber smartNumber = new SmartTelephoneNumber(phoneNumber);
            smartNumber.format = phoneNumberString.replaceAll("[0-9]", "#");
            log.debug("""
                Number string converted to smart phone number.
                String: {}
                Number: {}
                Format: {}
                """.stripIndent(),
                phoneNumberString, phoneNumber, smartNumber.format);
            return smartNumber;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot parse number string to smart phone number.
                    Number string:
                    %s
                    """.stripIndent(),
                    phoneNumberString), e);
        }
    }

    /**
     * Creates smart phone number from phone number.
     * @param phoneNumber The phone number.
     * @return The smart number.
     */
    public static SmartTelephoneNumber fromNumber(Number phoneNumber) {
        DataValidator.notNull(phoneNumber, "phoneNumber");
        // The minimal 6 digits phone number
        DataValidator.min(phoneNumber, MIN_PHONE_NUMBER, "phoneNumber");
        // The maximal 15 digits phone number with extension to 5 digits
        DataValidator.max(phoneNumber, MAX_PHONE_NUMBER, "phoneNumber");

        try {
            String numberString = String.valueOf(phoneNumber);
            SmartTelephoneNumber smartNumber = new SmartTelephoneNumber(phoneNumber);
            smartNumber.format = ConvertUtils.numberStringToFormat(numberString);
            log.debug("""
                Number string converted to smart phone number.
                String: {}
                Number: {}
                Format: {}
                """.stripIndent(),
                    numberString, phoneNumber, smartNumber.format);
            return smartNumber;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot create smart phone number from Number.
                    Number string:
                    %s
                    """.stripIndent(),
                    phoneNumber), e);
        }
    }

    SmartTelephoneNumber(Number number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return ConvertUtils.phoneNumberFormattedString(number, format);
    }

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

            if (object instanceof SmartTelephoneNumber smartTelephoneNumber) {
                result = CompareUtils.compareNumbers(getNumber(), smartTelephoneNumber.getNumber());
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
