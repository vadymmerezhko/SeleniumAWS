package com.smarte2e.unit;

import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.exceptions.SmartValidationException;
import com.smarte2e.utils.DataValidator;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DataValidatorTest {
    @Test
    public void testValidateNotNullValid() {
        DataValidator.notNull("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotNullNull() {
        DataValidator.notNull(null, "testData");
    }

    @Test
    public void testValidateNotEmptyValid() {
        DataValidator.notEmpty("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotEmptyEmpty() {
        DataValidator.notEmpty("", "testData");
    }

    @Test
    public void testValidateNotBlankValid() {
        DataValidator.notBlank("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotBlankBlank() {
        DataValidator.notBlank("   ", "testData");
    }

    @Test
    public void testValidateNotMultilineValid() {
        DataValidator.notMultiline("valid", "testData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateNotMultilineMultiline() {
        DataValidator.notMultiline("valid\nInvalid", "testData");
    }

    @Test
    public void testValidateColorFormatValid() {
        DataValidator.colorFormat("#FF0088", "colorData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateColorFormatInvalid() {
        DataValidator.colorFormat("FF0088", "colorData");
    }

    @Test
    public void testValidateMmDdYyyyDateValueValid() {
        DataValidator.dateValue("05/23/1970", "dateData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateMmDdYyyyDateValueInvalid() {
        DataValidator.dateValue("1970/05/23", "dateData");
    }

    @Test
    public void testValidateRangeValid() {
        DataValidator.range(50, 1, 100, "rangeData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateRangeInvalid() {
        DataValidator.range(150, 1, 100, "rangeData");
    }

    @Test
    public void testValidateFilePathWinValid() {
        DataValidator.filePathFormat("C:\\path\\to\\file.txt", "filePathData");
    }

    @Test
    public void testValidateFilePathLinuxValid() {
        DataValidator.filePathFormat("./target/file.txt", "filePathData");
    }

/*  TODO: Fix  @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFilePathInvalid() {
        DataValidator.filePathFormat("C:\\path\\to\\file??.txt", "filePathData");
    }*/

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFilePathBlankInvalid() {
        DataValidator.filePathFormat("", "filePathData");
    }


/*  TODO: Fix @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFilePathMultilineInvalid() {
        DataValidator.filePathFormat("/target\n/file.xtx", "filePathData");
    }*/

    @Test
    public void testValidateFolderWinPathValid() {
        DataValidator.folderPathFormat("C:\\path\\to\\", "folderPathData");
    }

    @Test
    public void testValidateFolderLinuxPathValid() {
        DataValidator.folderPathFormat("./target/test.txt", "folderPathData");
    }

/*  TODO: Fix  @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFolderPathInvalid() {
        DataValidator.folderPathFormat("C:\\path\\to*\\folder", "folderPathData");
    }*/

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFolderPathNullInvalid() {
        DataValidator.folderPathFormat(null, "folderPathData");
    }

/*  TODO: Fix @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFolderPathMultilineInvalid() {
        DataValidator.folderPathFormat("./target\n", "folderPathData");
    }*/

    @Test
    public void testValidateMinValid() {
        DataValidator.min(50, 50, "minData");
        DataValidator.min(0, -1, "minData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateMinInvalid() {
        DataValidator.min(49, 50, "minData");
    }

    @Test
    public void testValidateMaxValid() {
        DataValidator.max(50, 50, "maxData");
        DataValidator.max(-1, 0, "maxData");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateMaxInvalid() {
        DataValidator.max(51, 50, "minData");
    }

    @Test
    public void testValidateRangeWithinRange() {
        // Positive test cases
        DataValidator.range(5.0, 1.0, 10.0, "Test Value");
        DataValidator.range(1.0, 1.0, 10.0, "Test Value");
        DataValidator.range(10.0, 1.0, 10.0, "Test Value");
    }

    @Test
    public void testValidateRangeBelowRange() {
        // Negative test case: value below range
        String expectedMessage = "Test Value has invalid [1.000000:10.000000] range value: 0.000000";
        expectHandleError(() -> DataValidator.range(0.0, 1.0, 10.0, "Test Value"), expectedMessage);
    }

    @Test
    public void testValidateRangeAboveRange() {
        // Negative test case: value above range
        String expectedMessage = "Test Value has invalid [1.000000:10.000000] range value: 11.000000";
        expectHandleError(() -> DataValidator.range(11.0, 1.0, 10.0, "Test Value"), expectedMessage);
    }

    @Test
    public void testValidateRangeExactLowerBound() {
        // Positive test case: value exactly at lower bound
        DataValidator.range(1.0, 1.0, 10.0, "Test Value");
    }

    @Test
    public void testValidateRangeExactUpperBound() {
        // Positive test case: value exactly at upper bound
        DataValidator.range(10.0, 1.0, 10.0, "Test Value");
    }

    @Test
    public void testValidateFullClassNameWithValidClassName() {
        String validClassName = "com.smarte2e.MyClass";
        DataValidator.fullClassName(validClassName, "validClassName");
    }

    @Test
    public void testValidateFullClassNameWithValidNestedClassName() {
        String validClassName = "com.company.project.SomeClass$NestedClass";
        DataValidator.fullClassName(validClassName, "validClassName");
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
        DataValidator.fullClassName(null, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithEmptyClassName() {
        DataValidator.fullClassName("", "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithMultilineClassName() {
        DataValidator.fullClassName(
                "\ncom.company.project.SomeClass$NestedClass",
                "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithInvalidLowercaseClassName() {
        String invalidClassName = "com.smarte2e.myClass";  // Class name starts with lowercase
        DataValidator.fullClassName(invalidClassName, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidateFullClassNameWithInvalidPackageName() {
        String invalidClassName = "org.ex@ample.MyClass";  // Invalid character '@' in package name
        DataValidator.fullClassName(invalidClassName, "invalidClassName");
    }

    @Test
    public void testValidatePackageNameWithValidPackageName() {
        String validPackageName = "com.smarte2e.utils";
        DataValidator.packageName(validPackageName, "invalidClassName");
    }

    @Test
    public void testValidatePackageNameWithSingleSegment() {
        String validPackageName = "com";
        DataValidator.packageName(validPackageName, "invalidClassName");
    }

    @Test
    public void testValidatePackageNameWithUnderscore() {
        String validPackageName = "com.smarte2e_project.utils";
        DataValidator.packageName(validPackageName, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameWithNullPackageName() {
        DataValidator.packageName(null, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameWithEmptyPackageName() {
        DataValidator.packageName("", "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testInValidateMultilinePackageName() {
        String validPackageName = "\ncom.smarte2e_project.utils";
        DataValidator.packageName(validPackageName, "invalidClassName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameWithInvalidCharacters() {
        String invalidPackageName = "org.ex@ample.utils";  // Invalid '@' character
        DataValidator.packageName(invalidPackageName, "invalidPackageName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameStartingWithNumber() {
        String invalidPackageName = "123example.utils";
        DataValidator.packageName(invalidPackageName, "invalidPackageName");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatePackageNameStartingWithNullValueName() {
        String invalidPackageName = "ncom.smarte2e_project.utils";
        DataValidator.packageName(invalidPackageName, null);
    }

    @Test
    public void testValidatesSimpleClassNameWithValidClassName() {
        String validClassName = "MyClass";
        DataValidator.simpleClassName(validClassName, "className");
        Assert.assertTrue(true);  // If no exception is thrown, the test passes
    }

    @Test
    public void testValidatesSimpleClassNameWithInnerClassName() {
        String validInnerClassName = "OuterClass$InnerClass";
        DataValidator.simpleClassName(validInnerClassName, "className");
        Assert.assertTrue(true);  // If no exception is thrown, the test passes
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatesSimpleClassNameWithNullClassName() {
        DataValidator.simpleClassName(null, "className");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testValidatesSimpleClassNameWithEmptyClassName() {
        DataValidator.simpleClassName("", "className");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testValidatesSimpleClassNameWithInvalidLowercaseClassName() {
        String invalidClassName = "myClass";  // Invalid: starts with lowercase
        DataValidator.simpleClassName(invalidClassName, "className");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testValidatesSimpleClassNameWithInvalidCharacters() {
        String invalidClassName = "Class@Name";  // Invalid character '@'
        DataValidator.simpleClassName(invalidClassName, "className");
    }

}
