package org.example.utils;

import com.amazonaws.services.ec2.AmazonEC2;
import lombok.extern.slf4j.Slf4j;
import org.example.balancers.LoadBalancer;
import org.example.configs.Config;
import org.example.exceptions.SmartRuntimeException;
import org.example.helpers.TimeOut;

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
public final class ServerUtils {
    private final static LoadBalancer loadBalancer = LoadBalancer.getInstance();

    private ServerUtils() {}

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
        AmazonEC2 ec2 = AwsUtils.getEC2Client();

        for (long i = 0; i < serverCount; i++) {
            String instanceId = AwsUtils.runEC2AndEWaitForId(
                    ec2, threadCount,  awsImageId, securityKeyPairName, securityGroupName, userData);
            loadBalancer.setServerEC2Id(i, instanceId);
            String instanceIp = AwsUtils.waitForEC2Ip(ec2, instanceId);
            loadBalancer.setServerEC2PublicIp(i, instanceIp);
        }
    }

    /**
     * Terminates all Selenium servers.
     */
    public static void terminateAllSeleniumServers() {
        AmazonEC2 ec2Client = AwsUtils.getEC2Client();
        loadBalancer.getAllServersEC2Ids().forEach(ec2Id -> AwsUtils.terminateEC2(ec2Client, ec2Id));
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
            TimerUtils.waitSeconds(1);
            timeOut.checkExpired();

            if (ServerUtils.isAddressReachable(serverIP, port, 15000)) {
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
            TimerUtils.waitSeconds(1);
            timeOut.checkExpired();
            if (!ServerUtils.isAddressReachable(serverIP, port, 15000)) {
                log.info("Server {}:{} is unavailable.", serverIP, port);
                break;
            }
        }
    }

    /**
     * Creates local run server and runs tests.
     * @return The test result output.
     */
    public static synchronized String createLocalRunServerAndRunTests() {
        try {
            Config config = Config.getInstance();
            String startDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
            String accessKey = AwsUtils.getAwsAccessKey();
            String secretKey = AwsUtils.getAwsSecretKey();
            String userData = String.format(AWS_LOCAL_SERVER_USER_DATA_TEMPLATE,
                    accessKey,
                    secretKey,
                    startDate,
                    config.getTestngFile(),
                    config.getThreadCount(),
                    config.getBrowserName(),
                    config.getBrowserVersion());
            String encodedUserData = Base64.getEncoder().encodeToString(userData.getBytes());
            AmazonEC2 ec2 = AwsUtils.getEC2Client();
            String instanceId = AwsUtils.runEC2AndEWaitForId(ec2, config.getThreadCount(),
                    AWS_LOCAL_RUN_IMAGE_ID, SECURITY_KEY_PAIR_NAME, SECURITY_GROUP_NAME, encodedUserData);
            String publicIp = AwsUtils.waitForEC2Ip(ec2, instanceId);

            ServerUtils.waitForServerAvailability(publicIp, REMOTE_WEB_DRIVER_PORT);
            ServerUtils.waitForServerUnavailability(publicIp, REMOTE_WEB_DRIVER_PORT);
            log.info("AWS EC2 local test run completed on IP: {}", publicIp);

            AwsUtils.terminateEC2(ec2, instanceId);
            log.info("AWS EC2 local test run server terminated: {}", publicIp);

            String testLogFileName = String.format(TEST_REPORT_LOG_FILE_NAME_TEMPLATE, startDate);
            String logFilePath = AwsUtils.downloadFileFromS3(testLogFileName, ".",
                    TEST_REPORTS_AWS_BUCKET_NAME, accessKey, secretKey);
            String testOutput = FileSystemUtils.readFile(logFilePath);
            FileSystemUtils.deleteFile(logFilePath);
            return testOutput;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(
                    "Failed to create local run server and to run tests", e);
        }
    }
}
