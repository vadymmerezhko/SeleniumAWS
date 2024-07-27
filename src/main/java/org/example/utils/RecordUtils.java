package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;

/**
 * Record utils class.
 * Contains common methods to work with methods.
 */
@Slf4j
public final class RecordUtils {

    private RecordUtils() {}

    /**
     * Converts record to JSON string.
     * @param record The record to convert.
     * @return The record JSON string.
     */
    public static String recordToString(Object record) {
        DataValidationUtils.validateNotNull(record, "record");

        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(record);
            log.debug("Record object {} converted to JSON string: {}", record,jsonString);
            return jsonString;
        } catch (JsonProcessingException e) {
            throw new SmartRuntimeException("Failed to convert record to sting.", e);
        }
    }

    /**
     * Converts JSON string to record object by record class.
     * @param jsonString The JSON string.
     * @param recordClass The record class.
     * @return The record object.
     */
    public static Object stringToRecord(String jsonString, Class<?> recordClass) {
        DataValidationUtils.validateNotBlank(jsonString, "jsonString");
        DataValidationUtils.validateNotNull(recordClass, "recordClass");

        ObjectMapper mapper = new ObjectMapper();
        try {
            Object record = mapper.readValue(jsonString, recordClass);
            log.debug("JSON string {} converted to record object: {}", jsonString, record);
            return record;
        }
        catch (JsonProcessingException e) {
            throw new SmartRuntimeException("Failed to convert string to record.", e);
        }
    }
}
