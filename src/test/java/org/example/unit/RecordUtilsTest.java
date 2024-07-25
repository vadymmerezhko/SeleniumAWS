package org.example.unit;

import org.example.utils.RecordUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RecordUtilsTest {
    record ExampleRecord(
        String name, int age) {}

    @Test
    public void testRecordToStringPositive() {
        ExampleRecord record = new ExampleRecord("John Doe", 30);
        String expectedJson = "{\"name\":\"John Doe\",\"age\":30}";
        String actualJson = RecordUtils.recordToString(record);
        Assert.assertEquals(actualJson, expectedJson, "The JSON output is not as expected.");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testRecordToStringWithNullRecord() {
        RecordUtils.recordToString(null);
    }

    @Test
    public void testStringToRecordPositive() {
        String jsonString = "{\"name\":\"Jane Doe\",\"age\":25}";
        ExampleRecord expectedRecord = new ExampleRecord("Jane Doe", 25);
        ExampleRecord actualRecord = (ExampleRecord) RecordUtils.stringToRecord(jsonString, ExampleRecord.class);
        Assert.assertEquals(actualRecord.name, expectedRecord.name, "Name does not match.");
        Assert.assertEquals(actualRecord.age, expectedRecord.age, "Age does not match.");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testStringToRecordWithInvalidJson() {
        String invalidJson = "invalid json string";
        RecordUtils.stringToRecord(invalidJson, ExampleRecord.class);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testStringToRecordWithNullJsonString() {
        RecordUtils.stringToRecord(null, ExampleRecord.class);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testStringToRecordWithWrongRecordClass() {
        String jsonString = "{\"name\":\"Jane Doe\",\"age\":25}";
        RecordUtils.stringToRecord(jsonString, this.getClass());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testStringToRecordWithNullRecordClass() {
        String validJson = "{\"name\":\"John Doe\",\"age\":30}";
        RecordUtils.stringToRecord(validJson, null);
    }
}
