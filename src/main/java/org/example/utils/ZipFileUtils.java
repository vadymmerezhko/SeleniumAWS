package org.example.utils;

import net.lingala.zip4j.ZipFile;

import java.io.File;

/**
 * Zip manager class.
 */
public final class ZipFileUtils {

    private ZipFileUtils() {}

    /**
     * Unzips zipped file.
     * @param source The zipped file path.
     * @param destination The unzipped file path.
     */
    public static void unzip(String source, String destination) {
        unzip(source, destination, null);
    }

    /**
     * Unzips zipped file protected with password.
     * @param source The zipped file path.
     * @param destination The unzipped file path.
     * @param password The password.
     */
    public static void unzip(String source, String destination, String password) {
        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(destination);
            zipFile.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zips file folder.
     * @param folderSource The folder path.
     * @param destination The destination path.
     */
    public static void zipFolder(String folderSource, String destination) {
        try {
            ZipFile zipFile = new ZipFile(destination);
            zipFile.addFolder(new File(folderSource));
            zipFile.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
