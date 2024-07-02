package org.example.rmi;

import org.example.utils.ConverterUtils;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.REQUEST_HANDLER_ERROR_MSG;
import static org.example.utils.ServerUtils.*;

/**
 * RMI client class.
 */
public class RmiClient {
    private static final ConcurrentMap <Long, RmiServer> RMI_SERVER_MAP = new ConcurrentHashMap<>();

    private RmiClient() {}

    /**
     * Invokes remote method with input parameter in JSON format and returns JSON string value.
     * @param methodInput The input parameter in JSON format.
     * @return The output string in JSON format.
     */
    public static String invokeMethod(String methodInput) {
        try {
            String methodOutput = getRmiServer().invokeTestServerMethod(methodInput);

            if (methodInput.contains(REQUEST_HANDLER_ERROR_MSG)) {
                return methodOutput;
            }
            return ConverterUtils.convertRemoteOutputToJsonString(methodOutput);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static RmiServer getRmiServer() {
        long threadId = Thread.currentThread().threadId();

        if (!RMI_SERVER_MAP.containsKey(threadId)) {
            try {
                String serverIP = createRmiServer();
                String rmiServerName = getRmiServerName();
                int rmiServerPort = getRmiServerPort();
                Registry registry = LocateRegistry.getRegistry(serverIP, rmiServerPort);
                RMI_SERVER_MAP.put(threadId, (RmiServer) registry.lookup(rmiServerName));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return RMI_SERVER_MAP.get(threadId);
    }
}
