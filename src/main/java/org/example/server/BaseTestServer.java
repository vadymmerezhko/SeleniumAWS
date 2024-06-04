package org.example.server;

import org.example.data.MethodInput;
import org.example.rmi.RmiClient;
import org.example.utils.AwsManager;
import org.example.utils.RecordUtils;

import java.lang.reflect.Method;

import static org.example.constants.Settings.*;

/**
 * Base test server class.
 * Contains common functionality for all test classes.
 */
 class BaseTestServer {

    /**
     * Base test server constructor.
     */
    BaseTestServer() {}

    /**
     * Invokes remote method by its name with parameter class name and parameter JSON string.
     * @param methodName The method name.
     * @param paramClassName The parameter class name.
     * @param paramJsonString The parameter JSON string value.
     * @return The output JSON string value.
     */
    public String invokeMethod(String methodName, String paramClassName, String paramJsonString) {
        try {
            Class<?> paramClass = Class.forName(paramClassName);
            Method method = this.getClass().getMethod(methodName, paramClass);
            Object param = RecordUtils.stringToRecord(paramJsonString, paramClass);
            Object testResult = method.invoke(this, param);
            return RecordUtils.recordToString(testResult);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Object invokeRemoteMethod(String methodName, Object testInput, Class<?> testOutputClass) {
        String testOutputString = RmiClient.invokeMethod(testInputToMethodInputString(methodName, testInput));
        checkRequestHandlerError(testOutputString);
        System.out.println("Output Json:\n" + testOutputString);
        return RecordUtils.stringToRecord(testOutputString, testOutputClass);
    }

    protected Object invokeLambdaFunction(String methodName, Object testInput, Class<?> testOutputClass) {
        String testOutputString = AwsManager.invokeLambdaFunction(
                AWS_LAMBDA_FUNCTION_ARN,
                testInputToMethodInputString(methodName, testInput));
        checkRequestHandlerError(testOutputString);
        return RecordUtils.stringToRecord(testOutputString, testOutputClass);
    }

    protected String testInputToMethodInputString(String methodName, Object testInput) {
        return  RecordUtils.recordToString(
                new MethodInput(
                        methodName,
                        testInput.getClass().getName(),
                        RecordUtils.recordToString(testInput)));
    }

    private void checkRequestHandlerError(String methodOutput) {
        if (methodOutput.contains(REQUEST_HANDLER_ERROR_MSG)) {
            throw new RuntimeException(methodOutput);
        }
    }
}
