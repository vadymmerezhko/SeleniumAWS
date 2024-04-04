package org.example.utils;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.lambda.model.TooManyRequestsException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AwsManager {
    static private final int WAIT_EC2_ID_TIMEOUT = 30;
    static private final int WAIT_EC2_PUBLIC_IP_TIMEOUT = 30;
    static private final int AWS_LAMBDA_RETRY_COUNT = 20;
    static private final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    static private final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    static private final ConcurrentMap<Long, AWSLambda> amsLambdaClientMam = new ConcurrentHashMap<>();


    public static AmazonEC2 getEC2Client() {
        AWSCredentialsProvider provider = new EnvironmentVariableCredentialsProvider();

        return AmazonEC2ClientBuilder.standard()
                .withCredentials(provider)
                .withRegion(Regions.US_WEST_1)
                .build();
    }

    public static String runEC2(AmazonEC2 ec2, int threadCount, String imageId,
                                String keyPairName, String groupName, String userData) {
        RunInstancesRequest runInstancesRequest =
                new RunInstancesRequest();

        IamInstanceProfile profile = new IamInstanceProfile();
        profile.withArn("AWS_SSM_Role");

        runInstancesRequest.withImageId(imageId)
                .withInstanceType(getEc2InstanceType(threadCount))
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

    public static String runEC2AndEWaitForId(AmazonEC2 ec2, int threadCount, String imageId,
                                             String keyPairName, String groupName, String userData) {
        String ec2InstanceId;
        TimeOut timeOut = new TimeOut(WAIT_EC2_ID_TIMEOUT);
        timeOut.start();

        do {
            Waiter.waitSeconds(1);
            ec2InstanceId = AwsManager.runEC2(ec2, threadCount, imageId, keyPairName, groupName, userData);
        } while (ec2InstanceId == null);

        return ec2InstanceId;
    }

    public static String waitForEC2Ip(AmazonEC2 ec2Client, String ec2InstanceId) {
        String ec2InstanceIp;
        TimeOut timeOut = new TimeOut(WAIT_EC2_PUBLIC_IP_TIMEOUT);
        timeOut.start();

        do {
            Waiter.waitSeconds(1);
            ec2InstanceIp = AwsManager.getEC2PublicIp(ec2Client, ec2InstanceId);
        } while (ec2InstanceIp == null);

        return ec2InstanceIp;
    }

    public static void terminateEC2(AmazonEC2 ec2, String instanceID) {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.withInstanceIds(instanceID);
        ec2.terminateInstances(request);
    }

    public static String invokeLambdaFunction(String functionName, String inputJsonString) {
        try {
            AWSLambda client = getAwsLambdaClient();
            String lambdaInput = ConverterUtils.convertJsonStringToLambdaInput(inputJsonString);
            InvokeRequest request = new InvokeRequest()
                    .withFunctionName(functionName)
                    .withPayload(lambdaInput);
            InvokeResult result;
            int tryCount = 1;

            // Retry AWS Lambda function in case of "Too many requests" error.
            do {
                try {
                    result = client.invoke(request);
                    break;
                }
                catch (TooManyRequestsException e) {
                    if (tryCount == AWS_LAMBDA_RETRY_COUNT) {
                        throw e;
                    }
                    System.out.printf("Retry %d for thread id %d%n", tryCount, Thread.currentThread().threadId());
                    tryCount++;
                }
            }
            while (true);

            if (result.getFunctionError() != null) {
                throw new RuntimeException("AWS Lambda error:\n" + result.getFunctionError());
            }

            if (result.getStatusCode() != 200) {
                throw new RuntimeException("AWS Lambda status call: " + result.getStatusCode());
            }

            String lambdaOutputJsonString = new String(result.getPayload().array());
            return ConverterUtils.convertLambdaOutputToJsonString(lambdaOutputJsonString);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static AWSLambda getAwsLambdaClient() {
        long threadId = Thread.currentThread().threadId();

        if (!amsLambdaClientMam.containsKey(threadId)) {
            String accessKey = System.getenv(AWS_ACCESS_KEY_ID);
            String secretKey = System.getenv(AWS_SECRET_ACCESS_KEY);
            BasicAWSCredentials credentials = new
                    BasicAWSCredentials(accessKey, secretKey);
            AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.US_WEST_1);
            AWSLambda client = builder.build();
            amsLambdaClientMam.put(threadId, client);
            return client;
        }
        else {
            return amsLambdaClientMam.get(threadId);
        }
    }

    private static InstanceType getEc2InstanceType(int threadCount) {

        if (threadCount > 0 && threadCount <= 2) {
            return InstanceType.M5Large;
        }
        else if (threadCount > 2 && threadCount <= 4) {
            return InstanceType.M5Xlarge;
        }
        else if (threadCount > 4 && threadCount <= 8) {
            return InstanceType.M52xlarge;
        }
        else if (threadCount > 8 && threadCount <= 16) {
            return InstanceType.M54xlarge;
        }
        else if (threadCount > 16 && threadCount <= 32) {
            return InstanceType.M58xlarge;
        }
        throw new RuntimeException(
                "Thread count should be positive and less than 32.\nWrong thread count : " + threadCount);
    }
}
