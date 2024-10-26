package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    // TODO: add unit tests
    public static String getMethodName() {
        long threadId = Thread.currentThread().threadId();
        return methodMap.get(threadId);
    }

    /**
     * Return the parameter value.
     * @return The parameter value.
     * @param <P> The parameter type.
     */
    // TODO: add unit tests
    public static <P> P getParameterValue() {
        long threadId = Thread.currentThread().threadId();
        return (P) parameterMap.get(threadId);
    }

    /**
     * Throws "Method not implemented" exception by method name.
     * @param methodName The method name.
     */
    public static void throwMethodNotImplementedException(String methodName) {
        DataValidationUtils.validateFullMethodName(methodName, "methodName");
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
        performMethod(action, null, null, fix, methodName,
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
        performMethod(action, parameter, null, fix, methodName,
                waitMilliseconds, waitTimeoutMilliseconds);
    }

    /**
     * Performs bi-consumer method action with retries after exception.
     * This method has P parameter and return void.
     * If all reties caused exception then it throws the last exception.
     * @param action The action to perform.
     * @param parameter1 The first method parameter.
     * @param parameter2 The second method parameter.
     * @param fix The fix method that has Exception parameter.
     * Optional - can be null.
     * @param methodName - The method name for logging.
     * @param waitMilliseconds - The wait milliseconds.
     * @param waitTimeoutMilliseconds - The wait timeout milliseconds.
     */
    public static <P1, P2> void performBiConsumerMethod(
            BiConsumer<P1, P2> action,
            P1 parameter1,
            P2 parameter2,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        DataValidationUtils.validateNotNull(parameter1, "parameter1");
        DataValidationUtils.validateNotNull(parameter2, "parameter2");
        performMethod(action, parameter1, parameter2, fix, methodName,
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
        return performMethod(action, null, null, fix, methodName,
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
        return performMethod(action, parameter, null, fix, methodName,
                waitMilliseconds, waitTimeoutMilliseconds);
    }

    /**
     * Gets the *.java file path corresponding to a given Class<?>.
     * @param clazz The Class<?> object.
     * @return The path to the .java file if found,
     * or throws an exception otherwise.
     */
    public static String getJavaFilePathFromClass(Class<?> clazz) {
        try {
            // Convert the package to a directory structure (e.g., com/example/MyClass -> com/example/MyClass.java)
            String filePath = "src/test/java/" + clazz.getName().replace('.', File.separatorChar) + ".java";
            File sourceFile = new File(filePath);

            if (sourceFile.exists()) {
                return sourceFile.getAbsolutePath();
            }
            else {
                throw new RuntimeException(String.format(
                        "Cannot get *.java file path from class: %s", clazz.getName()));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get *.java file path from class: %s", clazz.getName()), e);
        }
    }

    /**
     * Gets the class name and method name from the stack trace at a specific depth index.
     * @param depthIndex The depth index in the stack trace:
     * 0 - is the current method
     * 1 - is the caller, etc.
     * @return The full method name.
     */
    public static String getFullMethodNameFromStackTrace(int depthIndex) {
        DataValidationUtils.validateMin(depthIndex, 0, "depthIndex");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Adjust the depthIndex to account for the getStackTrace() call itself and this method
        int adjustedIndex = depthIndex + 2;

        if (adjustedIndex >= 0 && adjustedIndex < stackTrace.length) {
            StackTraceElement element = stackTrace[adjustedIndex];
            String className = element.getClassName();
            String methodName = element.getMethodName();
            return String.format("%s.%s", className, methodName);
        }
        else {
            throw new RuntimeException(String.format(
                    "Cannot get class and method string from the stack trace by depth index: %d",
                    depthIndex));
        }
    }

    /**
     * Gets the Class<?> from a fully qualified method name.
     * @param fullMethodName The fully qualified method name
     * (e.g., "com.example.MyClass.myMethod").
     * @return The Class<?> the class,
     * or throws an exception if the class cannot be found.
     */
    public static Class<?> getClassFromFullMethodName(String fullMethodName) {
        DataValidationUtils.validateFullMethodName(fullMethodName, "fullMethodName");

        try {
            // Extract the class name
            String className = getClassNameNameFromFullMethodName(fullMethodName);

            // Load the class by its name
            return Class.forName(className);
        }
        catch (Exception e) {
            // Handle the case where the class cannot be found
            System.err.println("Class not found: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the full class name from a fully qualified method name.
     * @param fullMethodName The fully qualified method name
     * (e.g., "com.example.MyClass.myMethod").
     * @return The simple method name (e.g., "myMethod").
     */
    public static String getClassNameNameFromFullMethodName(String fullMethodName) {
        DataValidationUtils.validateFullMethodName(fullMethodName, "fullMethodName");

        // Find the last dot to isolate the method name
        int lastDotIndex = fullMethodName.lastIndexOf('.');

        if (lastDotIndex == -1) {
            throw new SmartRuntimeException("Invalid full method name: " + fullMethodName);
        }
        // Extract the simple method name (everything after the last dot)
        return fullMethodName.substring(0, lastDotIndex);
    }

    /**
     * Gets the method name from a fully qualified method name.
     * @param methodFullName The fully qualified method name
     * (e.g., "com.example.MyClass.myMethod").
     * @return The simple method name (e.g., "myMethod").
     */
    public static String getMethodNameFromFullMethodName(String methodFullName) {
        DataValidationUtils.validateFullMethodName(methodFullName, "methodFullName");

        // Find the last dot to isolate the method name
        int lastDotIndex = methodFullName.lastIndexOf('.');

        if (lastDotIndex == -1) {
            throw new SmartRuntimeException("Invalid full method name: " + methodFullName);
        }
        // Extract the simple method name (everything after the last dot)
        return methodFullName.substring(lastDotIndex + 1);
    }

    /**
     * Gets the parameter name based on parent full method name,
     * method name and parameter index from a class.
     * @param clazz The clas.
     * @param parentFullMethodName The parent full method name.
     * @param methodName The method name.
     * @param parameterIndex The index of the parameter (0-based).
     * @return The parameter name at the specified index, or null if not found.
     */
    public static String getMethodParameterName(Class<?> clazz,
                                                String parentFullMethodName,
                                                String methodName,
                                                int parameterIndex) {
        DataValidationUtils.validateNotNull(clazz, "class");
        DataValidationUtils.validateSimpleMethodName(methodName, "methodName");
        DataValidationUtils.validateMin(parameterIndex, 0, "parameterIndex");

        // Get parent method source code
        String sourceCode = getMethodSourceCode(clazz, parentFullMethodName);

        // Pattern to match the method call with any number of parameters
        Pattern pattern = Pattern.compile(methodName + "\\s*\\(([^)]*)\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sourceCode);

        // Search for the method call
        while (matcher.find()) {
            // Get the parameters inside the parentheses
            String parameters = matcher.group(1);

            // Split the parameters by comma and trim them
            String[] parameterArray = parameters.split(",");
            for (int i = 0; i < parameterArray.length; i++) {
                parameterArray[i] = parameterArray[i].trim();
            }
            // Check if the requested parameter index exists
            if (parameterIndex < parameterArray.length) {
                String parameterName =  parameterArray[parameterIndex].trim();
                log.debug("""
                        Method parameter is found.
                        Class: {}
                        Method: {}
                        Parameter index: {}
                        Parameter name: {}
                        """.stripIndent(),
                        clazz.getName(), methodName,
                        parameterIndex, parameterName);
                return parameterName;
            }
        }
        throw new RuntimeException(String.format("""
                Cannot get method parameter name from class.
                Class: %s
                Method name: %s
                Parameter index: %d
                """.stripIndent(),
                clazz.getName(),
                methodName,
                parameterIndex));
    }

    /**
     * Gets the parameter name if the method has exactly one parameter,
     * and its type.
     * @param clazz The class.
     * @param methodName The method name.
     * @return The parameter name, otherwise throws an exception.
     */
    public static String getSingleMethodParameterName(Class<?> clazz,
                                                      String methodName,
                                                      String parameterType) {
        DataValidationUtils.validateNotNull(clazz, "class");
        DataValidationUtils.validateFullMethodName(methodName, "methodName");

        // Get the file path for the class
        String filePath = getJavaFilePathFromClass(clazz);
        // Read the class content from the file
        String content = FileSystemUtils.readFile(filePath);

        // Pattern to match the method call with exactly one parameter
        Pattern pattern = Pattern.compile(methodName + "\\s*\\((\\w+)\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        // Search for the method call with one parameter
        while (matcher.find()) {
            // Get the parameter inside the parentheses
            String parameter = matcher.group(1).trim();

            // Return the parameter if it's not empty and declared with parameter type
            if (!parameter.isEmpty()) {

                // Check if the parameter is declared as a JSONArray
                if (isJSONArrayDeclared(content, parameterType, parameter)) {
                    log.debug("""
                            Method parameter name found.
                            Class: {}
                            Method: {}
                            Parameter type: {}
                            Parameter name: {}
                            """.stripIndent(),
                            clazz.getName(), methodName,
                            parameterType, parameter);
                    return parameter;
                }
                else {
                    throw new RuntimeException(String.format("""
                            Cannot get parameter name.
                            Class: %s
                            Method name: %s
                            Parameter type: %s
                            """.stripIndent(),
                            clazz.getName(), methodName, parameterType));
                }
            }
        }
        throw new RuntimeException(String.format("""
                Cannot get method parameter name.
                Class: %s
                Method name: %s
                Parameter type: %s
                """.stripIndent(),
                clazz.getName(), methodName, parameterType));
    }

    /**
     * Checks if a given parameter by its type.
     * @param content The class content.
     * @param parameterType The parameter type
     * @param parameter The parameter name to check.
     * @return true if the parameter is declared as a JSONArray, false otherwise.
     */
    private static boolean isJSONArrayDeclared(String content, String parameterType, String parameter) {
        // Refined pattern to capture more variations of declaration (like 'JSONArray bookmarks = new JSONArray();')
        Pattern declarationPattern = Pattern.compile("\\b" + parameterType + "\\s+" + parameter + "\\s*=\\s*new\\s+JSONArray\\s*\\(\\s*\\)\\s*;");
        Matcher declarationMatcher = declarationPattern.matcher(content);

        boolean isDeclared = declarationMatcher.find();
        log.debug("Is parameter '{}' declared as {}?: {}", parameter, parameterType, isDeclared);
        return isDeclared;
    }

    /**
     * Returns the source code of a clas by full class name.
     * @param fullClassName The full class name.
     * @return The source code.
     */
    public static String getClassSourceCode(String fullClassName) {
        DataValidationUtils.validateFullClassName(fullClassName, "fullClassName");

        try {
            // Get the Java file path corresponding to the class
            // Read the content of the Java file
            Class<?> targetClass = Class.forName(fullClassName);
            String filePath = getJavaFilePathFromClass(targetClass);
            String sourceCode = FileSystemUtils.readFile(filePath);
            log.debug("{} class source code is returned:\n{}", fullClassName, sourceCode);
            return sourceCode;
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("""
                    Cannot get class source code by full class name.
                    Class: %s
                    """.stripIndent(),
                    fullClassName));
        }
    }

    /**
     * Returns the source code of a method in a given class
     * based on the method's full name.
     * @param targetClass The class where the method is located.
     * @param fullMethodName The full method name
     * (e.g., "myMethod").
     * @return The source code.
     */
    public static String getMethodSourceCode(Class<?> targetClass, String fullMethodName) {
        DataValidationUtils.validateNotNull(targetClass, "targetClass");
        DataValidationUtils.validateFullMethodName(fullMethodName, "fullMethodName");

        try {
            // Get the Java file path corresponding to the class
            // Read the content of the Java file
            String sourceCode = getClassSourceCode(targetClass.getName());
            String methodName = getMethodNameFromFullMethodName(fullMethodName);
            // Basic pattern to match a method signature and its body
            // This pattern looks for the method name and captures
            // the method body including nested blocks
            Pattern pattern = Pattern.compile(
                    methodName + "\\s*\\([^)]*\\)\\s*\\{([\\s\\S]*?)\\n\\}",
                    Pattern.DOTALL
            );
            Matcher matcher = pattern.matcher(sourceCode);

            // If the method is found, return its source code
            if (matcher.find()) {
                String methodSourceCode = matcher.group(0);
                log.debug("{} method source code is returned:\n{}", methodName, sourceCode);
                return methodSourceCode;
            }
            throw new RuntimeException(String.format(
                    "Cannot get method '%s' source code in class '%s'.",
                    fullMethodName, targetClass.getName()));
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("""
                    Cannot get method source code.
                    Class: %s
                    Method name: %s
                    """.stripIndent(),
                    targetClass.getName(), fullMethodName));
        }
    }

    /**
     * Returns the object name from source code
     * by code line number.
     * based on the method's full name.
     * @param sourceCode The source code.
     * @param lineNumber The line number.
     * ATTENTION: It finds only the first object name in camel style
     * that goes before '=' sign in the code number string or
     * in the previous not empty and not blank string.
     * So it will not work well if other object is assigned with "="
     * sign in that line before the target one.
     * @return The source code.
     */
    public static String getObjectNameFromSourceCode(String sourceCode, int lineNumber) {
        DataValidationUtils.validateNotBlank(sourceCode, "sourceCode");
        DataValidationUtils.validateMin(lineNumber, 1, "lineNumber");

        try {
            String[] lines = TextUtils.splitMultilineString(sourceCode);
            // Validate the line number is within the given source code
            DataValidationUtils.validateMax(lineNumber, lines.length, "lineNumber");

            // Start with the specified line
            String line = lines[lineNumber - 1].trim();

            // Check if the line contains "="; if not, move to the previous non-empty line
            while (!line.contains("=") && lineNumber > 1) {
                line = lines[--lineNumber - 1].trim();
            }

            // Regex to find the variable name before "="
            String regex = "\\b([a-zA-Z][a-zA-Z0-9]*)\\s*=";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String objectName = matcher.group(1); // Return the variable name before "="
                log.debug(String.format("""
                        Object name is found in the source code code line.
                        Line number: {}
                        Class name: {}
                        Object name: {}
                        Source code:
                        {}
                        """.stripIndent(),
                        lineNumber, objectName, sourceCode));
                return objectName;
            }
            throw new SmartRuntimeException(String.format("""
                    Cannot get object name from the source code line.
                    Line number: %d
                    Source code:
                    %s
                    """.stripIndent(),
                    lineNumber, sourceCode));
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get object name from source code line.
                    Line number: %d
                    Source code:
                    %s
                    """.stripIndent(),
                    lineNumber, sourceCode), e);
        }
    }

    /**
     * Returns the class name of the declaring class
     * that instantiated the field object by the class package name.
     * This method should be called from the constructor
     * of the field object.
     * ATTENTION: There are some RESTRICTIONS:
     * - The declaring class and its field class cannot have the same package name.
     * @param packageName The class package name.
     * @return The class name of the declaring class.
     */
    public static String getDeclaringClassName(String packageName) {
        DataValidationUtils.validatePackageName(packageName, "packageName");
        // Get the current stack trace
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        // Traverse the stack trace to find the first record with target package name.
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            String className = stackTraceElement.getClassName();
            String actualPackageName = ConvertUtils.fullClassNameToPackageName(className);

            // Search for the first class name by its package name
            if (packageName.startsWith(actualPackageName)) {
                // Return the first class name with the given package name or with derived package name
                log.debug("The field's declaring class name is returned: {}", className);
                return className;
            }
        }
        throw new RuntimeException("Declaring class name not found.");
    }

    /**
     * Returns the invocation code line number from the stack trace by
     * declaring class name where a method or constructor was called from.
     * ATTENTION: There is a RESTRICTION:
     * - It returns code line number for the first class name found in the
     * stack trace. So it may not work as expected for recursive invocations.
     * @param declaringClassName The declaring class name
     * where the invocation was done.
     * @return The code line number.
     */
    public static int getInvocationCodeLineNumber(String declaringClassName) {
        DataValidationUtils.validatePackageName(declaringClassName, "packageName");
        // Get the current stack trace
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        // Traverse the stack trace to find the first record with target package name.
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            String className = stackTraceElement.getClassName();

            // Search for the first class name by its package name
            if (className.equals(declaringClassName)) {
                // Return the line number of the first class name found in the stack trace
                int lineNumber = stackTraceElement.getLineNumber();
                log.debug("""
                        The source code line number of the method or constructor invocation is returned
                        by the declaring class name.
                        The class name: {}
                        The line number: {}
                        """.stripIndent(),
                        declaringClassName, lineNumber);
                return lineNumber;
            }
        }
        throw new RuntimeException(String.format("""
                        The source code line number of the method or constructor invocation
                        is not found by the declaring class name.
                        The class name: {}
                        """.stripIndent(),
                        declaringClassName));
    }

    /**
     * Returns the line number of the code where this method was called.
     * @return The line number of the calling code.
     */
    public static int getCurrentInvocationCodeLineNumber() {
        // Get the current stack trace
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // stackTrace[0] is getStackTrace, stackTrace[1] is getInvocationCodeLineNumber, stackTrace[2] is the caller
        StackTraceElement invokingElement = stackTrace[2];  // The calling method’s stack trace element
        int lineNumber = invokingElement.getLineNumber();   // Retrieve the line number of the calling code
        log.debug("Invocation code line number is returned: " + lineNumber); // For debugging purposes
        return lineNumber;
    }

    /**
     * Returns the name of the first field instance
     * that holds the given field object in the declaring class.
     * ATTENTION: There are some RESTRICTIONS:
     * - The declaring class and its field class cannot have the same package name.
     * - The field class cannot be a subclass of the declaring class.
     * - The field class should have not default equals() method.
     * @param fieldObject The field object whose field name is needed.
     * @return The field name of the field object.
     */
    public static String getFieldInstanceName(String declaringClassName, Object fieldObject) {
        DataValidationUtils.validateFullClassName(declaringClassName, "declaringClassName");
        DataValidationUtils.validateNotNull(fieldObject, "fieldObject");

        try {
            // Check if this subclass name
            if (declaringClassName.contains("$")) {
                throw new RuntimeException(String.format(
                        "Cannot get field name for sub class: %s.",
                        declaringClassName));
            }
            Class<?> declaringClass = Class.forName(declaringClassName);
            // Crete expected object from its class
            Object expectedClassObject = declaringClass.getDeclaredConstructor().newInstance();

            // Iterate through the fields of the declaring class
            for (Field field : declaringClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                // Get the expected field by its name
                Field expectedField = declaringClass.getDeclaredField(fieldName);
                expectedField.setAccessible(true);
                Object expectedFieldObject = expectedField.get(expectedClassObject);

                // Check if the field's value matches the provided field object type
                if (expectedFieldObject.equals(fieldObject)) {
                    // Return the name of the field instance
                    log.debug("{} class field name is returned: {}",
                            declaringClass.getName(), fieldName);
                    return fieldName;
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot retrieve field instance name for the given object.", e);
        }
        throw new SmartRuntimeException("Field instance name not found for the given object.");
    }

    /**
     * Returns the simple class name from the full class name string.
     * Example: for input "java.util.ArrayList", it returns "ArrayList".
     * @param fullClassName The full class name (including the package).
     * @return The simple class name (class name without the package).
     */
    public static String getSimpleClassName(String fullClassName) {
        DataValidationUtils.validateFullClassName(fullClassName, "fullClassName");

        String simpleClassName = fullClassName;
        // Find the last occurrence of '.' to get the simple class name
        int lastDotIndex = fullClassName.lastIndexOf('.');

        // Return the substring after the last '.' or the entire string if no '.' is found
        if (lastDotIndex != -1) {
            simpleClassName = fullClassName.substring(lastDotIndex + 1);
        }
        log.debug("Simple class name {} is returned from full class name: {}",
                simpleClassName, fullClassName);
        return simpleClassName;
    }

    private static <P1, P2, R> R performMethod(
            Object action,
            P1 parameter1,
            P1 parameter2,
            Consumer<Exception> fix,
            String methodName,
            int waitMilliseconds,
            int waitTimeoutMilliseconds) {
        Exception lastException = null;
        long threadId = Thread.currentThread().threadId();

        methodMap.put(threadId, methodName);
        if (parameter1 != null) {
            parameterMap.put(threadId, parameter1);
        }
        DataValidationUtils.validateNotNull(action, "action");
        DataValidationUtils.validateSimpleMethodName(methodName, "methodName");
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
                    }
                    else if (action instanceof Consumer<?>) {
                        ((Consumer<P1>) action).accept(parameter1);
                        return null;
                    }
                    else if (action instanceof BiConsumer<?, ?>) {
                        ((BiConsumer<P1, P2>) action).accept((P1) parameter1, (P2) parameter2);
                        return null;
                    }
                    else if (action instanceof Supplier<?>) {
                        R returnValue = ((Supplier<R>) action).get();
                        log.debug("Method {} return value is {}", methodName, returnValue);
                        return returnValue;
                    }
                    else if (action instanceof Function<?, ?>) {
                        R returnValue = ((Function<P1, R>) (action)).apply(parameter1);
                        log.debug("Method {} with parameter {} return value is {}",
                                methodName, parameter1, returnValue);
                        return returnValue;
                    }
                }
                catch (Exception e) {

                    if (fix != null) {
                        fix.accept(e);
                    }
                    TimerUtils.waitMilliSeconds(waitMilliseconds);
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
