package org.example.balancer;

import org.example.constants.Settings;
import org.example.utils.ServerManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.example.constants.Settings.AWS_EC2_USER_DATA_TEMPLATE;

/**
 * This call performs load balancing of running test on AWS EC2 instances.
 */
public class LoadBalancer {

    private final AtomicLong maxServersCount = new AtomicLong(0);
    private final ConcurrentMap<Long, Long> serverThreadsCountMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Long> threadIdServerIdMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> serverIdPublicIpMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> serverEC2IdMap = new ConcurrentHashMap<>();
    private final Set<Long> serverLockMap = Collections.synchronizedSet(new HashSet<>());
    private static LoadBalancer loadBalancer;

    /**
     * Returns Load Balancer instance.
     * @return The Load Balancer instance.
     */
    public synchronized static LoadBalancer getInstance() {
        if (loadBalancer == null) {
            loadBalancer = new LoadBalancer();
        }
        return loadBalancer;
    }

    private LoadBalancer() {
    }

    /**
     * Locks the AWS EC2 server by its ID when test hangs up on it.
     * @param serverId The AWS EC2 ID.
     */
    public synchronized void lockSever(long serverId) {
        serverLockMap.add(serverId);

        if (serverLockMap.size() == maxServersCount.get()) {
            throw new RuntimeException("All Selenium Servers are locked!");
        }
    }

    /**
     * Unlocks the AWS EC2 server by its ID when hanged up test is resolved.
     * @param serverId The AWS EC2 ID.
     */
    public void unlockSever(long serverId) {
        serverLockMap.remove(serverId);
    }

    /**
     * Returns all AWS EC2 servers IDs.
     * @return The list of IDs.
     */
    public Collection<String> getAllServersEC2Ids() {
        return serverEC2IdMap.values();
    }

    /**
     * Sets the maximal servers number.
     * @param count The maximal count of the servers.
     */
    public synchronized void setMaxServersCount(long count) {
        maxServersCount.set(count);

        for (long i = 0; i < count; i++) {
            serverThreadsCountMap.put(i, 0L);
        }
    }

    /**
     * Sets AWS EC2 server IP by its internal ID.
     * @param serverId The internal server ID.
     * @param ec2Ip The EC2 instance IP.
     */
    public void setServerEC2PublicIp(long serverId, String ec2Ip) {
        serverIdPublicIpMap.put(serverId, ec2Ip);
    }

    /**
     * Sets AWS EC2 server ID by its EC2 ID.
     * @param serverId The internal server ID.
     * @param ec2Id The EC2 instance ID.
     */
    public void setServerEC2Id(long serverId, String ec2Id) {
        serverEC2IdMap.put(serverId, ec2Id);
    }

    /**
     * Returns AWS EC2 instance public IP.
     * @param serverId The internal server ID.
     * @param threadCount The maximal thread count.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @return The AWS EC2 public IP.
     */
    public synchronized String getServerPublicIp(long serverId, int threadCount, String browserName, String browserVersion) {
        String userData = String.format(AWS_EC2_USER_DATA_TEMPLATE, threadCount, browserName, browserVersion);
        String encodedUserData = Base64.getEncoder().encodeToString(userData.getBytes());

        if (serverIdPublicIpMap.isEmpty()) {
            try {
                ServerManager.createSeleniumServerInstances(
                        Settings.SELENIUM_SERVERS_COUNT,
                        threadCount,
                        Settings.AWS_DOCKER_IMAGE_ID,
                        Settings.SECURITY_KEY_PAIR_NAME,
                        Settings.SECURITY_GROUP_NAME,
                        encodedUserData);
            } catch (Exception e) {
                System.out.printf("Cannot create all servers:%n%s%n", e.getMessage());
                System.exit(-1);
            }
        }
        return serverIdPublicIpMap.get(serverId);
    }

    /**
     * Returns server ID for the current thread.
     * @return The server ID.
     */
    public synchronized long getThreadServerId() {
        long threadId = Thread.currentThread().threadId();
        return threadIdServerIdMap.get(threadId);
    }

    /**
     * Increments the server thread count.
     */
    public synchronized void incrementServerThreadCount() {
        long threadId = Thread.currentThread().threadId();
        long serverId = getMinCountPoolServerId();

        threadIdServerIdMap.put(threadId, serverId);

        if (serverThreadsCountMap.containsKey(serverId)) {
            long count = serverThreadsCountMap.get(serverId);
            serverThreadsCountMap.put(serverId, count + 1);
        }
        else {
            serverThreadsCountMap.put(serverId, 1L);
        }
    }

    /**
     * Decrements the server thread count.
     */
    public synchronized void decrementServerThreadCount() {
        long threadId = Thread.currentThread().threadId();
        long serverId = threadIdServerIdMap.get(threadId);

        if (serverThreadsCountMap.containsKey(serverId)) {
            long count = serverThreadsCountMap.get(serverId);

            if (count > 0) {
                serverThreadsCountMap.put(serverId, count - 1);
            }
        }
        else {
            serverThreadsCountMap.put(serverId, 0L);
        }
        threadIdServerIdMap.remove(threadId);
    }

    /**
     * Returns server ID that has minimal number of test threads.
     * @return The server ID.
     */
    private synchronized long getMinCountPoolServerId() {
        long minCountServerId = 0;
        long minCount = Integer.MAX_VALUE;
        Set<Long> serverIds = serverThreadsCountMap.keySet();

        for (long serverId : serverIds) {
            if (serverLockMap.contains(serverId)) {
                continue;
            }
            long count = serverThreadsCountMap.get(serverId);

            if (serverThreadsCountMap.get(serverId) < minCount) {
               minCount = count;
               minCountServerId = serverId;
            }
        }
        return minCountServerId;
    }
}
