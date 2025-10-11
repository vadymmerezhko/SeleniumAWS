package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.example.exceptions.SmartRuntimeException;

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
    private static final String FAKE_PATH = "fakepath";

    private FileSystemUtils() {}

    /**
     * Creates file.
     * @param folderPath The target folder path.
     * @param fileName The target file name.
     * @param fileContent The file content.
     */
    public static synchronized void createFile(String folderPath, String fileName, String fileContent) {
        try {
            DataValidator.folderPath(folderPath, "folderPath");
            DataValidator.filePath(fileName, "fileName");
            DataValidator.notNull(fileContent, "fileContent");

            Writer fileWriter = new FileWriter(String.format("%s/%s", folderPath, fileName), false);
            BufferedWriter br = new BufferedWriter(fileWriter);
            br.write(fileContent);
            br.close();
            log.debug("File {} is created in folder {} with content: {}", fileName, folderPath, fileContent);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot create %s/%s file.",
                    folderPath, fileName), e);
        }
    }

    /**
     * Creates file.
     * @param filePath The target folder path.
     * @param fileContent The file content.
     */
    public static synchronized void createFile(String filePath, String fileContent) {
        DataValidator.filePath(filePath, "filePath");
        DataValidator.notNull(fileContent, "fileContent");

        try {
            File file = new File(filePath);
            createFile(file.getParent(), file.getName(), fileContent);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot create %s file with content: '%s'.",
                    filePath, fileContent), e);
        }
    }

    /**
     * Creates folder.
     * @param folderPath The target folder path.
     */
    public static synchronized void createFolder(String folderPath) {
        DataValidator.folderPath(folderPath, "folderPath");

        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new SmartRuntimeException(String.format(
                            "Cannot create folder %s.", folderPath));
                }
                log.debug("Folder {} is created.", folderPath);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot create folder %s.", folderPath), e);
        }
    }

    /**
     * Reads and returns file content.
     * @param filePath The file path.
     * @return The file content.
     */
    public static synchronized String readFile(String filePath) {
        DataValidator.filePath(filePath, "filePath");

        try {
            String fileContent = Files.readString(Paths.get(filePath));
            log.debug("File {} is read with content: {}", filePath, fileContent);
            return fileContent;
        }
        catch (IOException e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot read from file %s.", filePath), e);
        }
    }

    /**
     * Deletes file by its path.
     * @param filePath The file path.
     */
    public static synchronized void deleteFile(String filePath) {
        DataValidator.filePath(filePath, "filePath");
        try {
            // Validate file path.
            Paths.get(filePath);
            File file = new File(filePath);

            if (file.exists()) {
                if (file.delete()) {
                    log.debug("File: {} is deleted or was not present.", filePath);
                } else {
                    throw new SmartRuntimeException(String.format(
                            "Cannot delete file: %s.", filePath));
                }
            }
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot delete file: %s.", filePath), e);
        }
    }

    /**
     * Deltes file directory by its path.
     * @param folderPath The directory path.
     */
    public static synchronized void deleteFolder(String folderPath) {
        DataValidator.folderPath(folderPath, "folderPath");

        File directory = new File(folderPath);
        try {
            FileUtils.deleteDirectory(directory);
            log.info("Folder {} is deleted.", folderPath);
        }
        catch (IOException e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot delete folder %s." , folderPath), e);
        }
    }

    /**
     * Moves file from source to target path.
     * @param fromPath The source path.
     * @param toPath The target path.
     */
    public static synchronized void moveFile(String fromPath, String toPath) {
        DataValidator.filePath(fromPath, "fromPath");
        DataValidator.filePath(toPath, "toPath");

        try {
            FileUtils.moveFile(FileUtils.getFile(fromPath), FileUtils.getFile(toPath));
            log.debug("File: {} is moved to {}.", fromPath, toPath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(
                    String.format("Cannot move file %s to %s.",
                            fromPath, toPath), e);
        }
    }

    /**
     * Returns true if file exists or false otherwise.
     * @param filePath The file path.
     * @return The true/false flag.
     */
    public static boolean fileExists(String filePath) {
        DataValidator.folderPath(filePath, "filePath");

        boolean exists = new File(filePath).exists();
        log.debug("File: {} exists: {}", filePath, exists);
        return exists;
    }

    /**
     * Returns all file names in the folder.
     * @param folderPath The folder path.
     * @return The set of file names.
     */
    public static Set<String> getFileNamesInFolder(String folderPath) {
        DataValidator.folderPath(folderPath, "folderPath");
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
                throw new SmartRuntimeException(String.format(
                        "The specified path '%s' is not a directory or does not exist.",
                        folderPath));
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
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
        DataValidator.filePath(fileName, "fileName");

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
            throw new SmartRuntimeException(String.format(
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
        DataValidator.filePath(fileName, "fileName");

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
            throw new SmartRuntimeException(String.format(
                    "Cannot get file name without extension form the file name: %s",
                    fileName), e);
        }
    }

    /**
     * Recursively searches for the file in the folder
     * and relative file path string.
     * @param folderPath The folder path to search in.
     * @param fileName The name of the file to search for.
     * @return The relative file path string if found
     * or null otherwise.
     */
    private static String recursivelyFindFileInFolder(String folderPath, String fileName) {
        DataValidator.folderPathExists(folderPath, "folderPath");
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        String filePath = null;

        try {
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Recursively search in subdirectories
                        filePath = recursivelyFindFileInFolder(file.getPath(), fileName);
                    }
                    else if (fileName.equals(file.getName())) {
                        filePath = file.getPath();
                    }
                    if (filePath != null) {
                        break;
                    }
                }
            }
            if (filePath != null) {
                log.debug("File '{}' is found in folder '{}': {}",
                        fileName, folderPath, filePath);
            }
            else {
                log.debug("No file '{}' is found in folder '{}': {}",
                        fileName, folderPath, filePath);
            }
            return filePath;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot find '%s' file in folder: %s",
                    fileName, folderPath), e);
        }
    }

    /**
     * Returns the folder path for the current working directory.
     * @return The current folder path as a String.
     */
    public static String getCurrentFolderPath() {
        return normalizeFilePathString(System.getProperty("user.dir"));
    }

    /**
     * Normalizes file path string - replaces
     * Windows slashes with Unix slashes and replaces "fakepath"
     * with actual file path found by file name in current
     * directory and all directories child directories.
     * This is a workaround for the case, when "fakepath" directory
     * is returned as file input value by WebDriver for security purpose.
     * @param filePath The file path to normalize.
     * @return The normalized file path.
     */
    public static String normalizeFilePathString(String filePath) {
        DataValidator.notNull(filePath, "filePath");

        // Recursively searches for the file in the current folder
        // if file path contains "fakepath" substring - workaround for RemoteWebDriver
        if (filePath.contains(FAKE_PATH)) {
            String folderPath = FileSystemUtils.getCurrentFolderPath();
            File file = new File(filePath);
            String fileName = file.getName();
            filePath = recursivelyFindFileInFolder(folderPath, fileName);

            if (filePath == null) {
                throw new SmartRuntimeException(String.format(
                        "Cannot recursively find %s file in folder: %s",
                        fileName, folderPath));
            }
        }
        // Replace windows slashes with Unix slashes
        String normalizedFilePath = filePath.replace("\\", "/");
        log.debug("""
                File path is normalized.
                Input: {}
                Output: {}
                """.stripIndent(),
                filePath,
                normalizedFilePath);
        return normalizedFilePath;
    }
}
