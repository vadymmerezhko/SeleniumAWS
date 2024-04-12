package org.example.utils;

import net.lingala.zip4j.ZipFile;

import java.io.File;

public class ZipManager {

    public static void unzip(String source, String destination) {
        unzip(source, destination, null);
    }

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
