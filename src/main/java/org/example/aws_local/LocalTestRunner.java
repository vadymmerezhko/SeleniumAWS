package org.example.aws_local;

import org.example.data.Config;
import org.example.utils.*;
import org.testng.Assert;

import java.text.SimpleDateFormat;

import static org.example.constants.Settings.*;

public class LocalTestRunner {
    static private final String RUN_TESTS_COMMAND_TEMPLATE =
            "mvn clean test \"-DtestSuite=./src/test/resources/%s\" "+
            "\"-DthreadCount=%d\" \"-DtestMode=%s\" \"-Dbrowser=%s:%s\"";

    static private final String TEST_REPORT_FOLDER_PATH = "target/surefire-reports";
    static private final String TEST_REPORT_ZIP_FOLDER_PATH = "target";
    static private final String TEST_REPORT_ZIP_FILE_NAME_TEMPLATE = "test-report-%s.zip";
    static private final String NO_FAILURES = "Failures: 0, Errors: 0";

    public static void main(String[] params) {
        Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
        String runtTestCommandLine = String.format(
                RUN_TESTS_COMMAND_TEMPLATE,
                config.getTestngFile(),
                config.getThreadCount(),
                config.getTestMode(),
                config.getBrowserName(),
                config.getBrowserVersion());

        if (SystemManager.isLinux()) {
            runtTestCommandLine = String.format("sudo %s", runtTestCommandLine);
        }

        String testOutput = CommandLineExecutor.runCommandLine(runtTestCommandLine);
        String dateStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
        String zipFileName = String.format(TEST_REPORT_ZIP_FILE_NAME_TEMPLATE, dateStamp);
        String zipFilePath = String.format("%s/%s", TEST_REPORT_ZIP_FOLDER_PATH, zipFileName);

        ZipManager.zipFolder(TEST_REPORT_FOLDER_PATH, zipFilePath);
        AwsManager.uploadFileToS3(zipFilePath, TEST_REPORTS_AWS_BUCKET_NAME);

        if (!testOutput.contains(NO_FAILURES)) {
            Assert.fail(testOutput);
        }
    }
}
