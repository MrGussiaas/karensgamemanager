package com.gussiaasmedia.karensgameservermanager.model;

public class ServerProcessModel {
    private String serverGuid;

    private int serverPort;

    private Process process;

    public ServerProcessModel(Process process){
        this.process = process;
    }

    public String getServerGuid() {
        return serverGuid;
    }

    public void setServerGuid(String serverGuid) {
        this.serverGuid = serverGuid;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
