package org.example.aws_local;

import org.example.utils.ServerManager;
import org.testng.Assert;

public class AwsEc2LocalTestRunner {

    public static void main(String[] args) {

        try {
            ServerManager.createLocalRunServerAndRunTests();
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
