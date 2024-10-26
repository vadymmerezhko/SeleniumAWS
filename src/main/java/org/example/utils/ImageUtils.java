package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.ui.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.openqa.selenium.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;
import java.util.List;

import static org.example.constants.Settings.*;

/**
 * Image utils class class.
 */
@Slf4j
public final class ImageUtils {

    private ImageUtils() {}

    /**
     * Reads buffered image from file.
     * @param imageFilePath The image file path.
     * @return The buffered image.
     */
    public static BufferedImage readBufferImageFromFile(String imageFilePath) {
        DataValidationUtils.validateFilePathFormat(imageFilePath, "imageFilePath");
        BufferedImage image = null;

        try {
            if (imageFilePath != null) {
                image = ImageIO.read(new File(imageFilePath));
            }
            if (image == null) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read buffered image from file %s.", imageFilePath));
            }
            log.debug("Buffer image is read from file.");
            return image;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot read image from file %s.", imageFilePath), e);
        }
    }

    /**
     * Gets web element buffered image.
     * @param element The web element.
     * @return The buffed image.
     */
    public static BufferedImage getWebElementBufferedImage(WebElement element) {
        DataValidationUtils.validateNotNull(element, "element");

        try {
            // Capture the screenshot of the WebElement as a byte array
            byte[] elementScreenshotBytes = element.getScreenshotAs(OutputType.BYTES);

            // Convert the byte array to a BufferedImage
            BufferedImage elementBufferedImage = ImageIO.read(new ByteArrayInputStream(elementScreenshotBytes));

            // Scale element size.
            Dimension elementSize = element.getSize();
            BufferedImage adjustedBufferedImage = scaleImage(elementBufferedImage,
                    elementSize.width, elementSize.height);

            log.debug("Web element {} buffered image returned with adjusted size.", element);
            return adjustedBufferedImage;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot %s web element get buffered image.", element), e);
        }
    }

    /**
     * Finds web elements by image.
     * @param elementImage The web element buffred image.
     * @param parent The parent element (can be null)/
     * @param colorsThreshold The colors threshold.
     * @param pixelsThreshold The pixels threshold.
     * @return The found web elements.
     */
    public static List<WebElement> findWebElementsByBufferedImage(
            BufferedImage elementImage,
            WebElement parent,
            int colorsThreshold,
            int pixelsThreshold,
            int sizeThresholdPixels) {
        DataValidationUtils.validateRange(colorsThreshold,
                MIN_COLOURS_THRESHOLD_PERCENTAGE, 100, "colorsThreshold");
        DataValidationUtils.validateRange(pixelsThreshold,
                MIN_PIXELS_THRESHOLD_PERCENTAGE, 100, "pixelsThreshold");
        DataValidationUtils.validateRange(sizeThresholdPixels,
                0, MAX_SIZE_THRESHOLD_PIXELS, "sizeThresholdPixels");

        WebDriver driver = WebDriverFactory.getDriver();
        List<WebElement> foundElements = new ArrayList<>();

        try {
            if (parent == null) {
                parent = driver.findElement(By.cssSelector("body"));
            }
            List<WebElement> childElements = parent.findElements(By.xpath("//*"));
            List<WebElement> targetSizeElements = new ArrayList<>();
            Dimension elementSize = new Dimension(elementImage.getWidth(), elementImage.getHeight());

            for (WebElement childElement : childElements) {
                Dimension childElementSize = childElement.getSize();

                if (compareSizes(elementSize, childElementSize, 0)) {
                    targetSizeElements.add(childElement);
                }
            }
            // Search for element with fuw pixels size difference.
            if (targetSizeElements.isEmpty()) {
                for (WebElement childElement : childElements) {
                    Dimension childElementSize = childElement.getSize();

                    if (compareSizes(elementSize, childElementSize, sizeThresholdPixels)) {
                        targetSizeElements.add(childElement);
                    }
                }
            }
            if (targetSizeElements.size() == 1) {
                BufferedImage targetElementImage = getWebElementBufferedImage(targetSizeElements.get(0));
                Dimension targetSize = new Dimension(targetElementImage.getWidth(), targetElementImage.getHeight());

                // Adjust expected element image to target element size if needed.
                if (!compareSizes(elementSize, targetSize, 0)) {
                    elementImage = scaleImage(elementImage,
                            targetElementImage.getWidth(), targetElementImage.getHeight());
                }
                if (compareImages(elementImage, targetElementImage, colorsThreshold, pixelsThreshold)) {
                    foundElements.add(targetSizeElements.get(0));
                }
            }
            log.debug("%d web elements found by buffered image: {}", foundElements);
            return foundElements;
        }
        catch (SmartRuntimeException e) {
            log.debug("Cannot find web elements by buffered image.", e);
            return foundElements;
        }
    }

    /**
     * Creates dummy image with target size.
     * @param width The image width.
     * @param height The image height.
     * @return The random image.
     */
    public static BufferedImage createDummyImage(int width, int height) {
        try {
            DataValidationUtils.validateRange(width, 0, 1000, "width");
            DataValidationUtils.validateRange(height, 0, 1000, "height");

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            Random random = new Random();

            // Fill the image with random colors
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Generate a random color
                    Color randomColor = new Color(
                            random.nextInt(256),
                            random.nextInt(256),
                            random.nextInt(256));
                    image.setRGB(x, y, randomColor.getRGB());
                }
            }
            // Dispose of the graphics context
            graphics.dispose();
            log.debug("Dummy image with size {}:{} created.", width, height);
            return image;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot generate dummy image with size %d:%d.", width, height));
        }
    }

    /**
     * Compares two buffered images.
     * @param expectedImage The expected image.
     * @param actualImage The actual image.
     * @param colorsThreshold The colors mismatch threshold %.
     * @param pointsThreshold The pixels mismathc threshold %.
     * @return The true flag if images are equal,
     * or false otherwise.
     */
    public static boolean compareImages(BufferedImage expectedImage, BufferedImage actualImage,
                                        int colorsThreshold, int pointsThreshold) {

        DataValidationUtils.validateNotNull(expectedImage, "expectedImage");
        DataValidationUtils.validateNotNull(actualImage, "actualImage");
        DataValidationUtils.validateRange(colorsThreshold,
                MIN_COLOURS_THRESHOLD_PERCENTAGE, 100, "colorsThreshold");
        DataValidationUtils.validateRange(pointsThreshold,
                MIN_PIXELS_THRESHOLD_PERCENTAGE, 100, "pointsThreshold");

        if (expectedImage.getWidth() != actualImage.getWidth() ||
            expectedImage.getHeight() != actualImage.getHeight()) {
            log.debug("""
                    Expected and actual images have different sizes:
                    Expected: %d:%d"
                    Actual: %d:%d"
                    Result: Not equal.
                    """.stripIndent(),
                    expectedImage.getWidth(),
                    expectedImage.getHeight(),
                    actualImage.getWidth(),
                    actualImage.getHeight());
            return false;
        }
        try {
            int failedPixels = 0;
            int width = actualImage.getWidth();
            int height = actualImage.getHeight();
            int square = width * height;
            int maxFailedPixels = square - ((square * pointsThreshold) / 100);

            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {
                    Color expectedColor = new Color(expectedImage.getRGB(x, y), true);
                    Color actualColor = new Color(actualImage.getRGB(x, y), true);

                    if (!compareColors(expectedColor, actualColor, colorsThreshold)) {
                        failedPixels++;
                    }
                    if (failedPixels > maxFailedPixels) {
                        log.debug("Two buffered images are not equal.");
                        return false;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot compare two buffered images.", e);
        }
        log.debug("Two buffered images are equal.");
        return true;
    }

    /**
     * Scales buffered image.
     * @param originalImage The original buffered image.
     * @param targetWidth The target image width (not bigger than screen width.).
     * @param targetHeight The target image height (hot bigger than screen height).
     * @return The target buffered image.
     */
    public static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        DataValidationUtils.validateNotNull(originalImage, "originalImage");
        DataValidationUtils.validateRange(targetWidth, 0, MAX_SCREEN_WIDTH, "targetWidth");
        DataValidationUtils.validateRange(targetHeight, 0, MAX_SCREEN_HEIGHT, "targetHeight");

        if (originalImage.getHeight() == targetHeight && originalImage.getWidth() == targetWidth) {
            return originalImage;
        }

        try {
            BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            g2d.dispose();
            return scaledImage;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot scale buffered image to target size %d : %d.",
                    targetWidth, targetHeight), e);
        }
    }

    /**
     * Saves buffered image to PNG file.
     * @param bufferedImage The buffered image.
     * @param imageFilePath The image file path.
     */
    public static void saveBufferedImageToPngFile(BufferedImage bufferedImage, String imageFilePath) {
        DataValidationUtils.validateNotNull(bufferedImage, "bufferedImage");
        DataValidationUtils.validateFilePathFormat(imageFilePath, "imageFilePath");

        try {
            File imageFile = new File(imageFilePath);
            ImageOutputStream ios = ImageIO.createImageOutputStream(imageFile);
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_DEFAULT); // No compression, highest quality
            writer.write(null, new javax.imageio.IIOImage(bufferedImage, null, null), param);
            ios.close();
            writer.dispose();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot save buffered image to PNG file %s.",
                    imageFilePath), e);
        }
    }

    private static Boolean compareColors(Color expectedColor, Color actualColor, int colorsThreshold) {

        if ( expectedColor.getAlpha() == 0 ) {
            return true;
        }
        int deviation;
        int maxColorValue = 256 * 3;
        int maxDeviation = maxColorValue - ((maxColorValue * colorsThreshold) / 100);

        deviation = Math.abs(expectedColor.getRed() - actualColor.getRed());
        deviation += Math.abs(expectedColor.getGreen() - actualColor.getGreen());
        deviation += Math.abs(expectedColor.getBlue() - actualColor.getBlue());

        return !(deviation > maxDeviation);
    }

    private static boolean compareSizes(Dimension expectedSize, Dimension actualSize, int threshold) {
        int xDiff = Math.abs(expectedSize.width - actualSize.width);
        int yDiff = Math.abs(expectedSize.height - actualSize.height);

        return  (xDiff <= 2 && yDiff <= threshold);
    }
}
