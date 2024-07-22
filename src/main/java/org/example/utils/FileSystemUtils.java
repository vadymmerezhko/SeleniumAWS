package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * File manager class.
 * Contains common file methods.
 */
@Slf4j
public final class FileSystemUtils {

    private FileSystemUtils() {}

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
            log.debug("File {} is created in folder {} with content: {}", fileName, folderPath, fileContent);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Cannot create %s/%s file.",
                    folderPath, fileName), e);
        }
    }

    /**
     * Creates file.
     * @param filePath The target folder path.
     * @param fileContent The file content.
     */
    public static synchronized void createFile(String filePath, String fileContent) {
        try {
            File file = new File(filePath);
            createFile(file.getParent(), file.getName(), fileContent);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Cannot create %s file:\n",
                    filePath), e);
        }
    }

    /**
     * Creates folder.
     * @param folderPath The target folder path.
     */
    public static synchronized void createFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new RuntimeException(String.format(
                            "Cannot create folder %s", folderPath));
                }
                log.debug("Folder {} is created.", folderPath);
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
            String fileContent = Files.readString(Paths.get(filePath));
            log.debug("File {} is read with content: {}", filePath, fileContent);
            return fileContent;
        }
        catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Cannot read from file %s\n%s", filePath, e.getMessage()));
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
                log.debug("File: {} is deleted.", filePath);
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
            log.info("Folder {} is deleted.", folderPath);
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
            log.debug("File: {} is moved to {}.", fromPath, toPath);
        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot move file %s to %s\n%s", fromPath, toPath, e.getMessage()));
        }
    }

    /**
     * Returns true if file exists or false otherwise.
     * @param filePath The file path.
     * @return The true/false flag.
     */
    public static boolean fileExists(String filePath) {
        boolean exists = new File(filePath).exists();
        log.debug("File: {} is exist={}", filePath, exists);
        return exists;
    }

    /**
     * Returns all file names in the folder.
     * @param folderPath The folder path.
     * @return The set of file names.
     */
    public static Set<String> getFileNamesInFolder(String folderPath) {
        Set<String> fileNames = new HashSet<>();

        try {
            File directory = new File(folderPath);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            fileNames.add(file.getName());
                        }
                    }
                } else {
                    return fileNames;
                }
            } else {
                throw new RuntimeException(String.format(
                        "The specified path '%s' is not a directory or does not exist.",
                        folderPath));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get file names from the folder: %s",
                    folderPath), e);
        }
        log.debug("File names in folder {} are: {}", folderPath, fileNames);
        return fileNames;
    }

    /**
     * Returns the file extension of a given file.
     * @param fileName The file name the extension
     * @return The file extension, or an empty string if no extension found.
     */
    public static String getFileExtension(String fileName) {
        String extension = "";
        try {
            // Check file name format.
            fileName = new File(fileName).getName();
            int dotIndex = fileName.lastIndexOf('.');

            if (dotIndex != -1) {
                extension = fileName.substring(dotIndex + 1).toLowerCase();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get file extension form the file name: %s",
                    fileName), e);
        }
        log.debug("File {} extension is {}.", fileName, extension);
        return extension;
    }

    /**
     * Returns the file name without extension of a given file.
     * @param fileName The file name with the extension.
     * @return The file name without extension.
     */
    public static String getFileNameWithoutExtension(String fileName) {
        try {
            // Check file name format.
            fileName = new File(fileName).getName();
            int dotIndex = fileName.lastIndexOf('.');
            String nameNoExtension = fileName;

            if (dotIndex != -1) {
                nameNoExtension = fileName.substring(0, dotIndex);
            }
            log.debug("File {} name without extension is {}.", fileName, nameNoExtension);
            return nameNoExtension;
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get file name without extension form the file name: %s",
                    fileName), e);
        }
    }
}
