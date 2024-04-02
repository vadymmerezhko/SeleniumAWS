package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiServer extends Remote {
    String invokeTestServerMethod(String methodInput) throws RemoteException;
}
