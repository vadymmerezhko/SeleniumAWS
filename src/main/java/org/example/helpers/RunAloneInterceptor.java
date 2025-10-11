package org.example.helpers;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.example.utils.DataValidator;

import java.lang.reflect.Method;

@Slf4j
/**
 * SmartElement methods interceptor.
 * Intercepts SmartElement methods to run @RunAlone
 * only when any other methods do not run or wait
 * for @RunAlone method completion.
 */
public class RunAloneInterceptor {

    private final Object target;

    /**
     * RunAlone interceptor constructor.
     * @param target The SmartElement object
     * which methods to be intercepted.
     */
    public RunAloneInterceptor(Object target) {
        this.target = target;
    }

    /**
     * Intercepts all SmartElement methods.
     * @param proxy The proxy.
     * @param args The method arguments.
     * @param method The method.
     * @return The method result.
     * @throws Throwable Throws an exception in case of error.
     */
    @RuntimeType
    public Object intercept(@This Object proxy, @AllArguments Object[] args, @Origin Method method) throws Throwable {
        DataValidator.notNull(method, "method");

        // Before method invocation logic
        beforeInvocation(method);
        Object result;
        try {
            // Invoke the actual WebElement method
            result = method.invoke(target, args);
        }
        finally {
            // After method invocation logic
            afterInvocation(method);
        }
        return result;
    }

    /**
     * Gets the real object wrapped by interceptor.
     * @return The native object.
     */
    // TODO: add unit tests
    public Object getNativeObject() {
        log.debug("Real smart element object is returned: {}", target);
        return target;
    }

    /**
     * This method is called before every SmartElement method.
     * @param method The method.
     */
    private void beforeInvocation(Method method) {
        DataValidator.notNull(method, "method");

        // Call logic to run before the method
        log.debug("Before SmartElement method: {}", method.getName());
        RunAloneMethodHandler.beforeMethod(method);
    }

    private void afterInvocation(Method method) {
        DataValidator.notNull(method, "method");

        // Call logic to run after the method
        log.debug("After SmartElement method: {}", method.getName());
        RunAloneMethodHandler.afterMethod(method);
    }
}
