package org.example.data;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConverterUtils;
import org.example.utils.DataValidationUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.Objects;

/**
 * Smart local date class.
 * Derived from Date class.
 */
@Slf4j
public final class SmartLocalDate implements SmartDateInterface,
        Temporal, TemporalAdjuster, ChronoLocalDate, Serializable {
    @Delegate
    private final LocalDate localDate;
    private final String format;

    /**
     * Parses date string to smart local date by its format.
     * @param dateString The local time format.
     * @return The smart local date.
     */
    public static SmartLocalDate parseLocalDate(String dateString) {
        SmartDate smartDate = ConverterUtils.stringToSmartDate(dateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        SmartLocalDate smartLocalDate = new SmartLocalDate(localDate, smartDate.getFormat());
        log.debug("String '{}' parsed to smart local date {} with date format '{}'",
                dateString, smartDate, smartDate.getFormat());
        return smartLocalDate;
    }

    /**
     * Constructs smart local date wrapper from local date and its format.
     * @param localDate The date.
     * @param format The date format.
     */
    public SmartLocalDate(LocalDate localDate, String format) {
        DataValidationUtils.validateNotNull(localDate, "localDate");
        DataValidationUtils.validateNotBlank(format, "format");
        this.localDate = localDate;
        this.format = format;
        log.debug("Smart local date {} is created with date format '{}'.",
                this, format);
    }

    /**
     * Get date format.
     * @return The date format.
     */
    public LocalDate getLocalDate() {
        log.debug("Returned local date: {}", localDate);
        return localDate;
    }

    @Override
    public String toString() {
        try {
            String dataString = ConverterUtils.localDateToString(localDate, format);
            log.debug("Smart local date converted to string '{}' with date format '{}'.",
                    dataString, format);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart local date %s to string.", this));
        }
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart local date equals() called. The actual smart date time object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart local date equals() called. The actual is the same object as expected.");
            return true;
        }
        try {
            LocalDate actaulLocalDate = ConverterUtils.objectToLocalDate(object);
            boolean result = localDate.equals(actaulLocalDate);
            log.debug("""
                    Smart local date equals() called.
                    Expected: {}
                    Actual: {}
                    Result: {}
                    """.stripIndent(),
                    this, actaulLocalDate, result);
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

    /**
     * Gets date format.
     * @return The date format.
     */
    @Override
    public String getFormat() {
        log.debug("Returned date format: {}", format);
        return format;
    }

    /**
     * Converts smart local date to date.
     * @return The date.
     */
    @Override
    public Date toDate() {
        Date date = ConverterUtils.localDateToDate(localDate);
        log.debug("Smart local date {} converted to date: {}", localDate, date);
        return date;
    }
}
