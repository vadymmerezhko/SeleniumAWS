package org.example.utils;

import com.amazonaws.services.ec2.AmazonEC2;
import lombok.extern.slf4j.Slf4j;
import org.example.balancer.LoadBalancer;
import org.example.data.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Base64;

import static org.example.constants.Settings.*;

@Slf4j
public class ServerManager {
    private static String AWS_RMI_SERVER_INSTANCE_ID;
    private static String AWS_RMI_SERVER_INSTANCE_IP;
    private static final int THREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();
    private final static LoadBalancer loadBalancer = LoadBalancer.getInstance();

    public static void createServerInstances(int serverCount,
                                             int threadCount,
                                             String awsImageId,
                                             String securityKeyPairName,
                                             String securityGroupName,
                                             String userData) {
        loadBalancer.setMaxServersCount(serverCount);
        AmazonEC2 ec2 = AwsManager.getEC2Client();

        for (long i = 0; i < serverCount; i++) {
            String instanceId = AwsManager.runEC2AndEWaitForId(
                    ec2, threadCount,  awsImageId, securityKeyPairName, securityGroupName, userData);
            loadBalancer.setServerEC2Id(i, instanceId);
            String instanceIp = AwsManager.waitForEC2Ip(ec2, instanceId);
            loadBalancer.setServerEC2PublicIp(i, instanceIp);
        }
    }

    public static void terminateAllSeleniumServers() {
        AmazonEC2 ec2Client = AwsManager.getEC2Client();
        loadBalancer.getAllServersEC2Ids().forEach(ec2Id -> AwsManager.terminateEC2(ec2Client, ec2Id));
    }

    public static boolean isAddressReachable(String address, int port, int timeout) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(address, port), timeout);

            return true;
        }
        catch (IOException exception) {
            return false;
        }
        finally {
            try {
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized String createRmiServer() {
        if (AWS_RMI_SERVER_INSTANCE_IP == null) {
            try {
                Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
                String userData = String.format(RMI_SERVER_USER_DATA_TEMPLATE,
                        config.getThreadCount(),
                        config.getBrowserName(),
                        config.getBrowserVersion());
                String encodedUserData = Base64.getEncoder().encodeToString(userData.getBytes());
                AmazonEC2 ec2 = AwsManager.getEC2Client();
                AWS_RMI_SERVER_INSTANCE_ID = AwsManager.runEC2AndEWaitForId(ec2, config.getThreadCount(),
                        AWS_RMI_IMAGE_ID, SECURITY_KEY_PAIR_NAME, SECURITY_GROUP_NAME, encodedUserData);
                AWS_RMI_SERVER_INSTANCE_IP = AwsManager.waitForEC2Ip(ec2, AWS_RMI_SERVER_INSTANCE_ID);
                waitForServerAvailability(AWS_RMI_SERVER_INSTANCE_IP, getRmiServerPort(THREAD_COUNT));
                log.info("AWS EC2 RMI server is running on IP: {}", AWS_RMI_SERVER_INSTANCE_IP);
            }
            catch (Exception e) {
                terminateAwsRmiServer();
            }
        }
        return AWS_RMI_SERVER_INSTANCE_IP;
    }

    public static String getRmiServerName(int index) {
        return RMI_SERVER_NAME + index;
    }

    public static String getRmiServerName() {
        int index = getRmiServerIndex();
        return RMI_SERVER_NAME + index;
    }

    public static int getRmiServerPort(int index) {
        return RMI_SERVER_BASE_PORT + index;
    }

    public static int getRmiServerPort() {
        int index = getRmiServerIndex();
        return RMI_SERVER_BASE_PORT + index;
    }

    private static int getRmiServerIndex() {
        long threadId = Thread.currentThread().threadId();
        return (int)threadId % THREAD_COUNT + 1;
    }

    public static synchronized void waitForServerAvailability(String serverIP, int port) {
        TimeOut timeOut = new TimeOut(SERVER_WAIT_TIMEOUT_SECONDS);
        timeOut.start();
        log.info("Waiting for server availability: {}:{}...", serverIP, port);

        while (true) {
            Waiter.waitSeconds(1);
            if (ServerManager.isAddressReachable(serverIP, port, 15000)) {
                log.info("Server {}:{} is available.", serverIP, port);
                break;
            }
        }
    }

    public static synchronized void waitForServerUnavailability(String serverIP, int port) {
        TimeOut timeOut = new TimeOut(SERVER_WAIT_TIMEOUT_SECONDS);
        timeOut.start();
        log.info("Waiting for server unavailability: %{}:{}...", serverIP, port);

        while (true) {
            Waiter.waitSeconds(1);
            if (!ServerManager.isAddressReachable(serverIP, port, 15000)) {
                log.info("Server {}:{} is unavailable.", serverIP, port);
                break;
            }
        }
    }

    public static void terminateAwsRmiServer() {
        if (AWS_RMI_SERVER_INSTANCE_ID != null) {
            AwsManager.terminateEC2(AwsManager.getEC2Client(), AWS_RMI_SERVER_INSTANCE_ID);
        }
    }

    public static synchronized String createLocalRunServerAndRunTests() {
        try {
            Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
            String startDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
            String accessKey = AwsManager.getAwsAccessKey();
            String secretKey = AwsManager.getAwsSecretKey();
            String userData = String.format(AWS_LOCAL_SERVER_USER_DATA_TEMPLATE,
                    accessKey,
                    secretKey,
                    startDate,
                    config.getTestngFile(),
                    config.getThreadCount(),
                    config.getBrowserName(),
                    config.getBrowserVersion());
            String encodedUserData = Base64.getEncoder().encodeToString(userData.getBytes());
            AmazonEC2 ec2 = AwsManager.getEC2Client();
            String instanceId = AwsManager.runEC2AndEWaitForId(ec2, config.getThreadCount(),
                    AWS_LOCAL_RUN_IMAGE_ID, SECURITY_KEY_PAIR_NAME, SECURITY_GROUP_NAME, encodedUserData);
            String publicIp = AwsManager.waitForEC2Ip(ec2, instanceId);

            ServerManager.waitForServerAvailability(publicIp, REMOTE_WEB_DRIVER_PORT);
            ServerManager.waitForServerUnavailability(publicIp, REMOTE_WEB_DRIVER_PORT);
            log.info("AWS EC2 local test run completed on IP: {}", publicIp);

            AwsManager.terminateEC2(ec2, instanceId);
            log.info("AWS EC2 local test run server terminated: {}", publicIp);

            String testLogFileName = String.format(TEST_REPORT_LOG_FILE_NAME_TEMPLATE, startDate);
            String logFilePath = AwsManager.downloadFileFromS3(testLogFileName, ".",
                    TEST_REPORTS_AWS_BUCKET_NAME, accessKey, secretKey);
            String testOutput = FileManager.readFile(logFilePath);
            FileManager.deleteFile(logFilePath);
            return testOutput;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
