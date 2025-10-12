package com.smarte2e.utils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import com.smarte2e.exceptions.SmartRuntimeException;

import java.io.File;

/**
 * Zip manager class.
 */
@Slf4j
public final class ZipFileUtils {

    private ZipFileUtils() {}

    /**
     * Unzips zipped file.
     * @param zipFilePath The zipped file path.
     * @param folderPath The unzipped file path.
     */
    public static void unzip(String zipFilePath, String folderPath) {
        DataValidator.filePathFormat(zipFilePath, "zipFilePath");
        DataValidator.filePathFormat(folderPath, "folderPath");

        unzip(zipFilePath, folderPath, null);
    }

    /**
     * Unzips zipped file protected with password.
     * @param zipFilePath The zipped file path.
     * @param folderPath The unzipped file path.
     * @param password The password.
     */
    public static void unzip(String zipFilePath, String folderPath, String password) {
        DataValidator.filePathFormat(zipFilePath, "zipFilePath");
        DataValidator.folderPathFormat(folderPath, "folderPath");

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }
            zipFile.extractAll(folderPath);
            zipFile.close();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot unzip file %s to folder %s.",
                    zipFilePath, folderPath), e);
        }
    }

    /**
     * Zips file folder.
     * @param folderPath The folder path.
     * @param zipFilePath The destination path.
     */
    public static void zip(String folderPath, String zipFilePath) {
        zip(folderPath, zipFilePath, null);
    }

    /**
     * Zips folder with password.
     * @param folderPath The folder path.
     * @param zipFilePath The zip file.
     * @param password The password.
     */
    public static void zip(String folderPath, String zipFilePath, String password) {
        DataValidator.folderPathFormat(folderPath, "folderPath");
        DataValidator.filePathFormat(zipFilePath, "zipFilePath");

        try {
            ZipFile zipFile;

            if (password != null) {
                zipFile = new ZipFile(zipFilePath, password.toCharArray());
                ZipParameters parameters = new ZipParameters();
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.AES);
                parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                parameters.setCompressionMethod(CompressionMethod.DEFLATE);
                zipFile.addFolder(new File(folderPath), parameters);
            } else {
                zipFile = new ZipFile(zipFilePath);
                zipFile.addFolder(new File(folderPath));
            }
            zipFile.close();
            log.debug("Folder {} successfully zipped to {}.",
                    folderPath, zipFilePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot zip folder %s to zip file %s.",
                    folderPath, zipFilePath), e);
        }
    }
}
