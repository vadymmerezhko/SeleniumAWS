package org.example.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File manager class.
 * Contains common file methods.
 */
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
                System.out.printf("Deleted the file: %s%n", filePath);
            } else {
                throw new RuntimeException("Cannot delete file: " + filePath);
            }
        }
    }

    /**
     * Deltes file directory by its path.
     * @param dirPath The directory path.
     */
    public static synchronized void deleteDirectory(String dirPath) {
        File directory = new File(dirPath);
        try {
            FileUtils.deleteDirectory(directory);
            System.out.printf("Deleted the directory: %s%n", dirPath);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Cannot delete directory %s\n%s" , dirPath, e.getMessage()));
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

    /**
     * Returns current folder path.
     * @return The current folder path.
     */
    public static String getCurrentFolder() {
        Path currentRelativePath = Paths.get("");
        return currentRelativePath.toAbsolutePath().toString();
    }
}
