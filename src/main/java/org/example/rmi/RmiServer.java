package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI server interface.
 */
public interface RmiServer extends Remote {

    /**
     * Invokes remote test method with input parameter in JSON format and returns JSON string value.
     * @param methodInput The input parameter in JSON format.
     * @return The output string in JSON format.
     */
    String invokeTestServerMethod(String methodInput) throws RemoteException;
}
