package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileManager {

    public static synchronized void createFile(String folderPath, String fileName, String fileContent) {
        try {
            Writer fileWriter = new FileWriter(String.format("%s/%s", folderPath, fileName), false);
            BufferedWriter br = new BufferedWriter(fileWriter);
            br.write(fileContent);
            br.close();
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Cannot create %s/%s file:\n",
                    folderPath, fileName) + e.getMessage());
        }
    }

    public static synchronized String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Cannot read from file %s\n%s", filePath, e.getMessage()));
        }
    }

    public static synchronized void deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                log.info("Deleted the file: {}", filePath);
            } else {
                throw new RuntimeException("Cannot delete file: " + filePath);
            }
        }
    }

    public static synchronized void deleteDirectory(String dirPath) {
        File directory = new File(dirPath);
        try {
            FileUtils.deleteDirectory(directory);
            log.info("Deleted the directory: {}", dirPath);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Cannot delete directory %s\n%s" , dirPath, e.getMessage()));
        }
    }

    public static synchronized void moveFile(String fromPath, String toPath) {
        try {
            FileUtils.moveFile(FileUtils.getFile(fromPath), FileUtils.getFile(toPath));
        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot move file %s to %s\n%s", fromPath, toPath, e.getMessage()));
        }
    }

    public static String getCurrentFolder() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
