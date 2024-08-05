package org.example.unit;

import org.example.asserts.SmartAssert;
import org.example.data.SmartDataObject;
import org.example.data.SmartType;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
import org.testng.annotations.Test;

import java.util.Date;

public class SmartAssertTest {

    static class DummyDataObject extends SmartDataObject {
        private SmartType smartType;

        public void setSmartType(SmartType smartType) {
            this.smartType = smartType;
        }

        @Override
        public String toString() {
            return smartType.toString();
        }
    }

    static class OtherFieldNameDataObject extends SmartDataObject {
        private SmartType someType;

        public void setSmartType(SmartType smartType) {
            this.someType = smartType;
        }

        @Override
        public String toString() {
            return someType.toString();
        }
    }

    static class EmptyDataObject extends SmartDataObject {
    }

    @Test
    public void testSmartAssertStringPositive() {
        String value = "some value";
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, value));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertIntegerPositive() {
        int value = 1;
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, value));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertLongPositive() {
        long value = 123456789L;
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, value));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertFloatPositive() {
        float value = 123.345f;
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, value));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertDoublePositive() {
        double value = 123456789.123456789d;
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, value));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertBooleanPositive() {
        boolean value = false;
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, value));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertDatePositive() {
        Date date = ConverterUtils.stringToDate("1970-05-23");
        String dateFormat = "yyy-MM-dd";
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, date, dateFormat));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, date, dateFormat));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertStringFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, "aaa"));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, "bbb"));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertIntegerFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, 1));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, 2));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertLongFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, 1L));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, 2L));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertFloatFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, 123.456f));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, 123.1f));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertDoubleFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, 123456789.123456789d));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, 987654321.987654321d));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertBooleanFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, true));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, false));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertDateFailsPositive() {
        DummyDataObject expected = new DummyDataObject();
        Date expectedDate = ConverterUtils.stringToDate("1970-05-23");
        Date actualDate = ConverterUtils.stringToDate("1974-12-13");
        String dateFormat = "yyyy-MM-dd";
        expected.setSmartType(new SmartType(expected, expectedDate, dateFormat));
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, actualDate, dateFormat));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSmartAssertExpectedNullNegative() {
        DummyDataObject actual = new DummyDataObject();
        actual.setSmartType(new SmartType(actual, "Some value"));

        SmartAssert.assertDataObjects(null, actual);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSmartAssertActualNullNegative() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, "Some value"));

        SmartAssert.assertDataObjects(expected, null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSmartAssertTheSameObjectNegative() {
        DummyDataObject dummyDataObject = new DummyDataObject();
        dummyDataObject.setSmartType(new SmartType(dummyDataObject, "Some value"));

        SmartAssert.assertDataObjects(dummyDataObject, dummyDataObject);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSmartAssertMissedFieldNegative() {
        DummyDataObject expected = new DummyDataObject();
        expected.setSmartType(new SmartType(expected, "Some value"));
        EmptyDataObject actual = new EmptyDataObject();

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSmartAssertOtherFieldNameNegative() {
        DummyDataObject expected = new DummyDataObject();
        String value = "Some value";
        expected.setSmartType(new SmartType(expected, value));
        OtherFieldNameDataObject actual = new OtherFieldNameDataObject();
        actual.setSmartType(new SmartType(expected, value));

        SmartAssert.assertDataObjects(expected, actual);
    }
}
