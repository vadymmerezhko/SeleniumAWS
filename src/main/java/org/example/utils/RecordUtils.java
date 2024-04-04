package org.example.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecordUtils {

    public static String recordToString(Object record) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stringToRecord(String testOutputJsonString, Class<?> recordClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(testOutputJsonString, recordClass);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException (e.getMessage());
        }
    }
}
