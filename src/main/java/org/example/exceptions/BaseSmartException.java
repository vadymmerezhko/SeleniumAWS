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
        log.error(String.format("""
                \n//////////////////////////////////////////////////////////////////////////
                EXCEPTION
                Message:
                %s
                Stack trace:
                %s
                //////////////////////////////////////////////////////////////////////////
                
                """.stripIndent(), getMessage(), getStackTraceString()));

        if (Config.getInstance().getDebugMode()) {
            String stackTrace = String.join(",", getStackTraceString());
            String message = String.format("Message: %s\nStack trace:\n%s",
                    getMessage(), stackTrace);
            String alertMessage = String.format("""
                    EXCEPTION
                    
                    Click OK to exit the test.
                    Message:
                    %s
                    """.stripIndent(), message);

            WebUtils.showAlert(alertMessage);
            log.error("""
                      \n//////////////////////////////////////////////////////////////////////////
                      Hard system exit after user closed EXCEPTION confirm popup.
                      //////////////////////////////////////////////////////////////////////////
                      
                      """.stripIndent());
            WebDriverFactory.hardSystemExit();
        }
    }

    private String getStackTraceString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printStackTrace(pw);
        return sw.toString();
    }
}
