package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.LambdaInput;
import org.example.data.LambdaOutput;
import org.example.data.TestInput;
import org.example.data.TestResult;

public class TestServerLambda extends BaseTestServerLambda implements TestServerInterface {
    @Override    public TestResult signUp(TestInput testInput) {
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
                testInputJsonString);

        LambdaOutput lambdaOutput = invokeLambdaFunction(lambdaInput);
        String testOutputJsonString = lambdaOutput.outputData();

        if (testOutputJsonString.startsWith("AWS Lambda error")) {
            throw new RuntimeException(testOutputJsonString);
        }

        try {
            return mapper.readValue(testOutputJsonString, TestResult.class);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException (e.getMessage());
        }
    }
}
