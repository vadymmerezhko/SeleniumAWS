package org.example.data;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConverterUtils;
import org.example.utils.DataValidationUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.Objects;

/**
 * Smart local date time class.
 * Derived from Date class.
 */
@Slf4j
public final class SmartLocalTime implements SmartTemporal,
        Temporal, TemporalAdjuster, Comparable<LocalTime>, Serializable {
    @Delegate
    private final LocalTime localTime;
    private final String format;

    /**
     * Parses date string to smart local time by its format.
     * @param dateString The local time format.
     * @return The smart local time.
     */
    public static SmartLocalTime parseLocalTime(String dateString) {
        SmartDate smartDate = ConverterUtils.stringToSmartDate(dateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
        LocalTime localTime = LocalTime.parse(dateString, formatter);
        SmartLocalTime smartLocalTime = new SmartLocalTime(localTime, smartDate.getFormat());
        log.debug("String '{}' parsed to smart local date time {} with date format '{}'",
                dateString, smartDate, smartDate.getFormat());
        return smartLocalTime;
    }

    /**
     * Constructs smart local date wrapper from local date and its format.
     * @param localTime The date.
     * @param format The date format.
     */
    public SmartLocalTime(LocalTime localTime, String format) {
        DataValidationUtils.validateNotNull(localTime, "localDate");
        DataValidationUtils.validateNotBlank(format, "dateFormat");
        this.localTime = localTime;
        this.format = format;
        log.debug("Smart local date time {} is created with date format '{}'.",
                this, format);
    }

    /**
     * Get date format.
     * @return The date format.
     */
    public LocalTime getLocalTime() {
        log.debug("Returned local time: {}", localTime);
        return localTime;
    }

    /**
     * Get local date format.
     * @return The date format.
     */
    @Override
    public String getFormat() {
        log.debug("Returned date format: {}", format);
        return format;
    }

    /**
     * Converts smart local time to date.
     * @return The date.
     */
    @Override
    public Date toDate() {
        Date date = ConverterUtils.localTimeToDate(localTime);
        log.debug("Local time {} converted to date: {}", localTime, date);
        return date;
    }

    @Override
    public String toString() {
        try {
            String dataString = ConverterUtils.localTimeToString(localTime, format);
            log.debug("Smart local time converted to string '{}' with date format '{}'.",
                    dataString, format);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart local time %s to string.", this));
        }
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart local time equals() called. The actual smart date time object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart local time equals() called. The actual is the same object as expected.");
            return true;
        }
        try {
            LocalTime actualLocalTime = ConverterUtils.objectToObject(
                    new SmartType(LocalTime.class), object);
            boolean result = localTime.equals(actualLocalTime);
            log.debug("""
                    Smart local time equals() called.
                    Expected: {}
                    Actual: {}
                    Result: {}
                    """.stripIndent(),
                    this, actualLocalTime, result);
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
