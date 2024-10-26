package org.example.unit;

import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.DataValidationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DataValidationUtilsTest {
    @Test
    public void testValidateNotNullValid() {
        DataValidationUtils.validateNotNull("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotNullNull() {
        DataValidationUtils.validateNotNull(null, "testData");
    }

    @Test
    public void testValidateNotEmptyValid() {
        DataValidationUtils.validateNotEmpty("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotEmptyEmpty() {
        DataValidationUtils.validateNotEmpty("", "testData");
    }

    @Test
    public void testValidateNotBlankValid() {
        DataValidationUtils.validateNotBlank("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotBlankBlank() {
        DataValidationUtils.validateNotBlank("   ", "testData");
    }

    @Test
    public void testValidateNotMultilineValid() {
        DataValidationUtils.validateNotMultiline("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotMultilineMultiline() {
        DataValidationUtils.validateNotMultiline("valid\nInvalid", "testData");
    }

    @Test
    public void testValidateColorFormatValid() {
        DataValidationUtils.validateColorFormat("#FF0088", "colorData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateColorFormatInvalid() {
        DataValidationUtils.validateColorFormat("FF0088", "colorData");
    }

    @Test
    public void testValidateMmDdYyyyDateValueValid() {
        DataValidationUtils.validateDateValue("05/23/1970", "dateData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateMmDdYyyyDateValueInvalid() {
        DataValidationUtils.validateDateValue("1970/05/23", "dateData");
    }

    @Test
    public void testValidateRangeValid() {
        DataValidationUtils.validateRange(50, 1, 100, "rangeData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateRangeInvalid() {
        DataValidationUtils.validateRange(150, 1, 100, "rangeData");
    }

    @Test
    public void testValidateFilePathWinValid() {
        DataValidationUtils.validateFilePathFormat("C:\\path\\to\\file.txt", "filePathData");
    }

    @Test
    public void testValidateFilePathLinuxValid() {
        DataValidationUtils.validateFilePathFormat("./target/file.txt", "filePathData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFilePathInvalid() {
        DataValidationUtils.validateFilePathFormat("C:\\path\\to\\file??.txt", "filePathData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFilePathBlankInvalid() {
        DataValidationUtils.validateFilePathFormat("", "filePathData");
    }


    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFilePathMultilineInvalid() {
        DataValidationUtils.validateFilePathFormat("/target\n/file.xtx", "filePathData");
    }

    @Test
    public void testValidateFolderWinPathValid() {
        DataValidationUtils.validateFolderPath("C:\\path\\to\\", "folderPathData");
    }

    @Test
    public void testValidateFolderLinuxPathValid() {
        DataValidationUtils.validateFolderPath("./target/test.txt", "folderPathData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFolderPathInvalid() {
        DataValidationUtils.validateFolderPath("C:\\path\\to*\\folder", "folderPathData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFolderPathNullInvalid() {
        DataValidationUtils.validateFolderPath(null, "folderPathData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFolderPathMultilineInvalid() {
        DataValidationUtils.validateFolderPath("./target\n", "folderPathData");
    }

    @Test
    public void testValidateMinValid() {
        DataValidationUtils.validateMin(50, 50, "minData");
        DataValidationUtils.validateMin(0, -1, "minData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateMinInvalid() {
        DataValidationUtils.validateMin(49, 50, "minData");
    }

    @Test
    public void testValidateMaxValid() {
        DataValidationUtils.validateMax(50, 50, "maxData");
        DataValidationUtils.validateMax(-1, 0, "maxData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateMaxInvalid() {
        DataValidationUtils.validateMax(51, 50, "minData");
    }

    @Test
    public void testValidateRangeWithinRange() {
        // Positive test cases
        DataValidationUtils.validateRange(5.0, 1.0, 10.0, "Test Value");
        DataValidationUtils.validateRange(1.0, 1.0, 10.0, "Test Value");
        DataValidationUtils.validateRange(10.0, 1.0, 10.0, "Test Value");
    }

    @Test
    public void testValidateRangeBelowRange() {
        // Negative test case: value below range
        String expectedMessage = "Test Value has invalid [1.000000:10.000000] range value: 0.000000";
        expectHandleError(() -> DataValidationUtils.validateRange(0.0, 1.0, 10.0, "Test Value"), expectedMessage);
    }

    @Test
    public void testValidateRangeAboveRange() {
        // Negative test case: value above range
        String expectedMessage = "Test Value has invalid [1.000000:10.000000] range value: 11.000000";
        expectHandleError(() -> DataValidationUtils.validateRange(11.0, 1.0, 10.0, "Test Value"), expectedMessage);
    }

    @Test
    public void testValidateRangeExactLowerBound() {
        // Positive test case: value exactly at lower bound
        DataValidationUtils.validateRange(1.0, 1.0, 10.0, "Test Value");
    }

    @Test
    public void testValidateRangeExactUpperBound() {
        // Positive test case: value exactly at upper bound
        DataValidationUtils.validateRange(10.0, 1.0, 10.0, "Test Value");
    }

    @Test
    public void testValidateFullClassNameWithValidClassName() {
        String validClassName = "org.example.MyClass";
        DataValidationUtils.validateFullClassName(validClassName, "validClassName");
    }

    @Test
    public void testValidateFullClassNameWithValidNestedClassName() {
        String validClassName = "com.company.project.SomeClass$NestedClass";
        DataValidationUtils.validateFullClassName(validClassName, "validClassName");
    }


    // Helper method to capture the handleError exception
    private void expectHandleError(Runnable action, String expectedMessage) {
        try {
            action.run();
            Assert.fail("Expected SmartRuntimeException was not thrown.");
        } catch (SmartValidationException e) {
            Assert.assertEquals(e.getMessage(), expectedMessage);
        }
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithNullClassName() {
        DataValidationUtils.validateFullClassName(null, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithEmptyClassName() {
        DataValidationUtils.validateFullClassName("", "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithMultilineClassName() {
        DataValidationUtils.validateFullClassName(
                "\ncom.company.project.SomeClass$NestedClass",
                "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithInvalidLowercaseClassName() {
        String invalidClassName = "org.example.myClass";  // Class name starts with lowercase
        DataValidationUtils.validateFullClassName(invalidClassName, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithInvalidPackageName() {
        String invalidClassName = "org.ex@ample.MyClass";  // Invalid character '@' in package name
        DataValidationUtils.validateFullClassName(invalidClassName, "invalidClassName");
    }

    @Test
    public void testValidatePackageNameWithValidPackageName() {
        String validPackageName = "org.example.utils";
        DataValidationUtils.validatePackageName(validPackageName, "invalidClassName");
    }

    @Test
    public void testValidatePackageNameWithSingleSegment() {
        String validPackageName = "com";
        DataValidationUtils.validatePackageName(validPackageName, "invalidClassName");
    }

    @Test
    public void testValidatePackageNameWithUnderscore() {
        String validPackageName = "org.example_project.utils";
        DataValidationUtils.validatePackageName(validPackageName, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameWithNullPackageName() {
        DataValidationUtils.validatePackageName(null, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameWithEmptyPackageName() {
        DataValidationUtils.validatePackageName("", "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testInValidateMultilinePackageName() {
        String validPackageName = "\norg.example_project.utils";
        DataValidationUtils.validatePackageName(validPackageName, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameWithInvalidCharacters() {
        String invalidPackageName = "org.ex@ample.utils";  // Invalid '@' character
        DataValidationUtils.validatePackageName(invalidPackageName, "invalidPackageName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameStartingWithNumber() {
        String invalidPackageName = "123example.utils";
        DataValidationUtils.validatePackageName(invalidPackageName, "invalidPackageName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameStartingWithNullValueName() {
        String invalidPackageName = "norg.example_project.utils";
        DataValidationUtils.validatePackageName(invalidPackageName, null);
    }

    @Test
    public void testValidatesSimpleClassNameWithValidClassName() {
        String validClassName = "MyClass";
        DataValidationUtils.validatesSimpleClassName(validClassName, "className");
        Assert.assertTrue(true);  // If no exception is thrown, the test passes
    }

    @Test
    public void testValidatesSimpleClassNameWithInnerClassName() {
        String validInnerClassName = "OuterClass$InnerClass";
        DataValidationUtils.validatesSimpleClassName(validInnerClassName, "className");
        Assert.assertTrue(true);  // If no exception is thrown, the test passes
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatesSimpleClassNameWithNullClassName() {
        DataValidationUtils.validatesSimpleClassName(null, "className");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatesSimpleClassNameWithEmptyClassName() {
        DataValidationUtils.validatesSimpleClassName("", "className");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testValidatesSimpleClassNameWithInvalidLowercaseClassName() {
        String invalidClassName = "myClass";  // Invalid: starts with lowercase
        DataValidationUtils.validatesSimpleClassName(invalidClassName, "className");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testValidatesSimpleClassNameWithInvalidCharacters() {
        String invalidClassName = "Class@Name";  // Invalid character '@'
        DataValidationUtils.validatesSimpleClassName(invalidClassName, "className");
    }

}
