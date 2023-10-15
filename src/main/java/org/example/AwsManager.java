package org.example;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

public class AwsManager {

    public static String runEC2(String imageName, String keyPairName, String groupName) {

        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        RunInstancesRequest runInstancesRequest =
                new RunInstancesRequest();

        runInstancesRequest.withImageId(imageName)
                .withInstanceType(InstanceType.T1Micro)
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(keyPairName)
                .withSecurityGroups(groupName);

        RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);

        String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
        System.out.println("EC2 Id: " + instanceId);

        return instanceId;
    }

    public static String getEC2PublicIp(String instanceId) {
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        List<String> instanceIds = new ArrayList<>();
        instanceIds.add(instanceId);

        DescribeInstancesRequest request =  new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);

        DescribeInstancesResult result = ec2.describeInstances(request);
        List<Reservation> reservations = result.getReservations();
        String publicIp = reservations.get(0).getInstances().get(0).getPublicIpAddress();

        System.out.println("EC2 public IP: " + publicIp);
        return publicIp;
    }
}
