package org.example.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.utils.DataValidator;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;

/**
 /**
 * RunAlone @Test method listener.
 * Intercepts @Test methods with @RunAlone annotation
 * to run them only when any other methods do not run or wait
 * for @RunAlone method completion.
 */
@Slf4j
public class RunAloneTestListener implements IInvokedMethodListener {

    /**
     * Test method listener.
     * Intercepts @Test methods to run @RunAlone
     * only when any other methods do not run or wait
     * for @RunAlone method completion.
     */
    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        DataValidator.notNull(invokedMethod, "invokedMethod");

        Method method = invokedMethod.getTestMethod().getConstructorOrMethod().getMethod();
        log.debug("Before invocation of @Test {} method.", method.getName());
        RunAloneMethodHandler.beforeMethod(method);
    }

    /**
     * Runs after invocation of @Test method.
     * @param invokedMethod The invoked method.
     * @param testResult The test result.
     */
    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        DataValidator.notNull(invokedMethod, "invokedMethod");

        Method method = invokedMethod.getTestMethod().getConstructorOrMethod().getMethod();
        log.debug("After invocation of @Test {} method.", method.getName());
        RunAloneMethodHandler.afterMethod(method);
    }
}