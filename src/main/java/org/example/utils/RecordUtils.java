package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Record utils class.
 * Contains common methods to work with methods.
 */
public final class RecordUtils {

    private RecordUtils() {}

    /**
     * Converts record to JSON string.
     * @param record The record to convert.
     * @return The record JSON string.
     */
    public static String recordToString(Object record) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts JSON string to record object by record class.
     * @param jsonString The JSON string.
     * @param recordClass The record class.
     * @return The record object.
     */
    public static Object stringToRecord(String jsonString, Class<?> recordClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, recordClass);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException (e.getMessage());
        }
    }
}
