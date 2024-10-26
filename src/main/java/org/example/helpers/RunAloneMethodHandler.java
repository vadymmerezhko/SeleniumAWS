package org.example.helpers;

import lombok.extern.slf4j.Slf4j;
import org.example.annotations.RunAlone;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test and SmartElement methods handler.
 * Intercepts @Test and SmartElement methods to run @RunAlone
 * only when any other methods do not run or wait
 * for @RunAlone method completion.
 */
@Slf4j
public class RunAloneMethodHandler {
    private static final AtomicInteger runAloneCount = new AtomicInteger(0);

    private RunAloneMethodHandler() {}

    public static void beforeMethod(Method method) {
        DataValidationUtils.validateNotNull(method, "method");

        synchronized (MethodExecutionTracker.class) {

            log.debug("Before {} method tread count: {}",
                    method.getName(), MethodExecutionTracker.getRunningMethodsCount());
            // Wait until no other @RunAlone method is running
            while (runAloneCount.get() > 0) {
                try {
                    MethodExecutionTracker.class.wait();
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SmartRuntimeException(String.format(
                            "Method %s is interrupted while waiting for other methods to complete.",
                            method.getName()), e);
                }
            }
            // Check if the method has the @RunAlone annotation
            if (method.isAnnotationPresent(RunAlone.class)) {

                log.debug("Before @RunAlone {} method tread count: {}",
                        method.getName(), MethodExecutionTracker.getRunningMethodsCount());
                // Wait until no other method is running
                while (MethodExecutionTracker.getRunningMethodsCount() > 0) {
                    try {
                        MethodExecutionTracker.class.wait();
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new SmartRuntimeException(String.format(
                                "Method %s is interrupted while waiting for other methods to complete.",
                                method.getName()), e);
                    }
                }
                // Increment run alone counter for every nested @RunAlone method in the same thread
                runAloneCount.incrementAndGet();
                log.debug("After @RunAlone {} method tread count: {}",
                        method.getName(), MethodExecutionTracker.getRunningMethodsCount());
            }
        }
        long threadId = Thread.currentThread().threadId();
        // Increment the count for all methods with @RunAlone annotation
        MethodExecutionTracker.increment(threadId);
    }

    public static void afterMethod(Method method) {
        DataValidationUtils.validateNotNull(method, "method");

        synchronized (MethodExecutionTracker.class) {
            // Increment run alone counter for every nested @RunAlone method in the same thread
            runAloneCount.decrementAndGet();
            long threadId = Thread.currentThread().threadId();
            // Decrement the count for all methods with @RunAlone annotation
            MethodExecutionTracker.decrement(threadId);
            // Notify waiting threads if necessary
            MethodExecutionTracker.class.notifyAll();
            log.debug("After {} method tread count: {}",
                    method.getName(), MethodExecutionTracker.getRunningMethodsCount());
        }
    }
}
