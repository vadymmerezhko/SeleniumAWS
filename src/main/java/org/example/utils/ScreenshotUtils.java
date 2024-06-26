package org.example.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Screenshot manager class.
 */
public final class ScreenshotUtils {

    private ScreenshotUtils() {}

    /**
     * Converts screenshot data to bytes.
     * @param target The screenshot target.
     * @param data The screenshot data.
     * @return The screenshot output.
     * @param <X> The output type.
     */
    public static <X> X convertScreenshotBytes(OutputType<X> target, byte[] data) {
        if (target.equals(OutputType.BYTES)) {
            return (X)data;
        }
        else if (target.equals(OutputType.BASE64)) {
            return (X) Base64.getEncoder().encodeToString(data);
        }
        else if (target.equals(OutputType.FILE)) {
            try {
                Path tmpFilePath = Files.createTempFile("screenshot", ".png");
                File tmpFile = tmpFilePath.toFile();
                tmpFile.deleteOnExit();
                Files.write(tmpFilePath, data);
                return (X)tmpFile;
            } catch (IOException var4) {
                throw new WebDriverException(var4);
            }
        }
        throw new RuntimeException(String.format("Wrong screenshot type: %s", target));
    }
}
