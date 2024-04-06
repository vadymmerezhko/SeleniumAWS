package org.example.rmi;

import org.example.data.Config;
import org.example.server.TestServerRequestHandler;
import org.example.utils.Waiter;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.example.constants.Settings.*;

public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    private static final int THREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();
    protected static final AtomicLong SESSION_NUMBER = new AtomicLong(0);
    private static final ConcurrentMap<Long, String> MEtHOD_RESULT_MAP = new ConcurrentHashMap<>();

    protected RmiServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) {

        registerRmiServer();

/*        for (int i = 1; i <= THREAD_COUNT; i++) {
            registerRmiServer();
        }*/
    }

    @Override
    public String invokeTestServerMethod(String methodInput) throws RemoteException {
            long threadId = Thread.currentThread().threadId();
            System.out.println("RMI server thread id: " + threadId);
            TestServerRequestHandler requestHandler = new TestServerRequestHandler();
            return requestHandler.handleRequest(methodInput, null);
    }

    public static String getRmiServerName(int index) {
        return RMI_SERVER_NAME; //String.format("%s-%d", RMI_SERVER_NAME, index);
    }

    public static int getRmiServerPort(int index) {
        return RMI_REGISTRY_PORT; //RMI_REGISTRY_PORT + index;
    }

    private static void registerRmiServer() {
        try {
            String serverName = getRmiServerName(1);
            int port = getRmiServerPort(1);

            System.out.println("RMI Server name: " + serverName);
            System.out.println("RMI port: " + port);

            System.setProperty("java.rmi.server.hostname", "54.151.3.64");
            RmiServer server = new RmiServerImpl();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(serverName, server);
            System.out.printf("RMI Test Server has been registered: %s%n", serverName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
