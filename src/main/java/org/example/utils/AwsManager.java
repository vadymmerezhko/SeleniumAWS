package org.example.utils;

import com.amazonaws.auth.*;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.lambda.model.TooManyRequestsException;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.AWS_REGION;
import static org.example.constants.Settings.REQUEST_HANDLER_ERROR_MSG;

/**
 * AWS manager class.
 */
@Slf4j
public class AwsManager {
    static private final int WAIT_EC2_ID_TIMEOUT = 120;
    static private final int WAIT_EC2_PUBLIC_IP_TIMEOUT = 120;
    static private final int AWS_LAMBDA_RETRY_COUNT = 20;
    static private final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    static private final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    static private final ConcurrentMap<Long, AWSLambda> amsLambdaClientMam = new ConcurrentHashMap<>();


    /**
     * Creates AWS EC2 client.
     * @return The EC2 client instance.
     */
    public static AmazonEC2 getEC2Client() {
        AWSCredentialsProvider provider = new EnvironmentVariableCredentialsProvider();

        return AmazonEC2ClientBuilder.standard()
                .withCredentials(provider)
                .withRegion(AWS_REGION)
                .build();
    }

    /**
     * Runs EC2 instance.
     * @param ec2 The EC2 client.
     * @param threadCount The max thread count.
     * @param imageId The AWS EC2 image ID.
     * @param keyPairName The AWS EC@ key pair name.
     * @param groupName The AWS EC2 group name.
     * @param userData The user data to run when EC2 starts.
     * @return The EC2 instance ID.
     */
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

    /**
     * Returns AWS EC2 instance public IP address.
     * @param ec2 The EC2 client.
     * @param instanceId The EC@ instance ID.
     * @return The EC2 public IP address.
     */
    public static String getEC2PublicIp(AmazonEC2 ec2, String instanceId) {
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);

        DescribeInstancesRequest request =  new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);

        DescribeInstancesResult result = ec2.describeInstances(request);
        List<Reservation> reservations = result.getReservations();
        return reservations.get(0).getInstances().get(0).getPublicIpAddress();
    }

    /**
     * Runs EC2 instance and waits till ID is available.
     * @param ec2 The EC2 client.
     * @param threadCount The max thread count.
     * @param imageId The EC2 image ID.
     * @param keyPairName The EC2 key pair name.
     * @param groupName The EC2 group name.
     * @param userData The EC2 user data to run when EC2 starts.
     * @return The EC2 instance ID.
     */
    public static String runEC2AndEWaitForId(AmazonEC2 ec2, int threadCount, String imageId,
                                             String keyPairName, String groupName, String userData) {
        String ec2InstanceId;
        TimeOut timeOut = new TimeOut("Wait EC2 instance ID.", WAIT_EC2_ID_TIMEOUT);
        timeOut.start();

        do {
            Waiter.waitSeconds(1);
            timeOut.checkExpired();
            ec2InstanceId = AwsManager.runEC2(ec2, threadCount, imageId, keyPairName, groupName, userData);
        } while (ec2InstanceId == null);

        return ec2InstanceId;
    }

    /**
     * Waits for EC2 instance public IP address.
     * @param ec2Client The EC2 client.
     * @param ec2InstanceId The EC2 instance ID.
     * @return The EC2 instance public IP.
     */
    public static String waitForEC2Ip(AmazonEC2 ec2Client, String ec2InstanceId) {
        String ec2InstanceIp;
        TimeOut timeOut = new TimeOut(
                String.format("Wait for EC2 instance %s IP address", ec2InstanceId),
                WAIT_EC2_PUBLIC_IP_TIMEOUT);
        timeOut.start();

        do {
            Waiter.waitSeconds(1);
            timeOut.checkExpired();
            ec2InstanceIp = AwsManager.getEC2PublicIp(ec2Client, ec2InstanceId);
        } while (ec2InstanceIp == null);

        return ec2InstanceIp;
    }

    /**
     * Terminates EC2 instance.
     * @param ec2 The EC2 client.
     * @param instanceID The EC2 instance ID.
     */
    public static void terminateEC2(AmazonEC2 ec2, String instanceID) {
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.withInstanceIds(instanceID);
        ec2.terminateInstances(request);
    }

    /**
     * Invokes AWS Lambda function by its name with JSON input parameter.
     * @param functionName The function name.
     * @param inputJsonString The JSON input string.
     * @return The JSON output string.
     */
    public static String invokeLambdaFunction(String functionName, String inputJsonString) {
        try {
            AWSLambda client = getAwsLambdaClient();
            String lambdaInput = ConverterUtils.convertJsonStringToRemoteInput(inputJsonString);
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
                    log.info("Retry {} for thread id {}", tryCount, Thread.currentThread().threadId());
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
            if (lambdaOutputJsonString.contains(REQUEST_HANDLER_ERROR_MSG)) {
                return lambdaOutputJsonString;
            }
            return ConverterUtils.convertRemoteOutputToJsonString(
                    ConverterUtils.convertRemoteOutputToJsonString(lambdaOutputJsonString));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Uploads file to AWS S3 bucket.
     * @param filePath The file path to upload.
     * @param bucketName The bucket name.
     * @param accessKey The AWS access key.
     * @param secretKey The AWS secret key.
     */
    public static void uploadFileToS3(String filePath, String bucketName, String accessKey, String secretKey) {
        try {
            File file = new File(filePath);
            String fileName = file.getName();
            AmazonS3 s3 = getAwsS3Client(accessKey, secretKey);
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
            s3.putObject(request);
        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot upload file %s to AWS S3 bucket '%s'.\n%s",
                            filePath,
                            bucketName,
                            e.getMessage()));
        }
    }

    /**
     * Downloads file to AWS S3 bucket.
     * @param fileName The file name to download.
     * @param targetFolderPath The target folder path.
     * @param bucketName The bucket name.
     * @param accessKey The AWS access key.
     * @param secretKey The AWS secret key.
     * @return The downloaded file path.
     */
    public static String downloadFileFromS3(String fileName, String targetFolderPath,
                                          String bucketName, String accessKey, String secretKey) {
        try {
            AmazonS3 s3 = getAwsS3Client(accessKey, secretKey);
            GetObjectRequest request = new GetObjectRequest(bucketName, fileName);
            S3Object s3Object = s3.getObject(request);
            byte[] data = s3Object.getObjectContent().readAllBytes();
            String fileContent = new String(data);

            FileManager.createFile(targetFolderPath, fileName, fileContent);
            File file = new File(String.format("%s/%s", targetFolderPath, fileName));
            return file.getPath();
        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot download file %s from AWS S3 bucket '%s'.\n%s",
                            fileName,
                            bucketName,
                            e.getMessage()));
        }
    }

    /**
     * Returns AWS access key value.
     * @return The access key value.
     */
    public static String getAwsAccessKey() {
        return System.getenv(AWS_ACCESS_KEY_ID);
    }

    /**
     * Returns AWS secret key value.
     * @return The secret key value.
     */
    public static String getAwsSecretKey() {
        return System.getenv(AWS_SECRET_ACCESS_KEY);
    }

    private static AWSLambda getAwsLambdaClient() {
        long threadId = Thread.currentThread().threadId();

        if (!amsLambdaClientMam.containsKey(threadId)) {
            BasicAWSCredentials credentials = new
                    BasicAWSCredentials(getAwsAccessKey(), getAwsSecretKey());
            AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(AWS_REGION);
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

    private static AmazonS3 getAwsS3Client(String accessKey, String secretKey) {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(AWS_REGION)
                    .build();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
