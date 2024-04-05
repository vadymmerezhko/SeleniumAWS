package org.example.rmi;

import org.example.data.Config;
import org.example.server.TestServerRequestHandler;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.example.constants.Settings.*;

public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    private static final int THREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();

    protected RmiServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) {

        try {
            for (int i = 1; i <= THREAD_COUNT; i++) {
                Thread sessionThread = new Thread() {
                    public void run() {
                        System.out.println("RMI server thread id: " + Thread.currentThread().threadId());
                        registerRmiServer();
                    }
                };
                sessionThread.start();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String invokeTestServerMethod(String methodInput) throws RemoteException {
        TestServerRequestHandler requestHandler = new TestServerRequestHandler();
        return requestHandler.handleRequest(methodInput, null);
    }

    public static String getRmiServerName(int index) {
        return String.format("%s-%d", RMI_SERVER_NAME, index);
    }

    public static int getRmiServerPort(int index) {
        return RMI_REGISTRY_PORT + index;
    }

    private static void registerRmiServer() {
        try {
            for (int i = 1; i <= THREAD_COUNT; i++) {
                String serverName = getRmiServerName(i);
                int port = getRmiServerPort(i);
                System.setProperty("java.rmi.server.hostname", "54.193.117.155");
                RmiServer server = new RmiServerImpl();
                Registry registry = LocateRegistry.createRegistry(port);
                registry.rebind(serverName, server);
                System.out.printf("RMI Test Server has been registered: %s%n", serverName);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
