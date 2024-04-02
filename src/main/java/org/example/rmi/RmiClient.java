package org.example.rmi;

import com.amazonaws.services.ec2.AmazonEC2;
import org.example.constants.Settings;
import org.example.data.Config;
import org.example.utils.AwsManager;
import org.example.utils.ServerManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import static org.example.constants.Settings.*;

public class RmiClient {
    private static final String RMI_SERVER_USER_DATA = "#!/bin/bash\n" +
                    "sudo git clone https://github.com/vadymmerezhko/SeleniumAWS.git\n" +
                    "cd SeleniumAWS\n" +
                    "sudo mvn -f rmi-pom.xml install\n" +
                    "sudo java -jar target/SeleniumAWSRmiServer-1.0-SNAPSHOT.jar";
    private static final AtomicReference<RmiServer> RMI_SERVER = new AtomicReference<>();

    private RmiClient() {}

    public static String invokeMethod(String methodInput) {
        try {
            String serverIP = getRmiServerPublicIp();
            if (RMI_SERVER.get() == null) {
                Registry registry = LocateRegistry.getRegistry(serverIP, RMI_REGISTRY_PORT);
                RMI_SERVER.set((RmiServer) registry.lookup(RMI_SERVER_NAME));
            }
            return RMI_SERVER.get().invokeTestServerMethod(methodInput);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized String getRmiServerPublicIp() {
        Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
        String encodedUserData = Base64.getEncoder().encodeToString(RMI_SERVER_USER_DATA.getBytes());
        AmazonEC2 ec2 =  AwsManager.getEC2Client();
        String instanceId = AwsManager.runEC2AndEWaitForId(ec2, config.getThreadCount(),
                AWS_DOCKER_IMAGE_ID, SECURITY_KEY_PAIR_NAME, SECURITY_GROUP_NAME, encodedUserData);
        String instanceIp = AwsManager.waitForEC2Ip(ec2, instanceId);
        System.out.println("AWS EC2 RMI server ip: " + instanceIp);
        return instanceIp;
    }
}
