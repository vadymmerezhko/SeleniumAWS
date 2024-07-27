package org.example.unit;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ClassUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class ClassUtilsTest {

    private static class TestClass {
        private final String field = "value";
    }

    private final Consumer<Exception> fix = (e) -> log.debug("Some fix goes here.");

    @Test
    public void testGetClassFieldNamePositive() {
        TestClass testClass = new TestClass();
        Assert.assertEquals(ClassUtils.getClassFieldName(testClass, testClass.field), "field");
    }

    @Test
    public void testGetClassFieldNameNegative() {
        TestClass testClass = new TestClass();
        Assert.assertNull(ClassUtils.getClassFieldName(testClass, new Object()));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testThrowMethodNotImplementedException() {
        ClassUtils.throwMethodNotImplementedException("someMethod");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testThrowMethodNotImplementedExceptionWithEmptyName() {
        ClassUtils.throwMethodNotImplementedException("");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testThrowMethodNotImplementedExceptionWithNullName() {
        ClassUtils.throwMethodNotImplementedException(null);
    }

    @Test
    public void testPerformRunnableMethod() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Runnable action = () -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
        };
        ClassUtils.performRunnableMethod(action, null, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test
    public void testPerformRunnableMethodWithFix() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Runnable action = () -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
        };
        ClassUtils.performRunnableMethod(action, fix, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformRunnableMethodFailure() {
        Runnable action = () -> {
            throw new SmartRuntimeException("Fail every time");
        };
        ClassUtils.performRunnableMethod(action, null, "testAction", 2, 10);
    }

    @Test
    public void testPerformConsumerMethod() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Consumer<String> action = (s) -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
        };
        ClassUtils.performConsumerMethod(action, "parameter",null, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test
    public void testPerformConsumerMethodWithFix() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Consumer<String> action = (s) -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
        };
        ClassUtils.performConsumerMethod(action, "parameter", fix, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformConsumerMethodFailure() {
        Consumer<String> action = (s) -> {
            throw new SmartRuntimeException("Fail every time");
        };
        ClassUtils.performConsumerMethod(action, "parameter",null, "testAction", 5, 10);
    }

    @Test
    public void testPerformSupplierMethod() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Supplier<String> action = () -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
            return "Some result";
        };
        ClassUtils.performSupplierMethod(action, null, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test
    public void testPerformSupplierMethodWithFix() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Supplier<String> action = () -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
            return "Some result";
        };
        ClassUtils.performSupplierMethod(action, fix, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformSupplierMethodFailure() {
        Supplier<String> action = () -> {
            throw new SmartRuntimeException("Fail every time");
        };
        ClassUtils.performSupplierMethod(action, null, "testAction", 5, 10);
    }

    @Test
    public void testPerformFunctionMethod() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Function<String, String> action = (s) -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
            return "Some result";
        };
        ClassUtils.performFunctionMethod(action, "parameter", null, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test
    public void testPerformFunctionMethodWithFix() {
        AtomicInteger attemptCounter = new AtomicInteger(0);
        Function<String, String> action = (s) -> {
            if (attemptCounter.incrementAndGet() < 3) {
                throw new SmartRuntimeException("Need more retries");
            }
            return "Some result";
        };
        ClassUtils.performFunctionMethod(action, "parameter", fix, "testAction", 5, 10);
        Assert.assertEquals(attemptCounter.get(), 3);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPerformFunctionMethodFailure() {
        Function<String, String> action = (s) -> {
            throw new SmartRuntimeException("Fail every time");
        };
        ClassUtils.performFunctionMethod(action, "parameter", null, "testAction", 5, 10);
    }
}
