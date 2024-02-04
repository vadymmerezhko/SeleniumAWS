package org.example.balancer;

import org.example.constants.Settings;
import org.example.utils.ServerManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class LoadBalancer {

    private static final String USER_DATA_TEMPLATE =
            "#!/bin/bash\n" +
            "sudo docker run -e SE_NODE_MAX_SESSIONS=%d -d -p 4444:4444 -p 7900:7900 " +
            "--shm-size=\"2g\" selenium/standalone-%s:%s";
    private final AtomicLong maxServersCount = new AtomicLong(0);
    private final ConcurrentMap<Long, Long> serverThreadsCountMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Long> threadIdServerIdMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> serverIdPublicIpMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> serverEC2IdMap = new ConcurrentHashMap<>();
    private final Set<Long> serverLockMap = Collections.synchronizedSet(new HashSet<>());
    private static LoadBalancer loadBalancer;

    public synchronized static LoadBalancer getInstance() {
        if (loadBalancer == null) {
            loadBalancer = new LoadBalancer();
        }
        return loadBalancer;
    }

    private LoadBalancer() {
    }

    public synchronized void lockSever(long serverId) {
        serverLockMap.add(serverId);

        if (serverLockMap.size() == maxServersCount.get()) {
            throw new RuntimeException("All Selenium Servers are locked!");
        }
    }

    public void unlockSever(long serverId) {
        serverLockMap.remove(serverId);
    }

    public Collection<String> getAllServersEC2Ids() {
        return serverEC2IdMap.values();
    }

    public synchronized void setMaxServersCount(long count) {
        maxServersCount.set(count);

        for (long i = 0; i < count; i++) {
            serverThreadsCountMap.put(i, 0L);
        }
    }

    public void setServerEC2PublicIp(long serverId, String publicIp) {
        serverIdPublicIpMap.put(serverId, publicIp);
    }

    public void setServerEC2Id(long serverEC2Id, String ec2Id) {
        serverEC2IdMap.put(serverEC2Id, ec2Id);
    }

    public synchronized String getServerPublicIp(long serverId, int threadCount, String browserName, String browserVersion) {
        String userData = String.format(USER_DATA_TEMPLATE, threadCount, browserName, browserVersion);
        String encodedUserData = Base64.getEncoder().encodeToString(userData.getBytes());

        if (serverIdPublicIpMap.isEmpty()) {
            try {
                ServerManager.createServerInstances(
                        Settings.SELENIUM_SERVERS_COUNT,
                        threadCount,
                        Settings.AWS_DOCKER_IMAGE_ID,
                        Settings.SECURITY_KEY_PAIR_NAME,
                        Settings.SECURITY_GROUP_NAME,

                        encodedUserData);
            } catch (Exception e) {
                System.out.println("Cannot create all servers:\n" + e.getMessage());
                System.exit(-1);
            }
        }
        return serverIdPublicIpMap.get(serverId);
    }

    public synchronized long getThreadServerId() {
        long threadId = Thread.currentThread().threadId();
        return threadIdServerIdMap.get(threadId);
    }

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
