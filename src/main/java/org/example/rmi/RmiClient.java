package org.example.rmi;

import org.example.utils.ServerManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicReference;

import static org.example.constants.Settings.*;
import static org.example.utils.ServerManager.createRmiServerPublicIp;

public class RmiClient {
    private static final AtomicReference<RmiServer> RMI_SERVER = new AtomicReference<>();

    private RmiClient() {}

    public static String invokeMethod(String methodInput) {
        try {
            setRmiServer();
            String methodOutput =  RMI_SERVER.get().invokeTestServerMethod(methodInput);
            return methodOutput;
        }
        catch (Exception e) {
            System.out.println("RMI Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static synchronized void setRmiServer() {
        if (RMI_SERVER.get() == null) {
            try {
                String serverIP = createRmiServerPublicIp();
                Registry registry = LocateRegistry.getRegistry(serverIP, RMI_REGISTRY_PORT);
                RMI_SERVER.set((RmiServer) registry.lookup(RMI_SERVER_NAME));
                if (!ServerManager.isAddressReachable(serverIP, RMI_REGISTRY_PORT, RMI_SERVER_WAIT_TIMEOUT)) {
                    throw new RuntimeException("RMI server ip/port is not reachable.");
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
