package org.example.rmi;

import org.example.server.TestServerRequestHandler;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.example.constants.Settings.RMI_REGISTRY_PORT;
import static org.example.constants.Settings.RMI_SERVER_NAME;

public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    protected RmiServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        RmiServer server;
        try {
            System.setProperty("java.rmi.server.hostname", "54.176.190.109");
            server = new RmiServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            registry.rebind(RMI_SERVER_NAME, server);
            System.out.println("RMI Test Server has been registered.");
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
}
