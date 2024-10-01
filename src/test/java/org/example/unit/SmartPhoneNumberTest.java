package org.example.unit;

import org.example.data.SmartPhoneNumber;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConvertUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;

public class SmartPhoneNumberTest {

    @Test
    public void testFromStringWithValidPhoneNumber() {
        String validNumberString = "+1234567890";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(validNumberString);

        Assert.assertNotNull(smartPhoneNumber);
        Assert.assertEquals(smartPhoneNumber.getNumber().longValue(), 1234567890L);
        Assert.assertEquals(smartPhoneNumber.getFormat(), "+##########");
    }

    @Test
    public void testToStringWithValidPhoneNumber() {
        String numberString = "+(123)456-7890";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithAllDashDelimiters() {
        String numberString = "+123-456-7890";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithCountryCode() {
        String numberString = "+123-456-789-0123";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithCountryCodeWithSpaces() {
        String numberString = "+1 456 789 0123";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidShortestPhoneNumberWithoutDelimiters() {
        String numberString = "100000";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidLongestPhoneNumberWithoutDelimiters() {
        String numberString = "99999999999999999999";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithMaximalPhoneExtension() {
        String numberString = "+987(456)-789-0123 #12345";
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString(numberString);
        String formattedString = smartPhoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartPhoneNumber.getNumber(), smartPhoneNumber.getFormat()));
    }

    @Test
    public void testFromNumberWithValidPhoneNumber() {
        Number phoneNumber = 1234567890L; // Valid phone number (10 digits)
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromNumber(phoneNumber);

        Assert.assertNotNull(smartPhoneNumber);
        Assert.assertEquals(smartPhoneNumber.getNumber(), phoneNumber);
        Assert.assertEquals(smartPhoneNumber.getFormat(), "##########");
    }

    @Test
    public void testFromNumberWithValidMinPhoneNumber() {
        Number phoneNumber = 100000L; // Minimal valid phone number (6 digits)
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromNumber(phoneNumber);

        Assert.assertNotNull(smartPhoneNumber);
        Assert.assertEquals(smartPhoneNumber.getNumber(), phoneNumber);
        Assert.assertEquals(smartPhoneNumber.getFormat(), "######");
    }

    @Test
    public void testFromNumberWithValidMaxPhoneNumber() {
        // Max valid phone number (15 digits) and 5 digits of extension
        Number phoneNumber = new BigInteger("99999999999999999999");
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromNumber(phoneNumber);

        Assert.assertNotNull(smartPhoneNumber);
        Assert.assertEquals(smartPhoneNumber.getNumber(), phoneNumber);
        Assert.assertEquals(smartPhoneNumber.getFormat(), "####################");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFromNumberWithInvalidShortPhoneNumber() {
        Number phoneNumber = 99999L; // Too short (5 digits)
        SmartPhoneNumber.fromNumber(phoneNumber);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFromNumberWithInvalidLongPhoneNumber() {
        // Too long number - 21 digits
        Number phoneNumber = new BigInteger("999999999999999999999");
        SmartPhoneNumber.fromNumber(phoneNumber);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromNumberWithNullPhoneNumber() {
        SmartPhoneNumber.fromNumber(null);
    }

    @Test
    public void testToStringWithValidPhoneNumberAndFormat() {
        SmartPhoneNumber smartPhoneNumber =
                SmartPhoneNumber.fromString("123-456-7890");

        String expected = "123-456-7890"; // Expected formatted string
        Assert.assertEquals(smartPhoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithValidPhoneNumberWithoutCountryCode() {
        // Assuming SmartPhoneNumber has a constructor where format can be null
        SmartPhoneNumber smartPhoneNumber =
                SmartPhoneNumber.fromString("+1(123)456-7890");

        String expected = "+1(123)456-7890"; // Expected string without formatting
        Assert.assertEquals(smartPhoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithValidPhoneNumberWithoutExtensionNumber() {
        // Assuming SmartPhoneNumber has a constructor where format can be null
        SmartPhoneNumber smartPhoneNumber =
                SmartPhoneNumber.fromString("+987(123)456-7890 #12345");

        // TODO: # is removed before number extension
        String expected = "+987(123)456-7890 12345"; // Expected string without formatting
        Assert.assertEquals(smartPhoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithShortPhoneNumberAndFormat() {
        SmartPhoneNumber smartPhoneNumber =
                SmartPhoneNumber.fromString("123-456");

        String expected = "123-456"; // Expected formatted string
        Assert.assertEquals(smartPhoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithShortPhoneNumberWithoutDelimiters() {
        SmartPhoneNumber smartPhoneNumber =
                SmartPhoneNumber.fromString("123456");

        String expected = "123456"; // Expected formatted string
        Assert.assertEquals(smartPhoneNumber.toString(), expected);
    }

    @Test
    public void testEqualsWithSameObject() {
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString("123-456-7890");

        Assert.assertEquals(smartPhoneNumber, smartPhoneNumber);
    }

    @Test
    public void testEqualsWithEqualNumber() {
        SmartPhoneNumber smartPhoneNumber1 = SmartPhoneNumber.fromString("+1(123)-456-7890");
        SmartPhoneNumber smartPhoneNumber2 = SmartPhoneNumber.fromString("1 123-456-7890");

        Assert.assertEquals(smartPhoneNumber1, smartPhoneNumber2);
    }

    @Test
    public void testEqualsWithDifferentNumber() {
        SmartPhoneNumber smartPhoneNumber1 = SmartPhoneNumber.fromString("+111(245)-456-7890");
        SmartPhoneNumber smartPhoneNumber2 = SmartPhoneNumber.fromString("1 123-456-7890");

        Assert.assertNotEquals(smartPhoneNumber1, smartPhoneNumber2);
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString("+1(245)-456-7890");

        Assert.assertNotEquals(smartPhoneNumber, new Object());
    }

    @Test
    public void testEqualsWithNullObject() {
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString("+1(245)-456-7890");

        Assert.assertNotEquals(smartPhoneNumber, null);
    }

    @Test
    public void testEqualsWithTheSameNumber() {
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString("987-654-321");

        Long sameNumber = 987654321L;
        Assert.assertNotEquals(smartPhoneNumber ,sameNumber);
    }

    @Test
    public void testEqualsWithNonNumberObject() {
        SmartPhoneNumber smartPhoneNumber = SmartPhoneNumber.fromString("+1(245)-456-7890");

        String nonNumberObject = "+1(245)-456-7890"; // Non-number object
        Assert.assertNotEquals(smartPhoneNumber, nonNumberObject);
    }
}
