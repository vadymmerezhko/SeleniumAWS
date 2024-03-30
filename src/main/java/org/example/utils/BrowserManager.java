package org.example.utils;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.File;

import static org.example.constants.Browsers.*;

public class BrowserManager {

    private BrowserManager() {}

    private static final String DOWNLOAD_BIN_FOLDER_PATH = "src/test/resources/bin";
    private static final String DOWNLOAD_BROWSER_ZIP_FILE_PATH = DOWNLOAD_BIN_FOLDER_PATH + "/browser.zip";
    private static final String DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH = DOWNLOAD_BIN_FOLDER_PATH + "/webdriver.zip";
    private static final String BROWSER_BIN_FOLDER_PATH_TEMPLATE = "src/main/resources/bin/browser/%s/%s";
    private static final String WEBDRIVER_BIN_FOLDER_PATH_TEMPLATE = "src/main/resources/bin/webdriver/%s/%s";
    private static final String BROWSERS_DOWNLOAD_JSON_FILE_PATH = "src/main/resources/downloads/browsers.json";
    private static final String WEBDRIVERS_DOWNLOAD_JSON_FILE_PATH = "src/main/resources/downloads/webdrivers.json";

    public synchronized static String downloadBrowserBinary(String browserName, String browserVersion) {
        try {
            String downloadUrl = getBrowserDownloadUrl(browserName, browserVersion);
            String zipFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            String browserFolderPath = String.format(BROWSER_BIN_FOLDER_PATH_TEMPLATE, browserName, browserVersion);
            String browserBinaryFilePath = getBrowserBinaryFilePath(browserName, browserFolderPath, zipFileName);

            if (!new File(browserBinaryFilePath).exists()) {
                System.out.printf("Downloading %s:%s browser binary files...%n", browserName, browserVersion);
                createDownloadBinFolder();
                FileManager.deleteFile(DOWNLOAD_BROWSER_ZIP_FILE_PATH);
                WebDownloadManager.download(downloadUrl, DOWNLOAD_BROWSER_ZIP_FILE_PATH);
                FileManager.deleteDirectory(browserFolderPath);
                ZipManager.unzip(DOWNLOAD_BROWSER_ZIP_FILE_PATH, browserFolderPath);
                FileManager.deleteFile(DOWNLOAD_BROWSER_ZIP_FILE_PATH);
            }
            return browserBinaryFilePath;
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Cannot download %s:%s browser binary.\n%s",
                    browserName, browserVersion, e.getMessage()));
        }
    }

    public synchronized static String downloadWebDriverBinary(String browserName, String browserVersion) {
        try {
            String downloadUrl = getWebDriverDownloadUrl(browserName, browserVersion);
            String zipFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
            String webDriverFolderPath = String.format(WEBDRIVER_BIN_FOLDER_PATH_TEMPLATE, browserName, browserVersion);
            String webDriverBinaryFilePath = getWebDriversBinaryPath(browserName, webDriverFolderPath, zipFileName);

            if (!new File(webDriverBinaryFilePath).exists()) {
                System.out.printf("Downloading %s:%s WebDriver binary file...%n", browserName, browserVersion);
                createDownloadBinFolder();
                FileManager.deleteFile(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
                WebDownloadManager.download(downloadUrl, DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
                File webDriverBinaryFile = new File(webDriverBinaryFilePath);
                FileManager.deleteDirectory(webDriverBinaryFile.getParent());
                ZipManager.unzip(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH, webDriverFolderPath);
                FileManager.deleteFile(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);

                // Workaround for Chrome WebDriver zip files up to the version 114.
                if (!webDriverBinaryFile.exists()) {
                    String webDriverFileName = webDriverBinaryFile.getName();
                    String parentFolderFilePath = String.format("%s/%s", webDriverFolderPath, webDriverFileName);
                    FileManager.moveFile(parentFolderFilePath, webDriverBinaryFilePath);
                }
            }
            return webDriverBinaryFilePath;
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Cannot download %s:%s WebDriver binary.\n%s",
                    browserName, browserVersion, e.getMessage()));
        }
    }

    private static void createDownloadBinFolder() {
        File binFolder = new File(DOWNLOAD_BIN_FOLDER_PATH);
        if (!binFolder.exists()) {
            if (!binFolder.mkdir()) {
                throw new RuntimeException("Cannot create downloads bin folder: " + DOWNLOAD_BIN_FOLDER_PATH);
            }
        }
    }

    private static String getBrowserDownloadUrl(String browserName, String browserVersion) {
        return getWebDriverDownloadUrl(browserName, browserVersion, BROWSERS_DOWNLOAD_JSON_FILE_PATH);
    }

    private static String getWebDriverDownloadUrl(String browserName, String browserVersion) {
        return getWebDriverDownloadUrl(browserName, browserVersion, WEBDRIVERS_DOWNLOAD_JSON_FILE_PATH);
    }

    private static String getWebDriverDownloadUrl(String browserName, String browserVersion, String jsonFilePath) {
        String platform = SystemManager.getPlatform();
        String dataModel = SystemManager.getDataModel();

        JSONObject browsers = new JSONObject(FileManager.readFile(jsonFilePath));
        JSONObject browser = browsers.getJSONObject(browserName);
        JSONObject platformDataModel = browser.getJSONObject(platform + dataModel);
        return platformDataModel.getString(browserVersion);
    }

    private static String getBrowserBinaryFilePath(String browserName, String browserFolderPath, String zipFileName) {
        String browserFolderName = FilenameUtils.removeExtension(zipFileName);
        String browserFileName;

        switch (browserName) {
            case CHROME -> browserFileName = SystemManager.isWindows() ? "chrome.exe" : "chrome";
            case FIREFOX -> browserFileName = SystemManager.isWindows() ? "firefox.exe" : "firefox";
            case EDGE -> browserFileName = SystemManager.isWindows() ? "msedge.exe" : "msedge";
            default -> throw new RuntimeException("Cannot get browser binary file path for " + browserName);
        }
        return String.format("%s/%s/%s", browserFolderPath, browserFolderName, browserFileName);
    }

    private static String getWebDriversBinaryPath(String browserName, String browserFolderPath, String zipFileName) {
        String webDriverFolderName =  zipFileName.split("\\.")[0];
        String webDriverFileName;

        switch (browserName) {
            case CHROME -> webDriverFileName = SystemManager.isWindows() ? "chromedriver.exe" : "chromedriver";
            case FIREFOX -> webDriverFileName = SystemManager.isWindows() ? "geckodriver.exe" : "geckodriver";
            case EDGE -> webDriverFileName = SystemManager.isWindows() ? "msedgedriver.exe" : "msedgedriver";
            default -> throw new RuntimeException("Cannot get WebDriver binary file path for " + browserName);
        }
        return String.format("%s/%s/%s", browserFolderPath, webDriverFolderName, webDriverFileName);
    }
}
