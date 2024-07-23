package org.example.unit;

import org.example.exceptions.DataValidationException;
import org.example.utils.DataValidationUtils;
import org.testng.annotations.Test;

public class DataValidationUtilsTest {
    @Test
    public void testValidateNotNullValid() {
        DataValidationUtils.validateNotNull("valid", "testData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateNotNullNull() {
        DataValidationUtils.validateNotNull(null, "testData");
    }

    @Test
    public void testValidateNotEmptyValid() {
        DataValidationUtils.validateNotEmpty("valid", "testData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateNotEmptyEmpty() {
        DataValidationUtils.validateNotEmpty("", "testData");
    }

    @Test
    public void testValidateNotBlankValid() {
        DataValidationUtils.validateNotBlank("valid", "testData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateNotBlankBlank() {
        DataValidationUtils.validateNotBlank("   ", "testData");
    }

    @Test
    public void testValidateNotMultilineValid() {
        DataValidationUtils.validateNotMultiline("valid", "testData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateNotMultilineMultiline() {
        DataValidationUtils.validateNotMultiline("valid\nInvalid", "testData");
    }

    @Test
    public void testValidateColorFormatValid() {
        DataValidationUtils.validateColorFormat("#FF0088", "colorData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateColorFormatInvalid() {
        DataValidationUtils.validateColorFormat("FF0088", "colorData");
    }

    @Test
    public void testValidateMmDdYyyyDateValueValid() {
        DataValidationUtils.validateMmDdYyyyDateValue("05/23/1970", "dateData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateMmDdYyyyDateValueInvalid() {
        DataValidationUtils.validateMmDdYyyyDateValue("1970/05/23", "dateData");
    }

    @Test
    public void testValidateRangeValid() {
        DataValidationUtils.validateRange(50, 1, 100, "rangeData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateRangeInvalid() {
        DataValidationUtils.validateRange(150, 1, 100, "rangeData");
    }

    @Test
    public void testValidateFilePathWinValid() {
        DataValidationUtils.validateFilePath("C:\\path\\to\\file.txt", "filePathData");
    }

    @Test
    public void testValidateFilePathLinuxValid() {
        DataValidationUtils.validateFilePath("./target/file.txt", "filePathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFilePathInvalid() {
        DataValidationUtils.validateFilePath("C:\\path\\to\\file??.txt", "filePathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFilePathBlankInvalid() {
        DataValidationUtils.validateFilePath("", "filePathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFilePathNullInvalid() {
        DataValidationUtils.validateFilePath(null, "filePathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFilePathMultilineInvalid() {
        DataValidationUtils.validateFilePath("/target\n/file.xtx", "filePathData");
    }

    @Test
    public void testValidateFolderWinPathValid() {
        DataValidationUtils.validateFolderPath("C:\\path\\to\\", "folderPathData");
    }

    @Test
    public void testValidateFolderLinuxPathValid() {
        DataValidationUtils.validateFolderPath("./target/test.txt", "folderPathData");
    }

    @Test
    public void testValidateFolderPathBlankValid() {
        DataValidationUtils.validateFolderPath("", "folderPathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFolderPathInvalid() {
        DataValidationUtils.validateFolderPath("C:\\path\\to*\\folder", "folderPathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFolderPathNullInvalid() {
        DataValidationUtils.validateFolderPath(null, "folderPathData");
    }

    @Test(expectedExceptions = DataValidationException.class)
    public void testValidateFolderPathMultilineInvalid() {
        DataValidationUtils.validateFolderPath("./target\n", "folderPathData");
    }
}
