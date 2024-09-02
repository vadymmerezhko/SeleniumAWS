package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConverterUtils;
import org.example.utils.DataValidationUtils;

import java.util.Date;
import java.util.Objects;

/**
 * Smart date class.
 * Derived from Date class.
 */
@Slf4j
public final class SmartDate extends Date implements SmartTemporal {
    private final String format;

    public static SmartDate parseDate(String dateString) {
        SmartDate smartDate = ConverterUtils.stringToSmartDate(dateString);
        log.debug("String '{}' parsed to smart date {} with date format '{}'",
                dateString, smartDate, smartDate.format);
        return smartDate;
    }

    /**
     * Constructs smart date from date and its format.
     * @param date The date.
     * @param format The date format.
     */
    public SmartDate(Date date, String format) {
        super(date.getTime());
        DataValidationUtils.validateNotNull(date, "date");
        DataValidationUtils.validateNotBlank(format, "format");
        this.format = format;
        log.debug("Smart date {} is created with date format '{}'.",
                this, format);
    }

    /**
     * Constructs smart date from time milliseconds and its format.
     * @param timeMilliseconds The date.
     * @param format The date format.
     */
    public SmartDate(long timeMilliseconds, String format) {
        super(timeMilliseconds);
        DataValidationUtils.validateMin(timeMilliseconds, 0, "timeMilliseconds");
        DataValidationUtils.validateNotBlank(format, "dateFormat");
        this.format = format;
        log.debug("Smart date {} created from {} milliseconds with date format '{}'.",
                this, timeMilliseconds, format);
    }

    /**
     * Get date format.
     * @return The date format.
     */
    @Override
    public String getFormat() {
        log.debug("Returned date format: {}", format);
        return format;
    }

    /**
     * Converts smart date to date.
     * @return The date.
     */
    @Override
    public Date toDate() {
        Date date = this;
        log.debug("Smart date {} converted to date: {}", this, date);
        return this;
    }

    @Override
    public String toString() {
        try {
            String dataString = ConverterUtils.dateToString(this, format);
            log.debug("Smart date converted to string '{}' with date format '{}'.",
                    dataString, format);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart date %s to string.", this));
        }
    }

    /**
     * Converts to smart local date.
     * @return The smart local date.
     */
    public SmartLocalDate toSmartLocalDate() {
        try {
            SmartLocalDate smartLocalDate = ConverterUtils.stringToSmartLocalDate(toString());
            log.debug("Smart date {} converted to smart local date: {}", this, smartLocalDate);
            return smartLocalDate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart date %s to smart local date.", this));
        }
    }

    /**
     * Converts to smart local date time.
     * @return The smart local date time.
     */
    public SmartLocalDateTime toSmartLocalDateTime() {
        try {
            SmartLocalDateTime smartLocalDateTime = ConverterUtils.stringToSmartLocalDateTime(toString());
            log.debug("Smart date {} converted to smart local date time: {}", this, smartLocalDateTime);
            return smartLocalDateTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart date %s to smart local date time.", this));
        }
    }

    /**
     * Converts to smart local time.
     * @return The smart local time.
     */
    public SmartLocalTime toSmartLocalTime() {
        try {
            SmartLocalTime smartLocalTime = ConverterUtils.stringToSmartLocalTime(toString());
            log.debug("Smart date {} converted to smart local time: {}", this, smartLocalTime);
            return smartLocalTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart date %s to smart local time.", this));
        }
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart date equals() called. The actual smart date time object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart date equals() called. The actual is the same object as expected.");
            return true;
        }
        try {
            boolean result = super.equals(object);
            log.debug("""
                    Smart date equals() called.
                    Expected: {}
                    Actual: {}
                    Result: {}
                    """.stripIndent(),
                    this, object, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Smart date %s method equals() failed for object %s.",
                    this, object), e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this, format);
    }
}
