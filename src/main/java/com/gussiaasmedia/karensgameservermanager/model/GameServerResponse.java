package com.gussiaasmedia.karensgameservermanager.model;

public class GameServerResponse {

    private String sessionGuid;
    private int port;

    public String getSessionGuid() {
        return sessionGuid;
    }

    public void setSessionGuid(String sessionGuid) {
        this.sessionGuid = sessionGuid;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
