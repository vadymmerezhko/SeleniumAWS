package org.example.server;

import org.example.data.MethodInput;
import org.example.rmi.RmiClient;

public class BaseTestServerRmi extends BaseTestServer {
    protected String invokeRmiMethod(MethodInput methodInput) {
        String rmiInputJsonString = methodInputToSting(methodInput);
        return RmiClient.invokeMethod(rmiInputJsonString);
    }
}
