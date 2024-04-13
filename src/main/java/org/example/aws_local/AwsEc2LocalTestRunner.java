package org.example.aws_local;

import org.example.utils.ServerManager;
import org.testng.Assert;

import static org.example.constants.Settings.NO_FAILURES;

public class AwsEc2LocalTestRunner {

    public static void main(String[] args) {

        try {
            String testOutput = ServerManager.createLocalRunServerAndRunTests();

            if (!testOutput.contains(NO_FAILURES)) {
                Assert.fail(testOutput);
            }
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
