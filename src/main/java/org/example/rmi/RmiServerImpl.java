package org.example.rmi;

import lombok.extern.slf4j.Slf4j;
import org.example.data.Config;
import org.example.servers.TestServerRequestHandler;
import org.example.utils.CommandLineUtils;
import org.example.utils.ServerUtils;
import org.example.utils.WaiterUtils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.example.constants.Settings.*;

/**
 * RMI server implementation.
 */
@Slf4j
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
            WaiterUtils.waitSeconds(5);
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
            log.debug("RMI server process id: {}", ProcessHandle.current().pid());
            log.debug("RMI server thread id: {}", threadId);
            TestServerRequestHandler requestHandler = new TestServerRequestHandler();
            return requestHandler.handleRequest(methodInput, null);
    }

    private static void registerRmiServer(int index) {
        try {
            String publicIp = getCurrentEc2PublicIp();
            log.info("RMI server port was detected: {}", publicIp);

            String rmiServerName = ServerUtils.getRmiServerName(index);
            int rmiRegistryPort = ServerUtils.getRmiServerPort(index);
            System.setProperty("java.rmi.server.hostname", publicIp);
            RmiServer server = new RmiServerImpl();
            Registry registry = LocateRegistry.createRegistry(rmiRegistryPort);
            registry.rebind(rmiServerName, server);

            log.info("RMI Test Server {} has been registered: {}:{}",
                    rmiServerName, publicIp, rmiRegistryPort);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCurrentEc2PublicIp() {
        String output = CommandLineUtils.runCommandLine(GET_EC2_PUBLIC_IP_COMMAND_LINE);
        return output.substring(0, output.indexOf("\n"));
    }
}
