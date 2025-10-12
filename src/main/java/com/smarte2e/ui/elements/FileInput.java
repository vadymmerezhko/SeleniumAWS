package com.smarte2e.ui.elements;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.annotations.RunAlone;
import com.smarte2e.data.SmartValue;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.interfaces.ReadableObject;
import com.smarte2e.interfaces.WritableObject;
import com.smarte2e.ui.wrappers.SmartElement;
import com.smarte2e.utils.DataValidator;
import com.smarte2e.utils.FileSystemUtils;
import org.openqa.selenium.By;

import java.io.File;
import java.nio.file.Path;


/**
 * File input element class that extents text input class.
 */
@Slf4j
public class FileInput extends SmartElement implements ReadableObject, WritableObject {

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
        DataValidator.filePath(filePath, "filePath");
        enterFilePath(Path.of(filePath));
    }

    /**
     * Enters file path from file object.
     * @param file The file object.
     */
    public void enterFilePath(File file) {
        DataValidator.notNull(file, "file");
        enterFilePath(file.getPath());
    }

    /**
     * Enters file path string.
     * @param filePath The file path string.
     */
    @RunAlone // Run this method when other methods wait to prevent interrupting by other thread
    public void enterFilePath(Path filePath) {
        DataValidator.notNull(filePath, "filePath");
        // Normalize file path - replace Windows slashes with Unix slashes
        String filePathString = FileSystemUtils.normalizeFilePathString(filePath.toString());
        filePath = Path.of(filePathString);

        try {
            clear();
            sendKeys(filePath.toAbsolutePath().toString());
            // Do not log file path for security purpose.
            log.debug("{} file input path is entered", elementName);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot enter %s file input file path: %s",
                    elementName, filePath), e);
        }
    }

    /**
     * Sets file input value.
     * @param value The value.
     * @param <T> The value type.
     */
    @Override
    public <T> void setValue(T value) {
        DataValidator.notNull(value, "value");
        SmartValue smartValue = new SmartValue(value);

        enterFilePath(smartValue.toPath());
        // Do not log file path for security purpose.
        log.debug("{} file input value is set.", elementName);
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
    public String getValueString() {
        try {
            // Normalize file path - replace Windows slashes with Unix slashes and
            // replace "fakepath" with actual path in the project folder or its sub folders
            String value = FileSystemUtils.normalizeFilePathString(getValueDomProperty());
            // Remove current folder path from file path to provide consistency for Unix and Mac
            String currentFolderPath = FileSystemUtils.getCurrentFolderPath();
            value = value.replace(currentFolderPath + "/", "");
            // Do not log file path for security purpose.
            log.debug("{} file input value is returned.", elementName);
            return value;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot get %s file input file path value.",
                    elementName), e);
        }
    }

    /**
     * Returns file input smart value - file path smart value.
     * @return The file path smart value.
     */
    @Override
    public SmartValue getValue() {
        SmartValue smartValue = new SmartValue(getValueString());
        log.debug("{} file input smart value is returned.", elementName);
        return smartValue;
    }
}
