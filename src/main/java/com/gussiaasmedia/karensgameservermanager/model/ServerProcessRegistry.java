package com.gussiaasmedia.karensgameservermanager.model;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


public class ServerProcessRegistry {
    private Map<Integer, ServerProcessModel> processRegistry = new HashMap<Integer, ServerProcessModel>();

    public ServerProcessModel getProcessForPort(Integer portNumber){
        return processRegistry.get(portNumber);
    }

    public void registerProcess(Integer portNumber, ServerProcessModel serverProcessModel){
        processRegistry.put(portNumber, serverProcessModel);
    }

    public void unregisterProcess(Integer portNumber){
        processRegistry.remove(portNumber);
    }
}
