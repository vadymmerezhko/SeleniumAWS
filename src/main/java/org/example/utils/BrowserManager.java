package org.example.utils;

import org.json.JSONObject;

import java.io.File;

import static org.example.constants.Browsers.*;

public class BrowserManager {

    private BrowserManager() {}

    private static final String DOWNLOAD_BROWSER_ZIP_FILE_PATH = "src/test/resources/bin/browser.zip";
    private static final String DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH = "src/test/resources/bin/webdriver.zip";
    private static final String BROWSER_BIN_FOLDER_PATH_TEMPLATE = "src/test/resources/bin/browser/%s/%s";
    private static final String WEBDRIVER_BIN_FOLDER_PATH_TEMPLATE = "src/test/resources/bin/webdriver/%s/%s";
    private static final String BROWSERS_DOWNLOAD_JSON_FILE_PATH = "src/test/resources/downloads/browsers.json";
    private static final String WEBDRIVERS_DOWNLOAD_JSON_FILE_PATH = "src/test/resources/downloads/webdrivers.json";

    public synchronized static String downloadBrowserBinary(String browserName, String browserVersion) {
        String downloadUrl = getBrowserDownloadUrl(browserName, browserVersion);
        String zipFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        String browserFolderPath = String.format(BROWSER_BIN_FOLDER_PATH_TEMPLATE, browserName, browserVersion);
        String browserBinaryFilePath = getBrowserBinaryFilePath(browserName, browserFolderPath, zipFileName);

        if (!new File(browserBinaryFilePath).exists()) {
            System.out.printf("Downloading %s:%s browser binary files...%n", browserName, browserVersion);
            FileManager.deleteFile(DOWNLOAD_BROWSER_ZIP_FILE_PATH);
            WebDownloadManager.download(downloadUrl, DOWNLOAD_BROWSER_ZIP_FILE_PATH);
            FileManager.deleteDirectory(browserFolderPath);
            ZipManager.unzip(DOWNLOAD_BROWSER_ZIP_FILE_PATH, browserFolderPath);
            FileManager.deleteFile(DOWNLOAD_BROWSER_ZIP_FILE_PATH);
        }
        return browserBinaryFilePath;
    }

    public synchronized static String downloadWebDriverBinary(String browserName, String browserVersion) {
        String downloadUrl = getWebDriverDownloadUrl(browserName, browserVersion);
        String zipFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1);
        String webDriverFolderPath = String.format(WEBDRIVER_BIN_FOLDER_PATH_TEMPLATE, browserName, browserVersion);
        String webDriverBinaryFilePath = getWebDriversBinaryPath(browserName, webDriverFolderPath, zipFileName);

        if (!new File(webDriverBinaryFilePath).exists()) {
            System.out.printf("Downloading %s:%s WebDriver binary file...%n", browserName, browserVersion);
            FileManager.deleteFile(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
            WebDownloadManager.download(downloadUrl, DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
            FileManager.deleteDirectory(webDriverFolderPath);
            ZipManager.unzip(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH, webDriverFolderPath);
            FileManager.deleteFile(DOWNLOAD_WEBDRIVER_ZIP_FILE_PATH);
        }
        return webDriverBinaryFilePath;
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
        String browserFolderName =  zipFileName.split("\\.")[0];
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
