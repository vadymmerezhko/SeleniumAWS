package org.example.utils;


import net.lingala.zip4j.ZipFile;

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
            e.printStackTrace();
        }
    }
}
