package org.example.server;

import org.example.data.MethodInput;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;

public class TestServerLambda extends BaseTestServerLambda implements TestServerInterface {
    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {
        String testInputJsonString = testInputToString(testInput);
        MethodInput methodInput = new MethodInput(
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                SignUpTestInput.class.getName(),
                testInputJsonString);

        String testOutputJsonString = invokeLambdaFunction(methodInput);
        return (SignUpTestResult)stringToTestResult(testOutputJsonString, SignUpTestResult.class);
    }
}