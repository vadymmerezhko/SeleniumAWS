package com.smarte2e.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import com.smarte2e.enums.BrowserName;
import com.smarte2e.enums.DataModel;
import com.smarte2e.enums.Platform;
import com.smarte2e.exceptions.SmartRuntimeException;
import org.json.JSONObject;

import java.io.File;

/**
 * Browser manager class.
 */
@Slf4j
public final class BrowserUtils {

    private static final String DOWNLOAD_BIN_FOLDER_PATH = "src/test/resources/bin";
    private static final String DOWNLOAD_BROWSER_ZIP_FILE_PATH = DOWNLOAD_BIN_FOLDER_PATH + "/browser.zip";
    private static final String DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH = DOWNLOAD_BIN_FOLDER_PATH + "/webdriver.zip";
    private static final String BROWSER_BIN_FOLDER_PATH_TEMPLATE = "src/test/resources/bin/browser/%s/%s";
    private static final String WEBDRIVER_BIN_FOLDER_PATH_TEMPLATE = "src/test/resources/bin/webdriver/%s/%s";
    private static final String BROWSERS_DOWNLOAD_JSON_FILE_PATH = "src/main/resources/downloads/browsers.json";
    private static final String WEBDRIVERS_DOWNLOAD_JSON_FILE_PATH = "src/main/resources/downloads/webdrivers.json";

    private BrowserUtils() {}

    /**
     * Downloads browser binary file.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @return The browser binary file path.
     */
    public synchronized static String downloadBrowserBinary(BrowserName browserName, String browserVersion) {
        try {
            String downloadUrl = getBrowserDownloadUrl(browserName, browserVersion);
            String zipFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            String browserFolderPath = String.format(BROWSER_BIN_FOLDER_PATH_TEMPLATE, browserName, browserVersion);
            String browserBinaryFilePath = getBrowserBinaryFilePath(browserName, browserFolderPath, zipFileName);

            if (!new File(browserBinaryFilePath).exists()) {
                log.info("Downloading {}:{} browser binary files...", browserName, browserVersion);
                createDownloadBinFolder();
                FileSystemUtils.deleteFile(DOWNLOAD_BROWSER_ZIP_FILE_PATH);
                WebDownloadUtils.download(downloadUrl, DOWNLOAD_BROWSER_ZIP_FILE_PATH);
                FileSystemUtils.deleteFolder(browserFolderPath);
                ZipFileUtils.unzip(DOWNLOAD_BROWSER_ZIP_FILE_PATH, browserFolderPath);
                FileSystemUtils.deleteFile(DOWNLOAD_BROWSER_ZIP_FILE_PATH);
            }
            return browserBinaryFilePath;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot download %s:%s browser binary.",
                    browserName, browserVersion), e);
        }
    }

    /**
     * Downloads web driver binary file.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @return The web driver binary file path.
     */
    public synchronized static String downloadWebDriverBinary(BrowserName browserName, String browserVersion) {
        try {
            String downloadUrl = getWebDriverDownloadUrl(browserName, browserVersion);
            String zipFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            String webDriverFolderPath = String.format(WEBDRIVER_BIN_FOLDER_PATH_TEMPLATE, browserName, browserVersion);
            String webDriverBinaryFilePath = getWebDriversBinaryPath(browserName, webDriverFolderPath, zipFileName);

            if (!new File(webDriverBinaryFilePath).exists()) {
                log.info("Downloading {}:{} WebDriver binary file...", browserName, browserVersion);
                createDownloadBinFolder();
                FileSystemUtils.deleteFile(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
                WebDownloadUtils.download(downloadUrl, DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
                File webDriverBinaryFile = new File(webDriverBinaryFilePath);
                FileSystemUtils.deleteFolder(webDriverBinaryFile.getParent());
                ZipFileUtils.unzip(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH, webDriverFolderPath);
                FileSystemUtils.deleteFile(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);

                // Workaround for Chrome WebDriver zip files up to the version 114.
                if (!webDriverBinaryFile.exists()) {
                    String webDriverFileName = webDriverBinaryFile.getName();
                    String parentFolderFilePath = String.format("%s/%s", webDriverFolderPath, webDriverFileName);
                    FileSystemUtils.moveFile(parentFolderFilePath, webDriverBinaryFilePath);
                }
            }
            return webDriverBinaryFilePath;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot download %s:%s WebDriver binary.",
                    browserName, browserVersion), e);
        }
    }

    private static void createDownloadBinFolder() {
        File binFolder = new File(DOWNLOAD_BIN_FOLDER_PATH);
        if (!binFolder.exists()) {
            if (!binFolder.mkdir()) {
                throw new SmartRuntimeException(String.format(
                        "Cannot create downloads bin folder: %s",
                        DOWNLOAD_BIN_FOLDER_PATH));
            }
        }
    }

    private static String getBrowserDownloadUrl(BrowserName browserName, String browserVersion) {
        return getWebDriverDownloadUrl(browserName, browserVersion, BROWSERS_DOWNLOAD_JSON_FILE_PATH);
    }

    private static String getWebDriverDownloadUrl(BrowserName browserName, String browserVersion) {
        return getWebDriverDownloadUrl(browserName, browserVersion, WEBDRIVERS_DOWNLOAD_JSON_FILE_PATH);
    }

    private static String getWebDriverDownloadUrl(BrowserName browserName, String browserVersion, String jsonFilePath) {
        Platform platform = SystemUtils.getPlatform();
        DataModel dataModel = SystemUtils.getDataModel();

        JSONObject browsers = new JSONObject(FileSystemUtils.readFile(jsonFilePath));
        JSONObject browser = browsers.getJSONObject(browserName.toString());
        JSONObject platformDataModel = browser.getJSONObject(platform.toString() + dataModel.toString());
        return platformDataModel.getString(browserVersion);
    }

    private static String getBrowserBinaryFilePath(BrowserName browserName, String browserFolderPath, String zipFileName) {
        String browserFolderName = FilenameUtils.removeExtension(zipFileName);
        String browserFileName;

        switch (browserName) {
            case CHROME -> browserFileName = SystemUtils.isWindows() ? "chrome.exe" : "chrome";
            case FIREFOX -> browserFileName = SystemUtils.isWindows() ? "firefox.exe" : "firefox";
            case EDGE -> browserFileName = SystemUtils.isWindows() ? "msedge.exe" : "msedge";
            default -> throw new SmartRuntimeException(String.format(
                    "Cannot get browser binary file path for %s.", browserName));
        }
        return String.format("%s/%s/%s", browserFolderPath, browserFolderName, browserFileName);
    }

    private static String getWebDriversBinaryPath(BrowserName browserName, String browserFolderPath, String zipFileName) {
        String webDriverFolderName =  zipFileName.split("\\.")[0];
        String webDriverFileName;

        switch (browserName) {
            case CHROME -> webDriverFileName = SystemUtils.isWindows() ? "chromedriver.exe" : "chromedriver";
            case FIREFOX -> webDriverFileName = SystemUtils.isWindows() ? "geckodriver.exe" : "geckodriver";
            case EDGE -> webDriverFileName = SystemUtils.isWindows() ? "msedgedriver.exe" : "msedgedriver";
            default -> throw new SmartRuntimeException(String.format(
                    "Cannot get WebDriver binary file path for %s.", browserName));
        }
        return String.format("%s/%s/%s", browserFolderPath, webDriverFolderName, webDriverFileName);
    }
}
