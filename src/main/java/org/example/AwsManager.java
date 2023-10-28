package org.example;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AwsManager {
    static private final int WAIT_EC2_ID_TIMEOUT = 30;
    static private final int WAIT_EC2_PUBLIC_IP_TIMEOUT = 30;
    private static final String AWS_ACCESS_ID = "AKIAXZRTVXLO44AM55Y5";
    private static final String AWS_KEY = "OBe4tL6Sy40H+jQWFs/DhgEJsW4tetjtb9jCWqN1";

    public static AWSCredentialsProvider getAwsCredentialProvider() {
        return new AWSCredentialsProvider() {
            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials(AWS_ACCESS_ID, AWS_KEY);
            }
            public void refresh() {
                // NOP
            }
        };
    }

    public static AmazonEC2 getEC2Client() {
        AWSCredentialsProvider provider = getAwsCredentialProvider();

        return AmazonEC2ClientBuilder.standard()
                .withCredentials(provider)
                .withRegion(Regions.US_WEST_1)
                .build();
    }

    public static String runEC2(
            AmazonEC2 ec2, String imageId, String keyPairName, String groupName, String userData) {

        RunInstancesRequest runInstancesRequest =
                new RunInstancesRequest();

        IamInstanceProfile profile = new IamInstanceProfile();
        profile.withArn("AWS_SSM_Role");

        runInstancesRequest.withImageId(imageId)
                .withInstanceType(InstanceType.M58xlarge)
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(keyPairName)
                .withSecurityGroups(groupName)
                .withUserData(userData);

        RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);
        return runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
    }

    public static String getEC2PublicIp(AmazonEC2 ec2, String instanceId) {
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);

        DescribeInstancesRequest request =  new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);

        DescribeInstancesResult result = ec2.describeInstances(request);
        List<Reservation> reservations = result.getReservations();
        return reservations.get(0).getInstances().get(0).getPublicIpAddress();
    }

    public static String runEC2AndEWaitForId(
            AmazonEC2 ec2, String imageId, String keyPairName, String groupName, String userData) {
        String ec2InstanceId = null;
        TimeOut timeOut = new TimeOut(WAIT_EC2_ID_TIMEOUT);
        timeOut.start();

        while (true) {
            Waiter.waitSeconds(1);
            ec2InstanceId = AwsManager.runEC2(ec2, imageId, keyPairName, groupName, userData);

            if (ec2InstanceId != null) break;
            System.out.println("Repeat getting EC2 Id");
        }
        return ec2InstanceId;
    }

    public static String waitForEC2Ip(AmazonEC2 ec2Client, String ec2InstanceId) {
        String ec2InstanceIp = null;
        TimeOut timeOut = new TimeOut(WAIT_EC2_PUBLIC_IP_TIMEOUT);
        timeOut.start();

        while (true) {
            Waiter.waitSeconds(1);
            ec2InstanceIp = AwsManager.getEC2PublicIp(ec2Client, ec2InstanceId);

            if (ec2InstanceIp != null) break;
            System.out.println("Repeat getting EC2 IP");
        }
        return ec2InstanceIp;
    }

    public static void terminateEC2(AmazonEC2 ec2, String instanceID) {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.withInstanceIds(instanceID);
        ec2.terminateInstances(request);
    }

    public static void runCommand(String instanceId, String command) {
        Map<String, List<String>> params = new HashMap<>(){{
            put("commands", new ArrayList<>(){{ add(command); }});
        }};
        int timeoutInSecs = 5;
        //You can add multiple command ids separated by commas
        Target target = new Target().withKey("InstanceIds").withValues(instanceId);
        //Create ssm client.
        //The builder could be chosen as per your preferred way of authentication
        //use withRegion for specifying your region
        AWSCredentialsProvider provider = getAwsCredentialProvider();
        AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.standard()
                .withCredentials(provider)
                .withRegion(Regions.US_WEST_1)
                .build();
        //Build a send command request
        SendCommandRequest commandRequest = new SendCommandRequest()
                .withTargets(target)
                .withDocumentName("AWS-RunShellScript")
                .withParameters(params);

        //The result has commandId which is used to track the execution further
        SendCommandResult commandResult = ssm.sendCommand(commandRequest);
        String commandId = commandResult.getCommand().getCommandId();
        //Loop until the invocation ends
        String status;
        do {
            ListCommandInvocationsRequest request = new ListCommandInvocationsRequest()
                    .withCommandId(commandId)
                    .withDetails(true);
            //You get one invocation per ec2 instance that you added to target
            //For just a single instance use get(0) else loop over the instanced
            CommandInvocation invocation = ssm.listCommandInvocations(request).getCommandInvocations().get(0);
            status = invocation.getStatus();
            if (status.equals("Success")) {
                //command output holds the output of running the command
                //eg. list of directories in case of ls
                String commandOutput = invocation.getCommandPlugins().get(0).getOutput();
                System.out.println(commandOutput);
                //Process the output
            }
            //Wait for a few seconds before you check the invocation status again
            try {
                TimeUnit.SECONDS.sleep(timeoutInSecs);
            } catch (InterruptedException e) {
                //Handle not being able to sleep
            }
        } while(status.equals("Pending") || status.equals("InProgress"));
        if (!status.equals("Success")) {
            //Command ended up in a failure
            System.out.println("Command succeeded.");
        }
    }
}
