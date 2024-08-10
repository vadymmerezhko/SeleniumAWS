package org.example.unit;

import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ImageUtils;
import org.openqa.selenium.*;
import org.testng.annotations.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.example.constants.Settings.TEST_WEBSITE_URL;
import static org.testng.Assert.*;

public class ImageUtilsTest {

    private WebDriver driver;
    private WebElement element;

    @BeforeMethod
    public void setUp() {
        driver = WebDriverFactory.getDriver();

        driver.get(TEST_WEBSITE_URL);
        element = driver.findElement(By.cssSelector("button[type='Submit']"));
    }

    @AfterClass
    public void tearDown() {
        WebDriverFactory.quiteAllBrowsersAndServers();
    }

    @Test
    public void testReadBufferImageFromFile() throws Exception {
        Path tempFile = Files.createTempFile("testImage", ".png");
        BufferedImage dummyImage = ImageUtils.createDummyImage(10, 10);
        ImageUtils.saveBufferedImageToPngFile(dummyImage, tempFile.toString());

        BufferedImage result = ImageUtils.readBufferImageFromFile(tempFile.toString());
        assertNotNull(result);
        assertEquals(result.getWidth(), 10);
        assertEquals(result.getHeight(), 10);

        Files.delete(tempFile);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testReadBufferImageFromFileInvalidPath() {
        ImageUtils.readBufferImageFromFile("invalid_path_:");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testReadBufferImageFromFileBlankPath() {
        ImageUtils.readBufferImageFromFile(" ");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testReadBufferImageFromFileNullPath() {
        ImageUtils.readBufferImageFromFile(null);
    }

    @Test
    public void testGetWebElementBufferedImage() throws Exception {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        BufferedImage fullImg = ImageIO.read(new ByteArrayInputStream(screenshot));

        Point point = element.getLocation();
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();

        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);

        BufferedImage result = ImageUtils.getWebElementBufferedImage(element);
        assertNotNull(result);
        assertEquals(result.getWidth(), eleScreenshot.getWidth());
        assertEquals(result.getHeight(), eleScreenshot.getHeight());
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testGetWebElementBufferedImageInvalidElement() {
        ImageUtils.getWebElementBufferedImage(null);
    }

    @Test
    public void testCreateDummyImage() {
        BufferedImage result = ImageUtils.createDummyImage(100, 100);
        assertNotNull(result);
        assertEquals(result.getWidth(), 100);
        assertEquals(result.getHeight(), 100);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCreateDummyImageInvalidSize() {
        ImageUtils.createDummyImage(-1, -1);
    }

    @Test
    public void testCompareImages() {
        BufferedImage img1 = ImageUtils.createDummyImage(100, 100);
        BufferedImage img2 = ImageUtils.createDummyImage(100, 100);
        boolean result = ImageUtils.compareImages(img1, img2, 90, 90);
        assertFalse(result);
    }

    @Test
    public void testScaleImage() {
        BufferedImage img = ImageUtils.createDummyImage(200, 200);
        BufferedImage scaledImg = ImageUtils.scaleImage(img, 100, 100);
        assertEquals(scaledImg.getWidth(), 100);
        assertEquals(scaledImg.getHeight(), 100);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testScaleImageInvalidSize() {
        BufferedImage img = ImageUtils.createDummyImage(200, 200);
        ImageUtils.scaleImage(img, -1, -1);
    }

    @Test
    public void testSaveBufferedImageToPngFile() throws Exception {
        BufferedImage img = ImageUtils.createDummyImage(100, 100);
        Path tempFile = Files.createTempFile("testImage", ".png");

        ImageUtils.saveBufferedImageToPngFile(img, tempFile.toString());
        assertTrue(Files.exists(tempFile));

        BufferedImage loadedImg = ImageUtils.readBufferImageFromFile(tempFile.toString());
        assertNotNull(loadedImg);
        assertEquals(loadedImg.getWidth(), 100);
        assertEquals(loadedImg.getHeight(), 100);

        Files.delete(tempFile);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSaveBufferedImageToPngFileInvalidPath() {
        BufferedImage img = ImageUtils.createDummyImage(100, 100);
        ImageUtils.saveBufferedImageToPngFile(img, ":invalid_path.png");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSaveBufferedImageToPngFileNullPath() {
        BufferedImage img = ImageUtils.createDummyImage(100, 100);
        ImageUtils.saveBufferedImageToPngFile(img, null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSaveBufferedImageToPngFileBlankPath() {
        BufferedImage img = ImageUtils.createDummyImage(100, 100);
        ImageUtils.saveBufferedImageToPngFile(img, "");
    }
}