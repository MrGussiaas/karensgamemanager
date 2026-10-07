package com.gussiaasmedia.karensgameservermanager.model;

import java.util.ArrayList;
import java.util.List;

public class PortStack
{
    private static final Integer START_PORT = 27777;
    private static final Integer PORT_RANGE = 10;
    private List<Integer> availablePorts = new ArrayList<Integer>();
    public PortStack(){
        for(int i = START_PORT; i < (START_PORT + PORT_RANGE); i++){
            availablePorts.add(i);
        }
    }
    public Integer getNextPort(){
        if(availablePorts.size() <= 0){
            return -1;
        }
        int nextPort = availablePorts.get(0);
        availablePorts.remove(0);
        return nextPort;
    }

    public void returnPort(Integer port){
        availablePorts.add(port);
    }
}
