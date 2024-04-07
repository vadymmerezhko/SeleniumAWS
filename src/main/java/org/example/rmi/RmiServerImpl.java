package org.example.rmi;

import org.example.server.TestServerRequestHandler;
import org.example.utils.CommandLineExecutor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.example.constants.Settings.*;

public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    private static final String GET_EC2_PUBLIC_IP_COMMAND_LINE =
            "curl http://169.254.169.254/latest/meta-data/public-ipv4";

    private static final String RMI_SERVER_USER_NAME = "ubuntu";

    protected RmiServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        registerRmiServer();
    }

    @Override
    public String invokeTestServerMethod(String methodInput) throws RemoteException {
            long threadId = Thread.currentThread().threadId();
            System.out.println("RMI server process id: " + ProcessHandle.current().pid());
            System.out.println("RMI server thread id: " + threadId);
            TestServerRequestHandler requestHandler = new TestServerRequestHandler();
            return requestHandler.handleRequest(methodInput, null);
    }

    private static void registerRmiServer() {
        try {
            String publicIp = getCurrentEc2PublicIp();
            System.setProperty("java.rmi.server.hostname", publicIp);
            RmiServer server = new RmiServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            registry.rebind(RMI_SERVER_NAME, server);

            System.out.printf("RMI Test Server has been registered: %s%n", RMI_SERVER_NAME);
            System.out.printf("Public IP: %s%n", publicIp);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCurrentEc2PublicIp() {
        String output = CommandLineExecutor.runCommandLine(GET_EC2_PUBLIC_IP_COMMAND_LINE);
        return output.substring(0, output.indexOf(RMI_SERVER_USER_NAME));
    }
}
