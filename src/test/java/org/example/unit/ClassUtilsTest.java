package org.example.unit;

import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ClassUtils;
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
        Function<String, Integer> successfulFunction = (input) -> input.length();
        int result = ClassUtils.performFunctionMethod(successfulFunction, "hello", null, METHOD_NAME, 100, 1000);
        Assert.assertEquals(result, 5);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformFunctionMethod_Failure() {
        Function<String, Integer> failingFunction = (input) -> { throw new RuntimeException("Error"); };
        ClassUtils.performFunctionMethod(failingFunction, "hello", null, METHOD_NAME, 100, 1000);
    }
}



