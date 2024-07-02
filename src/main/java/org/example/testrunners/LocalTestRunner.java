package org.example.testrunners;

import org.example.data.Config;
import org.example.utils.*;
import org.testng.Assert;

import static org.example.constants.Settings.*;

/**
 * This class runs tests locally.
 */
public class LocalTestRunner {
    static private final String RUN_TESTS_COMMAND_TEMPLATE =
            "mvn clean test \"-DtestSuite=./src/test/resources/%s\" "+
            "\"-DthreadCount=%d\" \"-DtestMode=%s\" \"-Dbrowser=%s:%s\"";
    static private final String TEST_REPORT_FOLDER_PATH = "target/surefire-reports";
    static private final String TARGET_FOLDER_PATH = "target";

    /**
     * Local Test Runner entry point method.
     * @param params The array of parameters. See README.md for more details.
     */
    public static void main(String[] params) {
        Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
        String startDate = config.getStartDate();
        String runtTestCommandLine = String.format(
                RUN_TESTS_COMMAND_TEMPLATE,
                config.getTestngFile(),
                config.getThreadCount(),
                config.getTestMode(),
                config.getBrowserName(),
                config.getBrowserVersion());

        if (SystemUtils.isLinux()) {
            runtTestCommandLine = String.format("sudo %s", runtTestCommandLine);
        }

        String testOutput = CommandLineUtils.runCommandLine(runtTestCommandLine);
        String zipFileName = String.format(TEST_REPORT_ZIP_FILE_NAME_TEMPLATE, startDate);
        String zipFilePath = String.format("%s/%s", TARGET_FOLDER_PATH, zipFileName);

        ZipFileUtils.zipFolder(TEST_REPORT_FOLDER_PATH, zipFilePath);
        AwsUtils.uploadFileToS3(zipFilePath, TEST_REPORTS_AWS_BUCKET_NAME,
                config.getAccessKey(), config.getSecretKey());

        String logFileName = String.format(TEST_REPORT_LOG_FILE_NAME_TEMPLATE, startDate);
        FileOperationUtils.createFile(TARGET_FOLDER_PATH, logFileName, testOutput);
        String logFilePath = String.format("%s/%s", TARGET_FOLDER_PATH, logFileName);
        AwsUtils.uploadFileToS3(logFilePath, TEST_REPORTS_AWS_BUCKET_NAME,
                config.getAccessKey(), config.getSecretKey());

        DockerUtils.stopAllContainers();

        if (!testOutput.contains(NO_FAILURES)) {
            Assert.fail(testOutput);
        }
    }
}
