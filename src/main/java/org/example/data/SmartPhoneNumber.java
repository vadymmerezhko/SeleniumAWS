package org.example.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.CompareUtils;
import org.example.utils.ConvertUtils;

import java.util.Objects;

/**
 * Smart phone number class.
 */
@Slf4j
public final class SmartPhoneNumber extends SmartObject implements FormattedValue {

    @Getter
    private final Number number;
    @Getter
    private String format;

    /**
     * Parses number string to smart phone number.
     * @param numberString The number string.
     * @return The smart number.
     */
    public static SmartPhoneNumber fromString(String numberString) {
        try {
            Number phoneNumber = ConvertUtils.phoneNumberStingToNumber(numberString);
            SmartPhoneNumber smartNumber = new SmartPhoneNumber(phoneNumber);
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
                    Cannot parse number string to smart phone number.
                    Number string:
                    %s
                    """.stripIndent(),
                    numberString), e);
        }
    }

    SmartPhoneNumber(Number number) {
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

            if (object instanceof Number thatNumber) {
                result = CompareUtils.compareNumbers(number, thatNumber);
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
