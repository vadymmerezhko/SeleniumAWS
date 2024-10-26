package org.example.ui.elements;

import org.example.annotations.RunAlone;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.wrappers.SmartElement;
import org.example.utils.DataValidationUtils;
import org.example.utils.FileSystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.nio.file.Path;

/**
 * File input element class that extents text input class.
 */
public class FileInput extends SmartElement {

    /**
     * File input element constructor with auto selector.
     */
    public FileInput() {
    }

    /**
     * File input element constructor by its selector.
     * @param by The element selector.
     */
    public FileInput(By by) {
        super(by);
    }

    /**
     * Enters file path.
     * @param filePath The file path.
     */
    public void enterFilePath(String filePath) {
        DataValidationUtils.validateFilePathFormat(filePath, "filePath");
        enterFilePath(Path.of(filePath));
    }

    /**
     * Enters file path from file object.
     * @param file The file object.
     */
    public void enterFilePath(File file) {
        DataValidationUtils.validateNotNull(file, "file");
        enterFilePath(file.getPath());
    }

    /**
     * Enters file path from file smart value.
     * @param fileSmartValue The file smart value.
     */
    public void enterFilePath(SmartValue fileSmartValue) {
        DataValidationUtils.validateNotNull(fileSmartValue, "fileSmartValue");
        enterFilePath(fileSmartValue.toPath());
    }

    /**
     * Enters file path string.
     * @param filePath The file path string.
     */
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void enterFilePath(Path filePath) {
        DataValidationUtils.validateNotNull(filePath, "filePath");
        WebElement fileInput = getElement();
        // Normalize file path - replace Windows slashes with Unix slashes
        String filePathString = FileSystemUtils.normalizeFilePathString(filePath.toString());
        filePath = Path.of(filePathString);

        try {
            fileInput.clear();
            fileInput.sendKeys(filePath.toAbsolutePath().toString());
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot enter %s file input file path: %s",
                    elementName, filePath), e);
        }
    }

    /**
     * Returns file input value - file path string.
     * It normalizes file path string - replaces
     * Windows slashes with Unix slashes and replaces "fakepath"
     * with actual file path found by file name in current
     * directory and all directories child directories.
     * This is a workaround for the case, when "fakepath" directory
     * is returned as file input value by WebDriver for security purpose.
     * @return The file path string.
     */
    public String getValue() {
        WebElement fileInput = getElement();

        try {
            // Normalize file path - replace Windows slashes with Unix slashes and
            // replace "fakepath" with actual path in the project folder or its sub folders
            String value = FileSystemUtils.normalizeFilePathString(
                    fileInput.getAttribute("value"));
            // Remove current folder path from file path to provide consistency for Unix and Mac
            String currentFolderPath = FileSystemUtils.getCurrentFolderPath();
            value = value.replace(currentFolderPath + "/", "");
            return value;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot gwt %s file input file path value.",
                    elementName), e);
        }
    }

    /**
     * Returns file input smart value - file path smart value.
     * @return The file path smart value.
     */
    public SmartValue getSmartValue() {
        try {
            return new SmartValue(getValue());
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot gwt %s file input file path value.",
                    elementName), e);
        }
    }
}
