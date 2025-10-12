package com.smarte2e.data;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.utils.ConvertUtils;
import com.smarte2e.utils.DataValidator;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Date;
import java.util.Objects;

/**
 * Smart local date class.
 * Derived from Date class.
 */
@Slf4j
public final class SmartLocalDate extends SmartObject implements SmartTemporal,
        Temporal, TemporalAdjuster, ChronoLocalDate, Serializable {
    @Delegate
    private final LocalDate localDate;
    private final String format;

    /**
     * Parses date string to smart local date by its format.
     * @param dateString The local time format.
     * @return The smart local date.
     */
    public static SmartLocalDate fromString(String dateString) {
        SmartDate smartDate = ConvertUtils.stringToSmartDate(dateString);
        LocalDate localDate = ConvertUtils.dateToLocalDate(smartDate);
        String dateFormat = ConvertUtils.dateTimeFormatToDateFormat(smartDate.getFormat());
        SmartLocalDate smartLocalDate = new SmartLocalDate(localDate, dateFormat);
        log.debug("String '{}' parsed to smart local date {} with date format '{}'",
                dateString, smartDate, smartDate.getFormat());
        return smartLocalDate;
    }

    /**
     * Creates smart local date instance from milliseconds.
     * @param milliseconds The milliseconds.
     * @return The smart local date instance.
     */
    public static SmartLocalDate fromMilliseconds(long milliseconds) {
        SmartDate smartDate = SmartDate.fromMilliseconds(milliseconds);
        SmartLocalDate smartLocalDate = smartDate.toSmartLocalDate();
        log.debug("Smart local date is created from {} milliseconds: {}",
                milliseconds, smartLocalDate);
        return smartLocalDate;
    }

    /**
     * Constructs smart local date wrapper from local date and its format.
     * @param localDate The date.
     * @param format The date format.
     */
    public SmartLocalDate(LocalDate localDate, String format) {
        DataValidator.notNull(localDate, "localDate");
        DataValidator.notBlank(format, "format");
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
            String dateString = ConvertUtils.localDateToString(localDate, format);
            log.debug("Smart local date converted to string '{}' with date format '{}'.",
                    dateString, format);
            return dateString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart local date %s to string.", localDate));
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
            LocalDate actaulLocalDate = ConvertUtils.objectToObject(
                    SmartType.fromClass(LocalDate.class), object);
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
        return Objects.hash(localDate, format);
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
        Date date = ConvertUtils.localDateToDate(localDate);
        log.debug("Smart local date {} converted to date: {}", localDate, date);
        return date;
    }
}
