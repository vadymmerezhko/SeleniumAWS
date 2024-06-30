package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File manager class.
 * Contains common file methods.
 */
@Slf4j
public class FileManager {

    /**
     * Creates file.
     * @param folderPath The target folder path.
     * @param fileName The target file name.
     * @param fileContent The file content.
     */
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

    /**
     * Creates folder.
     * @param folderPath The target folder path.
     */
    public static synchronized void createFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.mkdirs()) {
                throw new RuntimeException(String.format("Cannot create folder %s", folderPath));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads and returns file content.
     * @param filePath The file path.
     * @return The file content.
     */
    public static synchronized String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Cannot read from file %s\n%s", filePath, e.getMessage()));
        }
    }

    /**
     * Deletes file by its path.
     * @param filePath The file path.
     */
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

    /**
     * Deltes file directory by its path.
     * @param folderPath The directory path.
     */
    public static synchronized void deleteFolder(String folderPath) {
        File directory = new File(folderPath);
        try {
            FileUtils.deleteDirectory(directory);
            log.info("Deleted the directory: {}", folderPath);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Cannot delete folder %s\n%s" , folderPath, e.getMessage()));
        }
    }

    /**
     * Moves file from source to target path.
     * @param fromPath The source path.
     * @param toPath The target path.
     */
    public static synchronized void moveFile(String fromPath, String toPath) {
        try {
            FileUtils.moveFile(FileUtils.getFile(fromPath), FileUtils.getFile(toPath));
        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot move file %s to %s\n%s", fromPath, toPath, e.getMessage()));
        }
    }
}
