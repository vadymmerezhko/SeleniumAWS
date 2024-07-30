package org.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.utils.ClassUtils;
import org.example.utils.WebUtils;

import java.io.StringWriter;
import java.io.PrintWriter;

@Slf4j
public class BaseSmartException extends RuntimeException {
    private static final int MAX_MESSAGE_LENGTH = 500;

    public BaseSmartException() {
        super();
        handleException();
    }

    public BaseSmartException(Throwable e) {
        super(e);
        handleException();
    }

    public BaseSmartException(String message) {
        super(message);
        handleException();
    }

    public BaseSmartException(String message, Throwable e) {
        super(message, e);
        handleException();
    }

    private void handleException() {
        if (Config.getInstance().getDebugMode()) {
            String methodName = ClassUtils.getMethodName();
            Object parameter = ClassUtils.getParameterValue();
            String stackTrace = String.join(",", getStackTraceString());
            String message = String.format("Message: %s\nStack trace:\n%s",
                    getMessage(), stackTrace);
            String header = "EXCEPTION\n\n";
            String footer = "\n\nClick OK to continue.\nOr click CANCEL to terminate the test.";

            if (message.length() > MAX_MESSAGE_LENGTH) {
                message = message.substring(0, MAX_MESSAGE_LENGTH);
            }
            if (ClassUtils.getMethodName() != null) {
                if (ClassUtils.getParameterValue() != null) {
                    String parameterValue = parameter.toString();
                    message = String.format(
                            "%sMethod: %s\nParameter: %s\n%s%s",
                            header, methodName, parameterValue, message, footer);
                } else {
                    message = String.format("%sMethod: %s\n%s%s",
                            header, methodName, message, footer);
                }
            }
            if (!WebUtils.showConfirm(message)) {
                log.error(
                    "\n///////////////////////////////////////////////////////////\n\n" +
                    "User made hard system exit on exception confirm popup.\n\n" +
                    "///////////////////////////////////////////////////////////");
                WebDriverFactory.hardSystemExit();
            }
        }
    }

    private String getStackTraceString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printStackTrace(pw);
        return sw.toString();
    }
}
