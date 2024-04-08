package org.example.utils;

import com.amazonaws.services.ec2.AmazonEC2;
import org.example.balancer.LoadBalancer;
import org.example.data.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

import static org.example.constants.Settings.*;

public class ServerManager {
    private static String AWS_RMI_SERVER_INSTANCE_ID;
    private static String AWS_RMI_SERVER_INSTANCE_IP;
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

    public static synchronized String createRmiServerPublicIp() {
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
                AWS_RMI_SERVER_INSTANCE_IP = AwsManager.getEC2PublicIp(ec2, AWS_RMI_SERVER_INSTANCE_ID);
                waitForRmiServer(AWS_RMI_SERVER_INSTANCE_IP);
                System.out.println("AWS EC2 RMI server is running on IP: " + AWS_RMI_SERVER_INSTANCE_IP);
            }
            catch (Exception e) {
                terminateAwsRmiServer();
            }
        }
        return AWS_RMI_SERVER_INSTANCE_IP;
    }

    public static void waitForRmiServer(String serverIP) {
        TimeOut timeOut = new TimeOut(RMI_SERVER_WAIT_TIMEOUT_SECONDS);
        timeOut.start();
        System.out.println("Waiting for RMI server...");

        while (true) {
            Waiter.waitSeconds(1);
            if (ServerManager.isAddressReachable(serverIP, RMI_REGISTRY_PORT, 15000)) {
                System.out.println("RMI server is ready.");
                break;
            }
        }
    }

    public static void terminateAwsRmiServer() {
        if (AWS_RMI_SERVER_INSTANCE_ID != null) {
            AwsManager.terminateEC2(AwsManager.getEC2Client(), AWS_RMI_SERVER_INSTANCE_ID);
        }
    }
}
