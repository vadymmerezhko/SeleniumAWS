package org.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.utils.WebUtils;

import java.io.StringWriter;
import java.io.PrintWriter;

@Slf4j
public class BaseSmartException extends RuntimeException {
    private static final int MAX_MESSAGE_LENGTH = 300;

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
            String stackTrace = String.join(",", getStackTraceString());
            String message = String.format("Message: %s\nStack trace:\n%s",
                    getMessage(), stackTrace);

            if (message.length() > MAX_MESSAGE_LENGTH) {
                message = message.substring(0, MAX_MESSAGE_LENGTH);
            }
            String confirmMessage = String.format("""
                    EXCEPTION
                    
                    Message:
                    %s
                    ...
                    
                    Click OK to continue.
                    Or click CANCEL to exit the test.      
                    """.stripIndent(), message);

            if (!WebUtils.showConfirm(confirmMessage)) {
                log.info("User made hard system exit on exception confirm popup.");
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
