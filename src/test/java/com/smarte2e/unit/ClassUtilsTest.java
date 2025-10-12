package com.smarte2e.unit;

import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.exceptions.SmartValidationException;
import com.smarte2e.unit.supplemental.classes.SomeClass;
import com.smarte2e.unit.fields.SomeField;
import com.smarte2e.utils.ClassUtils;
import org.testng.annotations.*;
import org.testng.Assert;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.Function;

public class ClassUtilsTest {
    private static final String METHOD_NAME = "testMethod";

    @Test
    public void testPerformRunnableMethod_Success() {
        Runnable successfulRunnable = () -> System.out.println("Runnable ran successfully.");
        ClassUtils.performRunnableMethod(successfulRunnable, null, METHOD_NAME, 100, 1000);
        // If no exception is thrown, we consider the test passed.
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformRunnableMethod_Failure() {
        Runnable failingRunnable = () -> { throw new RuntimeException("Error"); };
        ClassUtils.performRunnableMethod(failingRunnable, null, METHOD_NAME, 100, 1000);
    }

    @Test
    public void testPerformConsumerMethod_Success() {
        Consumer<String> successfulConsumer = (param) -> System.out.println("Consumed " + param);
        ClassUtils.performConsumerMethod(successfulConsumer, "test", null, METHOD_NAME, 100, 1000);
        // If no exception is thrown, we consider the test passed.
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformConsumerMethod_Failure() {
        Consumer<String> failingConsumer = (param) -> { throw new RuntimeException("Error"); };
        ClassUtils.performConsumerMethod(failingConsumer, "test", null, METHOD_NAME, 100, 1000);
    }

    @Test
    public void testPerformSupplierMethod_Success() {
        Supplier<Integer> successfulSupplier = () -> 42;
        int result = ClassUtils.performSupplierMethod(successfulSupplier, null, METHOD_NAME, 100, 1000);
        Assert.assertEquals(result, 42);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformSupplierMethod_Failure() {
        Supplier<Integer> failingSupplier = () -> { throw new RuntimeException("Error"); };
        ClassUtils.performSupplierMethod(failingSupplier, null, METHOD_NAME, 100, 1000);
    }

    @Test
    public void testPerformFunctionMethod_Success() {
        Function<String, Integer> successfulFunction = String::length;
        int result = ClassUtils.performFunctionMethod(successfulFunction, "hello", null, METHOD_NAME, 100, 1000);
        Assert.assertEquals(result, 5);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformFunctionMethod_Failure() {
        Function<String, Integer> failingFunction = (input) -> { throw new RuntimeException("Error"); };
        ClassUtils.performFunctionMethod(failingFunction, "hello", null, METHOD_NAME, 100, 1000);
    }

    @Test
    public void testGetDeclaringClassNameFromValidField() {
        SomeClass someClassInstance = new SomeClass();
        SomeField someFieldInstance = someClassInstance.getSomeField();
        String declaringClassName = someFieldInstance.getDeclaringClassName();

        Assert.assertEquals(declaringClassName, SomeClass.class.getName());
    }

    @Test
    public void testGetFieldInstanceNameFromValidField() {
        SomeClass someClassInstance = new SomeClass();
        SomeField someFieldInstance = someClassInstance.getSomeField();
        String fieldName = someFieldInstance.getFieldName();

        Assert.assertEquals(fieldName, "someField");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetFieldInstanceNameFromNullFieldValue() {
        ClassUtils.getFieldInstanceName(getClass().getName(), null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetFieldInstanceNameFromFieldValueWithInvalidClassName() {
        ClassUtils.getFieldInstanceName("Invalid class name", new SomeField());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetFieldInstanceNameFromFieldValueWithNullClassName() {
        ClassUtils.getFieldInstanceName(null, new SomeField());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetFieldInstanceNameFromInvalidObject() {
        ClassUtils.getFieldInstanceName(getClass().getName(), new Object());
    }

    @Test
    public void testGetObjectNameInTheMiddleOfTheLineFromValidSourceCode() {
        String sourceCode = """
        public class ExampleClass {
            SomeClass myObject = new SomeClass();
            int anotherObject = 42;
        }
        """;
        int lineNumber = 2;
        String objectName = ClassUtils.getObjectNameFromSourceCode(
                sourceCode, lineNumber);
        Assert.assertEquals(objectName, "myObject");
    }

    @Test
    public void testGetObjectNameInTheMiddleOfTheLineFromValidSourceCodeWithEmptyLines() {
        String sourceCode = """
        
        public class ExampleClass {
        
            SomeClass myObject = new SomeClass();
            
            int anotherObject = 42;
        }
        
        """;
        int lineNumber = 4;
        String objectName = ClassUtils.getObjectNameFromSourceCode(
                sourceCode, lineNumber);
        Assert.assertEquals(objectName, "myObject");
    }

    @Test
    public void testGetObjectNameInTheNextLineFromValidSourceCode() {
        String sourceCode = """
        public class ExampleClass {
            SomeClass myObject =
                new SomeClass();
            int anotherObject = 42;
        }
        """;
        int lineNumber = 3;
        String objectName = ClassUtils.getObjectNameFromSourceCode(
                sourceCode, lineNumber);
        Assert.assertEquals(objectName, "myObject");
    }

    @Test
    public void testGetObjectNameInTheNextLineWithoutNewFromValidSourceCode() {
        String sourceCode = """
        public class ExampleClass {
            SomeClass myObject = new
                SomeClass();
            int anotherObject = 42;
        }
        """;
        int lineNumber = 3;
        String objectName = ClassUtils.getObjectNameFromSourceCode(
                sourceCode, lineNumber);
        Assert.assertEquals(objectName, "myObject");
    }

    @Test
    public void testGetObjectNameFromValidSourceCodeWithEmptyLines() {
        String sourceCode = """
        public class ExampleClass {
        
            SomeClass myObject = new
            
                SomeClass();
                
            int anotherObject = 42;
        }
        """;
        int lineNumber = 5;
        String objectName = ClassUtils.getObjectNameFromSourceCode(
                sourceCode, lineNumber);
        Assert.assertEquals(objectName, "myObject");
    }

    @Test
    public void testGetObjectNameFromValidSingleLineSourceCode() {
        String sourceCode = """
        public class ExampleClass { SomeClass myObject = new SomeClass(); int anotherObject = 42; }
        """;
        int lineNumber = 1;
        String objectName = ClassUtils.getObjectNameFromSourceCode(
                sourceCode, lineNumber);
        Assert.assertEquals(objectName, "myObject");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetObjectNameOfTheWrongLineNumberFromValidSourceCode() {
        String sourceCode = """
        public class ExampleClass {
            SomeClass myObject = new SomeClass();
            int anotherObject = 42;
        }
        """;
        int lineNumber = 1;
        ClassUtils.getObjectNameFromSourceCode(sourceCode, lineNumber);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetObjectNameOfTheTooBigLineNumberFromValidSourceCode() {
        String sourceCode = """
        public class ExampleClass {
            SomeClass myObject = new SomeClass();
            int anotherObject = 42;
        }
        """;
        int lineNumber = 6;
        ClassUtils.getObjectNameFromSourceCode(sourceCode, lineNumber);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetObjectNameOfTheTooSmallLineNumberFromValidSourceCode() {
        String sourceCode = """
        public class ExampleClass {
            SomeClass myObject = new SomeClass();
            int anotherObject = 42;
        }
        """;
        int lineNumber = 0;
        ClassUtils.getObjectNameFromSourceCode(sourceCode, lineNumber);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetObjectNameFromNullSourceCode() {
        ClassUtils.getObjectNameFromSourceCode(null, 3);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetObjectNameFromBlankSourceCode() {
        ClassUtils.getObjectNameFromSourceCode("", 3);
    }

    @Test
    public void testGetSimpleClassNameWithFullClassName() {
        String fullClassName = "java.util.ArrayList";
        String simpleClassName = ClassUtils.getSimpleClassName(fullClassName);
        Assert.assertEquals(simpleClassName, "ArrayList");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetSimpleClassNameWithSimpleClassName() {
        String fullClassName = "ArrayList";
        String simpleClassName = ClassUtils.getSimpleClassName(fullClassName);
        Assert.assertEquals(simpleClassName, "ArrayList");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetSimpleClassNameWithNullFullClassName() {
        ClassUtils.getSimpleClassName(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetSimpleClassNameWithBlankFullClassName() {
        ClassUtils.getSimpleClassName("      ");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetSimpleClassNameWithMultilineFullClassName() {
        ClassUtils.getSimpleClassName("java.util.ArrayList\n");
    }

    @Test
    public void testGetInvocationCodeLineNumberWithValidClassName() {
        // Get the expected code line number of the method invocation
        int expectedLineNumber = ClassUtils.getCurrentInvocationCodeLineNumber() + 2;
        // Call the method to get the line number where it's invoked
        int lineNumber = ClassUtils.getInvocationCodeLineNumber(getClass().getName());

        // Check if the returned line number
        Assert.assertEquals(lineNumber, expectedLineNumber);
    }
}



