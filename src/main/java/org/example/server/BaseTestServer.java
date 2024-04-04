package org.example.server;

import org.example.data.MethodInput;
import org.example.rmi.RmiClient;
import org.example.utils.AwsManager;
import org.example.utils.RecordUtils;

import java.lang.reflect.Method;

import static org.example.constants.Settings.AWS_LAMBDA_FUNCTION_ARN;

public class BaseTestServer {

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
        return RecordUtils.stringToRecord(
                RmiClient.invokeMethod(
                        testInputToMethodInputString(methodName, testInput)),
                        testOutputClass);
    }

    protected Object invokeLambdaFunction(String methodName, Object testInput, Class<?> testOutputClass) {
        return RecordUtils.stringToRecord(
                AwsManager.invokeLambdaFunction(
                        AWS_LAMBDA_FUNCTION_ARN,
                        testInputToMethodInputString(methodName, testInput)),
                testOutputClass);
    }

    protected String testInputToMethodInputString(String methodName, Object testInput) {
        return  RecordUtils.recordToString(
                new MethodInput(
                        methodName,
                        testInput.getClass().getName(),
                        RecordUtils.recordToString(testInput)));
    }
}
