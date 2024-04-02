package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.MethodInput;
import org.example.data.SignUpTestResult;

public class BaseTestServer {

    protected String methodInputToSting(MethodInput methodInput) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(methodInput);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String testInputToString(Object testInput) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(testInput);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object stringToTestResult(String testOutputJsonString, Class<?> testResultClass) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(testOutputJsonString, testResultClass);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException (e.getMessage());
        }
    }
}
