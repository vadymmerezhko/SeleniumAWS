package org.example.unit;

import org.example.data.SmartDataObject;
import org.example.data.SmartType;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class SmartTypeTest {

    static class DummyDataObject extends SmartDataObject {
        private final SmartType smartType;

        public DummyDataObject(SmartType smartType) {
            this.smartType = smartType;
            initialize();
        }
    }

    // Positive test for the string constructor
    @Test
    public void testStringConstructorPositive() {
        String value = "Some value";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toString(), value);
    }

    // Negative test for the string constructor with null value
    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringConstructorNullDate() {
        SmartType.withDate(null, "yyy-MM-dd");
    }

    // Test constructors with different data types
    @Test
    public void testIntegerConstructorPositive() {
        int value = 1;
        SmartType smartType = SmartType.withInteger(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toInteger(), value);
    }

    @Test
    public void testIntegerStringConstructorPositive() {
        String value = "1";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toInteger(), Integer.parseInt(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testIntegerStringConstructorNegative() {
        String value = "invalid";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        smartType.toInteger();
    }

    @Test
    public void testLongConstructorPositive() {
        long value = 123456789L;
        SmartType smartType = SmartType.withLong(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toLong(), value);
    }

    @Test
    public void testLongStringConstructorPositive() {
        String value = "123456789";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toLong(), Long.parseLong(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLongStringConstructorNegative() {
        String value = "invalid";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        smartType.toLong();
    }

    @Test
    public void testFloatConstructorPositive() {
        float value = 123.245f;
        SmartType smartType = SmartType.withFloat(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toFloat(), value);
    }

    @Test
    public void testFloatStringConstructorPositive() {
        String value = "123.456";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toFloat(), Float.parseFloat(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFloatStringConstructorNegative() {
        String value = "invalid";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        smartType.toFloat();
    }

    @Test
    public void testDoubleConstructorPositive() {
        double value = 12356788.123456789d;
        SmartType smartType = SmartType.withDouble(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toDouble(), value);
    }

    @Test
    public void testDoubleStringConstructorPositive() {
        String value = "1123456789.123456789";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toDouble(), Double.parseDouble(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDoubleStringConstructorNegative() {
        String value = "invalid";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        smartType.toDouble();
    }

    @Test
    public void testBooleanConstructorPositive() {
        boolean value = true;
        SmartType smartType = SmartType.withBoolean(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toBoolean(), value);
    }

    @Test
    public void testBooleanStringConstructorPositive() {
        String value = "false";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toBoolean(), Boolean.parseBoolean(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testBooleanStringConstructorNegative() {
        String value = "invalid";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        smartType.toBoolean();
    }

    @Test
    public void testDateConstructorPositive() {
        Date date = ConverterUtils.stringToDate("1970-05-23");
        String dateFormat = "yyy-MM-dd";
        SmartType smartType = SmartType.withDate(date, dateFormat);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toDate(), date);
    }

    @Test
    public void testDateStringConstructorPositive() {
        String value = "1970-05-23";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);

        Assert.assertEquals(smartType.toDate(), ConverterUtils.stringToDate(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDateStringConstructorNegative() {
        String value = "invalid";
        SmartType smartType = SmartType.withString(value);
        new DummyDataObject(smartType);
        smartType.toDate();
    }

    @Test
    public void testSetStringPositive() {
        String value = "Some value";
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setString(value);

        Assert.assertEquals(smartType.toString(), value);
    }

    @Test
    public void testSetIntegerPositive() {
        int value = 1;
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setInteger(value);

        Assert.assertEquals(smartType.toInteger(), value);
    }

    @Test
    public void testSetLongPositive() {
        long value = 123456789L;
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setLong(value);

        Assert.assertEquals(smartType.toLong(), value);
    }

    @Test
    public void testSetFloatPositive() {
        float value = 123.456f;
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setFloat(value);

        Assert.assertEquals(smartType.toFloat(), value);
    }

    @Test
    public void testSetDoublePositive() {
        double value = 123.456f;
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setDouble(value);

        Assert.assertEquals(smartType.toDouble(), value);
    }

    @Test
    public void testSetBooleanPositive() {
        boolean value = true;
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setBoolean(value);

        Assert.assertEquals(smartType.toBoolean(), value);
    }

    @Test
    public void testSetDatePositive() {
        Date date = ConverterUtils.stringToDate("1970-05-23");
        String dateFormat = "yyyy-MM-dd";
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);
        smartType.setDate(date, dateFormat);

        Assert.assertEquals(smartType.toDate(), date);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetNullDateNegative() {
        String dateFormat = "yyyy-MM-dd";
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);

        smartType.setDate(null, dateFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetNullDateFormatNegative() {
        Date date = ConverterUtils.stringToDate("1970-05-23");
        SmartType smartType = SmartType.auto();
        new DummyDataObject(smartType);

        smartType.setDate(date, null);
    }
}

