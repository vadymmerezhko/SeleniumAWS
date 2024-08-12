package org.example.unit;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.example.data.TargetPageOutput;
import org.example.exceptions.SmartRuntimeException;

import java.time.LocalDate;
import java.util.Date;

public class SmartTypeTest {

    @Test
    public void testSmartTypeAutoInitialization() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();

        // Act
        String headerValue = targetPageOutput.getHeader().toString();
        String statusValue = targetPageOutput.getStatus().toString();

        // Assert
        Assert.assertNotNull(headerValue, "Header SmartType should be initialized.");
        Assert.assertNotNull(statusValue, "Status SmartType should be initialized.");
    }

    @Test
    public void testSetLocalDate() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        LocalDate localDate = LocalDate.of(2024, 8, 8);
        String dateFormat = "yyyy-MM-dd";

        // Act
        targetPageOutput.getHeader().setLocalDate(localDate, dateFormat);

        // Assert
        Assert.assertEquals(targetPageOutput.getHeader().toString(), "2024-08-08",
                "LocalDate conversion should match the expected value.");
    }

    @Test
    public void testSetAndSaveString() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String value = "TestValue";

        // Act
        targetPageOutput.getStatus().setAndSaveString(value);

        // Assert
        Assert.assertEquals(targetPageOutput.getStatus().toString(), value,
                "SmartType value should match the saved value.");
    }

    @Test
    public void testToDateStringWithValidDate() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        Date date = new Date();
        String dateFormat = "yyyy-MM-dd";
        targetPageOutput.getHeader().setLocalDate(LocalDate.now(), dateFormat);

        // Act
        String dateString = targetPageOutput.getHeader().toDateString(dateFormat);

        // Assert
        Assert.assertNotNull(dateString, "Date string should not be null.");
        // Additional format validation could be done here
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateStringWithInvalidObject() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String invalidValue = "InvalidDate";
        targetPageOutput.getHeader().setAndSaveString(invalidValue);

        // Act
        targetPageOutput.getHeader().toDateString("yyyy-MM-dd");

        // Exception is expected
    }

    @Test
    public void testToIntegerWithValidInteger() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String value = "123";
        targetPageOutput.getHeader().setAndSaveString(value);

        // Act
        int intValue = targetPageOutput.getHeader().toInteger();

        // Assert
        Assert.assertEquals(intValue, 123, "Integer conversion should match the expected value.");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testToIntegerWithInvalidInteger() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String invalidValue = "InvalidInteger";
        targetPageOutput.getHeader().setAndSaveString(invalidValue);

        // Act
        targetPageOutput.getHeader().toInteger();

        // Exception is expected
    }

    @Test
    public void testToLongWithValidLong() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String value = "123456789012345";
        targetPageOutput.getHeader().setAndSaveString(value);

        // Act
        long longValue = targetPageOutput.getHeader().toLong();

        // Assert
        Assert.assertEquals(longValue, 123456789012345L, "Long conversion should match the expected value.");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testToLongWithInvalidLong() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String invalidValue = "InvalidLong";
        targetPageOutput.getHeader().setAndSaveString(invalidValue);

        // Act
        targetPageOutput.getHeader().toLong();

        // Exception is expected
    }

    @Test
    public void testToFloatWithValidFloat() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String value = "123.45";
        targetPageOutput.getHeader().setAndSaveString(value);

        // Act
        float floatValue = targetPageOutput.getHeader().toFloat();

        // Assert
        Assert.assertEquals(floatValue, 123.45f, "Float conversion should match the expected value.");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testToFloatWithInvalidFloat() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String invalidValue = "InvalidFloat";
        targetPageOutput.getHeader().setAndSaveString(invalidValue);

        // Act
        targetPageOutput.getHeader().toFloat();

        // Exception is expected
    }

    @Test
    public void testToDoubleWithValidDouble() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String value = "123.456789";
        targetPageOutput.getHeader().setAndSaveString(value);

        // Act
        double doubleValue = targetPageOutput.getHeader().toDouble();

        // Assert
        Assert.assertEquals(doubleValue, 123.456789, "Double conversion should match the expected value.");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testToDoubleWithInvalidDouble() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String invalidValue = "InvalidDouble";
        targetPageOutput.getHeader().setAndSaveString(invalidValue);

        // Act
        targetPageOutput.getHeader().toDouble();

        // Exception is expected
    }

    @Test
    public void testToBooleanWithValidBoolean() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String value = "true";
        targetPageOutput.getHeader().setAndSaveString(value);

        // Act
        boolean booleanValue = targetPageOutput.getHeader().toBoolean();

        // Assert
        Assert.assertTrue(booleanValue, "Boolean conversion should match the expected value.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testToBooleanWithInvalidBoolean() {
        // Arrange
        TargetPageOutput targetPageOutput = new TargetPageOutput();
        String invalidValue = "InvalidBoolean";
        targetPageOutput.getHeader().setAndSaveString(invalidValue);

        // Act
        targetPageOutput.getHeader().toBoolean();

        // Exception is expected
    }

    @Test
    public void testEqualsWithSameValue() {
        // Arrange
        TargetPageOutput targetPageOutput1 = new TargetPageOutput();
        TargetPageOutput targetPageOutput2 = new TargetPageOutput();
        String value = "TestValue";
        targetPageOutput1.getHeader().setAndSaveString(value);
        targetPageOutput2.getHeader().setAndSaveString(value);

        // Act
        boolean isEqual = targetPageOutput1.getHeader().equals(targetPageOutput2.getHeader());

        // Assert
        Assert.assertTrue(isEqual, "SmartType objects with the same value should be equal.");
    }

    @Test
    public void testEqualsWithDifferentValue() {
        // Arrange
        TargetPageOutput targetPageOutput1 = new TargetPageOutput();
        TargetPageOutput targetPageOutput2 = new TargetPageOutput();
        String value1 = "TestValue1";
        String value2 = "TestValue2";
        targetPageOutput1.getHeader().setAndSaveString(value1);
        targetPageOutput2.getHeader().setAndSaveString(value2);

        // Act
        boolean isEqual = targetPageOutput1.getHeader().equals(targetPageOutput2.getHeader());

        // Assert
        Assert.assertFalse(isEqual, "SmartType objects with different values should not be equal.");
    }
}

