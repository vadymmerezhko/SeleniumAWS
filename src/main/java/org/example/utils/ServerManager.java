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

/**
 * Server manager class.
 */
@Slf4j
public class ServerManager {
    private static String AWS_RMI_SERVER_INSTANCE_ID;
    private static String AWS_RMI_SERVER_INSTANCE_IP;
    private static final int THREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();
    private final static LoadBalancer loadBalancer = LoadBalancer.getInstance();

    /**
     * Creates Selenium server instance(s).
     * @param serverCount The server count.
     * @param threadCount The server thread count.
     * @param awsImageId The AWS EC2 image ID.
     * @param securityKeyPairName The AWS security pair name.
     * @param securityGroupName The AWS security group name.
     * @param userData The EC2 user data to run when EC@ instance starts.
     */
    public static void createSeleniumServerInstances(int serverCount,
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

    /**
     * Terminates all Selenium servers.
     */
    public static void terminateAllSeleniumServers() {
        AmazonEC2 ec2Client = AwsManager.getEC2Client();
        loadBalancer.getAllServersEC2Ids().forEach(ec2Id -> AwsManager.terminateEC2(ec2Client, ec2Id));
    }

    /**
     * Checks if server address is reachable.
     * @param address The server address.
     * @param port The server port.
     * @param timeout The timeout.
     * @return true if server is reachable, or false otherwise.
     */
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

    /**
     * Creates RMI server and returns its public IP address.
     * @return The RMI server public IP address.
     */
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

    /**
     * Returns RMI server name by its index.
     * @param index The server index.
     * @return The RMI server name.
     */
    public static String getRmiServerName(int index) {
        return RMI_SERVER_NAME + index;
    }

    /**
     * Returns RMI server name.
     * @return The RMI server name.
     */
    public static String getRmiServerName() {
        int index = getRmiServerIndex();
        return RMI_SERVER_NAME + index;
    }

    /**
     * Returns RMI server port by its index.
     * @param index The server index.
     * @return The RMI server port.
     */
    public static int getRmiServerPort(int index) {
        return RMI_SERVER_BASE_PORT + index;
    }

    /**
     * Returns RMI server port.
     * @return The RMI server port.
     */
    public static int getRmiServerPort() {
        int index = getRmiServerIndex();
        return RMI_SERVER_BASE_PORT + index;
    }

    /**
     * Returns current thread RMI server index.
     * @return The RMI server index.
     */
    private static int getRmiServerIndex() {
        long threadId = Thread.currentThread().threadId();
        return (int)threadId % THREAD_COUNT + 1;
    }

    /**
     * Waits for server availability by server IP address and port number.
     * @param serverIP The server IP address
     * @param port The server port number.
     */
    public static synchronized void waitForServerAvailability(String serverIP, int port) {
        TimeOut timeOut = new TimeOut(
                String.format("Waits for server %s:%d availability", serverIP, port),
                SERVER_WAIT_TIMEOUT_SECONDS);
        timeOut.start();
        log.info("Waiting for server availability: {}:{}", serverIP, port);

        while (true) {
            Waiter.waitSeconds(1);
            timeOut.checkExpired();

            if (ServerManager.isAddressReachable(serverIP, port, 15000)) {
                log.info("Server {}:{} is available.", serverIP, port);
                break;
            }
        }
    }

    /**
     * Waits for server unavailability by server IP address and port number.
     * @param serverIP The server IP address
     * @param port The server port number.
     */
    public static synchronized void waitForServerUnavailability(String serverIP, int port) {
        TimeOut timeOut = new TimeOut(
                String.format("Waits for server %s:%d unavailability", serverIP, port),
                SERVER_WAIT_TIMEOUT_SECONDS);
        timeOut.start();
        log.info("Waiting for server unavailability: {}:{}", serverIP, port);

        while (true) {
            Waiter.waitSeconds(1);
            timeOut.checkExpired();
            if (!ServerManager.isAddressReachable(serverIP, port, 15000)) {
                log.info("Server {}:{} is unavailable.", serverIP, port);
                break;
            }
        }
    }

    /**
     * Terminates RMI server.
     */
    public static void terminateAwsRmiServer() {
        if (AWS_RMI_SERVER_INSTANCE_ID != null) {
            AwsManager.terminateEC2(AwsManager.getEC2Client(), AWS_RMI_SERVER_INSTANCE_ID);
        }
    }

    /**
     * Creates local run server and runs tests.
     * @return The test result output.
     */
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
