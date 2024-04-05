package org.example.rmi;

import org.example.data.Config;
import org.example.utils.ConverterUtils;
import org.example.utils.ServerManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;
import static org.example.utils.ServerManager.createRmiServerPublicIp;

public class RmiClient {
    private static final ConcurrentMap <Long, RmiServer> RMI_SERVER_MAP = new ConcurrentHashMap<>();
    private static final int TREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();

    private RmiClient() {}

    public static String invokeMethod(String methodInput) {
        try {
            String methodOutput = getRmiServer().invokeTestServerMethod(methodInput);
            return ConverterUtils.convertRemoteOutputToJsonString(methodOutput);
        }
        catch (Exception e) {
            System.out.println("RMI Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static RmiServer getRmiServer() {
        long threadId = Thread.currentThread().threadId();

        if (!RMI_SERVER_MAP.containsKey(threadId)) {
            try {
                String serverName = getServerName(threadId);
                String serverIP = createRmiServerPublicIp();
                int port = getServerPort(threadId);
                Registry registry = LocateRegistry.getRegistry(serverIP, port);
                RMI_SERVER_MAP.put(threadId, (RmiServer) registry.lookup(serverName));
                if (!ServerManager.isAddressReachable(
                        serverIP, port, RMI_SERVER_WAIT_TIMEOUT)) {
                    throw new RuntimeException("RMI server ip/port is not reachable.");
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return RMI_SERVER_MAP.get(threadId);
    }

    private static String getServerName(long threadId) {
        return RMI_SERVER_NAME; //RmiServerImpl.getRmiServerName(getServerIndex(threadId));
    }

    static int getServerIndex(long threadId) {
        return  (int) threadId % TREAD_COUNT + 1;
    }

    private static int getServerPort(long threadId) {
        return RMI_REGISTRY_PORT; //RmiServerImpl.getRmiServerPort(getServerIndex(threadId));
    }
}
