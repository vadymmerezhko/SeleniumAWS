package org.example.rmi;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {
    private static final String GET_EC2_PUBLIC_IP_COMMAND_LINE =
            "sudo curl http://169.254.169.254/latest/meta-data/public-ipv4";
    private static final int THREAD_COUNT = new Config(CONFIG_PROPERTIES_FILE_NAME).getThreadCount();

    protected RmiServerImpl() throws RemoteException {
        super();
    }

    public static void main(String[] args) {

        for (int i = 1; i <= THREAD_COUNT; i++) {
            registerRmiServer(i);
        }

        while (true) {
            Waiter.waitSeconds(5);
        }
    }

    @Override
    public String invokeTestServerMethod(String methodInput) throws RemoteException {
            long threadId = Thread.currentThread().threadId();
            log.info("RMI server process id: {}", ProcessHandle.current().pid());
            log.info("RMI server thread id: {}", threadId);
            TestServerRequestHandler requestHandler = new TestServerRequestHandler();
            return requestHandler.handleRequest(methodInput, null);
    }

    private static void registerRmiServer(int index) {
        try {
            String publicIp = getCurrentEc2PublicIp();
            log.info("RMI server port was detected: {}", publicIp);

            String rmiServerName = ServerManager.getRmiServerName(index);
            int rmiRegistryPort = ServerManager.getRmiServerPort(index);
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
        String output = CommandLineExecutor.runCommandLine(GET_EC2_PUBLIC_IP_COMMAND_LINE);
        return output.substring(0, output.indexOf("\n"));
    }
}
