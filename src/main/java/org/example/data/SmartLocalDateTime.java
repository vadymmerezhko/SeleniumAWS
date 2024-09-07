package org.example.data;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConverterUtils;
import org.example.utils.DataValidationUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
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
public final class SmartLocalDateTime implements SmartTemporal,
        Temporal, TemporalAdjuster, ChronoLocalDateTime<LocalDate>, Serializable {
    @Delegate
    private final LocalDateTime localDateTime;
    private final String format;

    /**
     * Parses date string to smart local date time by its format.
     * @param dateString The local time format.
     * @return The smart local date time.
     */
    public static SmartLocalDateTime parseLocalDateTime(String dateString) {
        SmartDate smartDate = ConverterUtils.stringToSmartDate(dateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        SmartLocalDateTime smartLocalDateTime = new SmartLocalDateTime(localDateTime, smartDate.getFormat());
        log.debug("String '{}' parsed to smart local date time {} with date format '{}'",
                dateString, smartDate, smartDate.getFormat());
        return smartLocalDateTime;
    }

    /**
     * Constructs smart local date wrapper from local date and its format.
     * @param localDateTime The date.
     * @param format The date format.
     */
    public SmartLocalDateTime(LocalDateTime localDateTime, String format) {
        DataValidationUtils.validateNotNull(localDateTime, "localDate");
        DataValidationUtils.validateNotBlank(format, "format");
        this.localDateTime = localDateTime;
        this.format = format;
        log.debug("Smart local date time {} is created with date format '{}'.",
                this, format);
    }

    /**
     * Get date format.
     * @return The date format.
     */
    public LocalDateTime getLocalDateTime() {
        log.debug("Returned local date: {}", localDateTime);
        return localDateTime;
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
     * Converts smart local date time to date.
     * @return The date.
     */
    @Override
    public Date toDate() {
        Date date = ConverterUtils.localDateTimeToDate(localDateTime);
        log.debug("Smart local date time {} converted to date: {}", localDateTime, date);
        return date;
    }

    @Override
    public String toString() {
        try {
            String dataString = ConverterUtils.localDateTimeToString(localDateTime, format);
            log.debug("Smart local date time converted to string '{}' with date format '{}'.",
                    dataString, format);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart local date time %s to string.", this));
        }
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart local date time equals() called. The actual smart date time object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart local date time equals() called. The actual is the same object as expected.");
            return true;
        }
        try {
            boolean result;
            LocalDateTime actualLocalDateTime = ConverterUtils.objectToObject(
                    SmartType.fromClass(LocalDateTime.class), object);
            result = localDateTime.equals(actualLocalDateTime);
            log.debug("""
                    Smart local date time equals() called.
                    Expected: {}
                    Actual: {}
                    Result: {}
                    """.stripIndent(),
                    this, actualLocalDateTime, result);
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
