package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The method manger class.
 */
@Slf4j
public final class ClassUtils {
    private  static final int MAX_RETRY_COUNT = 100;
    private  static final int MAX_WAIT_MILLISECONDS = 60 * 1000;

    private ClassUtils() {}
    private static final ConcurrentMap<Long, String> methodMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, Object> parameterMap = new ConcurrentHashMap<>();

    public static String getMethodName() {
        long threadId = Thread.currentThread().threadId();
        return methodMap.get(threadId);
    }

    public static <P> P getParameterValue() {
        long threadId = Thread.currentThread().threadId();
        return (P) parameterMap.get(threadId);
    }

    /**
     * Throws "Method not implemented" exception by method name.
     * @param methodName The method name.
     */
    public static void throwMethodNotImplementedException(String methodName) {
        DataValidationUtils.validateNotBlank(methodName, "methodName");
        throw new SmartRuntimeException(String.format("Method %s is not implemented.", methodName));
    }

    /**
     * Returns class field name or null if not found.
     * @param parentObject The parent (class) object.
     * @param fieldObject The field object.
     * @return The field object or null.
     */
    public static String getClassFieldName(Object parentObject, Object fieldObject) {
        DataValidationUtils.validateNotNull(parentObject, "parentObject");
        DataValidationUtils.validateNotNull(fieldObject, "fieldObject");

        if (parentObject == null || fieldObject == null) {
            log.debug("Field name is null for parent object {} and field object {}.",
                    parentObject, fieldObject);
            return null;
        }
        try {
            Field[] fields = parentObject.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldReference = field.get(parentObject);

                if (fieldReference == fieldObject) {
                    String fieldName = field.getName();
                    log.debug("Field name is {} for parent object {} and field object {}.",
                            fieldName, parentObject, fieldObject);
                    return fieldName;
                }
            }
        } catch (Exception e) {
            throw new SmartRuntimeException("Cannot get class field name.", e);
        }
        return null;
    }

    /**
     * Performs runnable method action with retries after exception.
     * This method doesn't have parameters and return void.
     * If all reties caused exception then it throws the last exception.
     * @param action The action to perform.
     * @param fix The fix method that has Exception parameter.
     *            Optional - can be null.
     * @param methodName - The method name for logging.
     * @param retryCount - The number of reties from 1 to 100.
     * @param waitMilliseconds - The wait milliseconds from 0 to 1 60000.
     */
    public static void performRunnableMethod(
            Runnable action,
            Consumer<Exception> fix,
            String methodName,
            int retryCount,
            int waitMilliseconds) {
        performMethod(action, null, fix, methodName, retryCount, waitMilliseconds);
    }

    /**
     * Performs consumer method action with retries after exception.
     * This method has P parameter and return void.
     * If all reties caused exception then it throws the last exception.
     * @param action The action to perform.
     * @param parameter The method parameter.
     * @param fix The fix method that has Exception parameter.
     *            Optional - can be null.
     * @param methodName - The method name for logging.
     * @param retryCount - The number of reties from 1 to 100.
     * @param waitMilliseconds - The wait milliseconds from 0 to 1 60000.
     */
    public static <P> void performConsumerMethod(
            Consumer<P> action,
            P parameter,
            Consumer<Exception> fix,
            String methodName,
            int retryCount,
            int waitMilliseconds) {
        DataValidationUtils.validateNotNull(parameter, "parameter");
        performMethod(action, parameter, fix, methodName, retryCount, waitMilliseconds);
    }

    /**
     * Performs supplier method action with retries after exception.
     * This method doesn't have parameters and returns R value.
     * If all reties caused exception then it throws the last exception.
     * @param action The action to perform.
     * @param fix The fix method that has Exception parameter.
     *            Optional - can be null.
     * @param methodName - The method name for logging.
     * @param retryCount - The number of reties from 1 to 100.
     * @param waitMilliseconds - The wait milliseconds before next try
     *                         from 0 to 1 60000.
     */
    public static <R> R performSupplierMethod(
            Supplier<R> action,
            Consumer<Exception> fix,
            String methodName,
            int retryCount,
            int waitMilliseconds) {
        return performMethod(action, null, fix, methodName, retryCount, waitMilliseconds);
    }

    /**
     * Performs function method action with retries after exception.
     * This method has P parameter and returns R value.
     * If all reties caused exception then it throws the last exception.
     * @param action The action to perform.
     * @param parameter The method parameter.
     * @param fix The fix method that has Exception parameter.
     *            Optional - can be null.
     * @param methodName - The method name for logging.
     * @param retryCount - The number of reties from 1 to 100.
     * @param waitMilliseconds - The wait milliseconds from 0 to 1 60000.
     */
    public static <P, R> R performFunctionMethod(
            Function<P, R> action,
            P parameter,
            Consumer<Exception> fix,
            String methodName,
            int retryCount,
            int waitMilliseconds) {
        DataValidationUtils.validateNotNull(parameter, "parameter");
        return performMethod(action, parameter, fix, methodName, retryCount, waitMilliseconds);
    }

    private static <P, R> R performMethod(
            Object action,
            P parameter,
            Consumer<Exception> fix,
            String methodName,
            int retryCount,
            int waitMilliseconds) {
        Exception lastException = null;
        long threadId = Thread.currentThread().threadId();

        methodMap.put(threadId, methodName);
        if (parameter != null) {
            parameterMap.put(threadId, parameter);
        }

        DataValidationUtils.validateNotNull(action, "action");
        DataValidationUtils.validateNotBlank(methodName, "methodName");
        DataValidationUtils.validateRange(retryCount, 1, MAX_RETRY_COUNT, "retryCount");
        DataValidationUtils.validateRange(waitMilliseconds,0, MAX_WAIT_MILLISECONDS, "waitMilliseconds");

        try {
            for (int i = 1; i <= retryCount; i++) {
                try {
                    if (action instanceof Runnable) {
                        ((Runnable) action).run();
                        return null;
                    } else if (action instanceof Consumer<?>) {
                        ((Consumer<P>) action).accept(parameter);
                        return null;
                    } else if (action instanceof Supplier<?>) {
                        R returnValue = ((Supplier<R>) action).get();
                        log.debug("Method {} return value is {}", methodName, returnValue);
                        return returnValue;
                    } else if (action instanceof Function<?, ?>) {
                        R returnValue = ((Function<P, R>) (action)).apply(parameter);
                        log.debug("Method {} with parameter {} return value is {}",
                                methodName, parameter, returnValue);
                        return returnValue;
                    }
                } catch (Exception e) {
                    if (fix != null) {
                        fix.accept(e);
                    }
                    WaiterUtils.waitMilliSeconds(waitMilliseconds);
                    log.debug("Method '{}' retry: {}.", methodName, i);
                    lastException = e;
                }
            }
            throw new SmartRuntimeException(String.format(
                    "Failed to run method '%s' after %d retries.",
                    methodName, retryCount), lastException);
        }
        finally {
            methodMap.remove(threadId);
            parameterMap.remove(threadId);
        }
    }
}
