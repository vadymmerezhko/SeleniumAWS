package org.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.utils.WebUtils;

import java.io.StringWriter;
import java.io.PrintWriter;

@Slf4j
public class BaseSmartException extends RuntimeException {

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

            String confirmMessage = String.format("""
                    EXCEPTION
                    
                    Click OK to continue.
                    Or click CANCEL to exit the test.
                    Message:
                    %s
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
