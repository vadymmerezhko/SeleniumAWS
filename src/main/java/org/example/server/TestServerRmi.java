package org.example.server;

import org.example.data.MethodInput;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.server.TestServerInterface;

public class TestServerRmi extends BaseTestServerRmi implements TestServerInterface {
    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {
        String testInputJsonString = testInputToString(testInput);
        MethodInput methodInput = new MethodInput(
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                SignUpTestInput.class.getName(),
                testInputJsonString);
        String testOutputJsonString = invokeRmiMethod(methodInput);
        return (SignUpTestResult)stringToTestResult(testOutputJsonString, SignUpTestResult.class);
    }
}
