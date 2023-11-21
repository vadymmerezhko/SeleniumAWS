package org.example;

import com.amazonaws.services.ec2.AmazonEC2;
import org.example.balancer.LoadBalancer;

public class ServerManager {
    private final static LoadBalancer loadBalancer = LoadBalancer.getInstance();

    public static void createServerInstances(long serverCount,
                                             String awsImageId,
                                             String securityKeyPairName,
                                             String securityGroupName,
                                             String userData) {
        loadBalancer.setMaxServersCount(serverCount);
        AmazonEC2 ec2 = AwsManager.getEC2Client();

        for (long i = 0; i < serverCount; i++) {
            String instanceId = AwsManager.runEC2AndEWaitForId(
                    ec2, awsImageId, securityKeyPairName, securityGroupName, userData);
            //System.out.println(i + " : " + instanceId);
            loadBalancer.setServerEC2Id(i, instanceId);
            String instanceIp = AwsManager.waitForEC2Ip(ec2, instanceId);
            //System.out.println(i + " : " + instanceIp);
            loadBalancer.setServerEC2PublicIp(i, instanceIp);
        }
        //System.out.println("All " + serverCount + " Selenium servers are created");
    }

    public static void terminateAllSeleniumServers() {
        AmazonEC2 ec2Client = AwsManager.getEC2Client();

        loadBalancer.getAllServersEC2Ids().forEach(ec2Id -> {
            AwsManager.terminateEC2(ec2Client, ec2Id);
        });
        //System.out.println("All Selenium servers are terminated");
    }
}
