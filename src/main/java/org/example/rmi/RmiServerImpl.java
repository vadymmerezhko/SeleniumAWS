package org.example.rmi;

import org.example.data.Config;
import org.example.server.TestServerRequestHandler;
import org.example.utils.CommandLineExecutor;
import org.example.utils.ServerManager;
import org.example.utils.Waiter;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.example.constants.Settings.*;

/**
 * RMI server implementation.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {
    private static final String GET_EC2_PUBLIC_IP_COMMAND_LINE =
            "sudo curl http://169.254.169.254/latest/meta-data/public-ipv4";
    private static final int THREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();

    /**
     * RMI server implementation constructor.
     * @throws RemoteException in case of error.
     */
    protected RmiServerImpl() throws RemoteException {
        super();
    }

    /**
     * RMI server implementation entry point.
     * @param args The array of arguments.
     */
    public static void main(String[] args) {

        for (int i = 1; i <= THREAD_COUNT; i++) {
            registerRmiServer(i);
        }

        while (true) {
            Waiter.waitSeconds(5);
        }
    }

    /**
     * Invokes remote test method with input parameter in JSON format and returns JSON string value.
     * @param methodInput The input parameter in JSON format.
     * @return The output string in JSON format.
     */
    @Override
    public String invokeTestServerMethod(String methodInput) throws RemoteException {
            long threadId = Thread.currentThread().threadId();
            System.out.printf("RMI server process id: %d%n", ProcessHandle.current().pid());
            System.out.printf("RMI server thread id: %d%n", threadId);
            TestServerRequestHandler requestHandler = new TestServerRequestHandler();
            return requestHandler.handleRequest(methodInput, null);
    }

    private static void registerRmiServer(int index) {
        try {
            String publicIp = getCurrentEc2PublicIp();
            System.out.printf("RMI server port was detected: %s%n", publicIp);

            String rmiServerName = ServerManager.getRmiServerName(index);
            int rmiRegistryPort = ServerManager.getRmiServerPort(index);
            System.setProperty("java.rmi.server.hostname", publicIp);
            RmiServer server = new RmiServerImpl();
            Registry registry = LocateRegistry.createRegistry(rmiRegistryPort);
            registry.rebind(rmiServerName, server);

            System.out.printf("RMI Test Server %s has been registered: %s:%s%n",
                    rmiServerName, publicIp, rmiRegistryPort);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCurrentEc2PublicIp() {
        String output = CommandLineExecutor.runCommandLine(GET_EC2_PUBLIC_IP_COMMAND_LINE);
        return output.substring(0, output.indexOf("\n"));
    }
}
