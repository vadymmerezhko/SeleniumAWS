package org.example.testrunners;

import org.example.utils.ServerUtils;
import org.testng.Assert;

import static org.example.constants.Settings.NO_FAILURES;

/**
 * This class runs tests on AWS EC2 instance.
 */
public class AwsEc2LocalTestRunner {

    /**
     * AWS Test Runner entry point. This method is used to run tests on AWS EC2 instance.
     * @param args The array of parameters. See README.md file for more details.
     */
    public static void main(String[] args) {

        try {
            String testOutput = ServerUtils.createLocalRunServerAndRunTests();

            if (!testOutput.contains(NO_FAILURES)) {
                Assert.fail(testOutput);
            }
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
