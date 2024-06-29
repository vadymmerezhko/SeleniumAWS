package org.example.utils;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageUtils {

    private ImageUtils() {}

    public static String convertPngToJpg(String pngFilePath) {
        try {
            File pngFile = new File(pngFilePath);
            BufferedImage pngImage = ImageIO.read(pngFile);
            int height = pngImage.getHeight();
            int width = pngImage.getWidth();
            BufferedImage jpgImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            jpgImage.createGraphics().drawImage(
                    pngImage,
                    new AffineTransform(1f, 0f, 0f, 1f, 0, 0),
                    null);
            String jpgFilePath = pngFilePath.replace(".png", ".jpg");
            ImageIO.write(jpgImage, "jpg", new File(jpgFilePath));
            return jpgFilePath;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
