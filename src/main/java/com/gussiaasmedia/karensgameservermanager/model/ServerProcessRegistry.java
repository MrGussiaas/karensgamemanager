package com.gussiaasmedia.karensgameservermanager.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


public class ServerProcessRegistry {
    private Map<Integer, ServerProcessModel> processRegistry = new HashMap<Integer, ServerProcessModel>();
    private static final Logger log = LoggerFactory.getLogger(ServerProcessRegistry.class);
    public ServerProcessModel getProcessForPort(Integer portNumber){
        return processRegistry.get(portNumber);
    }

    public void registerProcess(Integer portNumber, ServerProcessModel serverProcessModel){
        processRegistry.put(portNumber, serverProcessModel);
        log.debug("registered a new process: " + serverProcessModel.getServerGuid() + " on port: " + portNumber);
        log.debug(processRegistry.toString());
    }

    public void unregisterProcess(Integer portNumber){
        processRegistry.remove(portNumber);
    }
}
