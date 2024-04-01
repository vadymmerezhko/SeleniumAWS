package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.LambdaInput;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;

public class TestServerLambda extends BaseTestServerLambda implements TestServerInterface {
    @Override    public SignUpTestResult signUp(SignUpTestInput testInput) {
        ObjectMapper mapper = new ObjectMapper();
        String testInputJsonString;

        try {
            testInputJsonString = mapper.writeValueAsString(testInput);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        LambdaInput lambdaInput = new LambdaInput(
                Thread.currentThread().threadId(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                SignUpTestInput.class.getName(),
                testInputJsonString);

        String testOutputJsonString = invokeLambdaFunction(lambdaInput);

        if (testOutputJsonString.startsWith("AWS Lambda error")) {
            throw new RuntimeException(testOutputJsonString);
        }

        try {
            return mapper.readValue(testOutputJsonString, SignUpTestResult.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException (e.getMessage());
        }
    }
}
