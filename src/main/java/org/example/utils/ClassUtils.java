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

    private ClassUtils() {}
    private static final ConcurrentMap<Long, String> methodMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, Object> parameterMap = new ConcurrentHashMap<>();

    /**
     * Returns method name.
     * @return The method name.
     */
    public static String getMethodName() {
        long threadId = Thread.currentThread().threadId();
        return methodMap.get(threadId);
    }

    /**
     * Return the parameter value.
     * @return The parameter value.
     * @param <P> The parameter type.
     */
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
    public static String getObjectFieldName(Object parentObject, Object fieldObject) {
        DataValidationUtils.validateNotNull(parentObject, "parentObject");
        DataValidationUtils.validateNotNull(fieldObject, "fieldObject");

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
     * @param waitMilliseconds - The wait milliseconds.
     * @param waitTimeoutMilliseconds - The wait timeout milliseconds.
     */
    public static void performRunnableMethod(
            Runnable action,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        performMethod(action, null, fix, methodName, 
                waitMilliseconds, waitTimeoutMilliseconds);
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
     * @param waitMilliseconds - The wait milliseconds.
     * @param waitTimeoutMilliseconds - The wait timeout milliseconds.
     */
    public static <P> void performConsumerMethod(
            Consumer<P> action,
            P parameter,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        DataValidationUtils.validateNotNull(parameter, "parameter");
        performMethod(action, parameter, fix, methodName, 
                waitMilliseconds, waitTimeoutMilliseconds);
    }

    /**
     * Performs supplier method action with retries after exception.
     * This method doesn't have parameters and returns R value.
     * If all reties caused exception then it throws the last exception.
     * @param action The action to perform.
     * @param fix The fix method that has Exception parameter.
     *            Optional - can be null.
     * @param methodName - The method name for logging.
     * @param waitMilliseconds - The wait milliseconds before next try.
     * @param waitTimeoutMilliseconds - The wait timeout milliseconds.
     */
    public static <R> R performSupplierMethod(
            Supplier<R> action,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        return performMethod(action, null, fix, methodName,
                waitMilliseconds, waitTimeoutMilliseconds);
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
     * @param waitMilliseconds - The wait milliseconds.
     * @param waitTimeoutMilliseconds - The wait timeout milliseconds (from 1 second).
     */
    public static <P, R> R performFunctionMethod(
            Function<P, R> action,
            P parameter,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        DataValidationUtils.validateNotNull(parameter, "parameter");
        return performMethod(action, parameter, fix, methodName,
                waitMilliseconds, waitTimeoutMilliseconds);
    }

    private static <P, R> R performMethod(
            Object action,
            P parameter,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        Exception lastException = null;
        long threadId = Thread.currentThread().threadId();

        methodMap.put(threadId, methodName);
        if (parameter != null) {
            parameterMap.put(threadId, parameter);
        }
        DataValidationUtils.validateNotNull(action, "action");
        DataValidationUtils.validateNotBlank(methodName, "methodName");
        DataValidationUtils.validateMin(waitMilliseconds, 0,  "waitMilliseconds");
        DataValidationUtils.validateMin(waitTimeoutMilliseconds, 1, "waitTimeoutMilliseconds");

        int retryCount = 0;
        long startMilliseconds = System.currentTimeMillis();

        try {
            while (System.currentTimeMillis() - startMilliseconds < waitTimeoutMilliseconds) {
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
                    log.debug("Method '{}' try count: {}.", methodName, retryCount);
                    lastException = e;
                }
                retryCount++;
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
