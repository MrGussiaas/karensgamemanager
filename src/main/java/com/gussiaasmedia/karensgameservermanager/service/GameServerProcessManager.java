package com.gussiaasmedia.karensgameservermanager.service;

import com.gussiaasmedia.karensgameservermanager.model.GameServerResponse;
import com.gussiaasmedia.karensgameservermanager.model.PortStack;
import com.gussiaasmedia.karensgameservermanager.model.ServerProcessModel;
import com.gussiaasmedia.karensgameservermanager.model.ServerProcessRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.UUID;

@Service
public class GameServerProcessManager {
    private ServerProcessRegistry serverProcessRegistry = new ServerProcessRegistry();
    private PortStack portStack = new PortStack();
    private static final Logger log = LoggerFactory.getLogger(GameServerProcessManager.class);
    private static final String SERVER_EXE =
            "/home/ubuntu/TwoKarensServer/TwoKarensServer.x86_64";
    private final RestClient restClient;
    public GameServerProcessManager(@Value("${matchmaking.api.url}") String managerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(managerUrl)
                .build();
    }

    public Integer reservePort(){
        return portStack.getNextPort();
    }

    public GameServerResponse startServer(int port) {
        log.info("This is the info line");
        log.debug("this is the debug line");
        ProcessBuilder processBuilder = new ProcessBuilder(
                SERVER_EXE,
                "-batchmode",
                "-nographics",
                "--port",
                String.valueOf(port)
        );
        processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);

        try {
            Process newProcess = processBuilder.start();
            GameServerResponse response = new GameServerResponse();
            ServerProcessModel serverProcessModel = new ServerProcessModel(newProcess);
            UUID uuid = UUID.randomUUID();
            String serverID = uuid.toString();
            serverProcessModel.setServerGuid((serverID));
            serverProcessModel.setServerPort(port);
            serverProcessRegistry.registerProcess(port, serverProcessModel);
            newProcess.onExit().thenAccept(p -> {
                log.info("Unity server on port {} exited with code: {}", port, p.exitValue());
                try {
                    // Unregister the port automatically
                    ServerProcessModel killedServerProcessModel = serverProcessRegistry.getProcessForPort(port);
                    sendDeathSignalToAPI(killedServerProcessModel.getServerGuid());
                    portStack.returnPort(port);
                    serverProcessRegistry.unregisterProcess(port);
                }
                catch(Exception e){
                    log.error("error cleaning up server process", e);
                }

            });

            boolean ready = waitForServer(port, 30);

            if (!ready) {
                log.error(
                        "Unity server failed to become ready on port {}",
                        port
                );

                newProcess.destroy();

                return null;
            }

            response.setPort(serverProcessModel.getServerPort());
            response.setSessionGuid(serverProcessModel.getServerGuid());
            log.info("server: " + serverProcessModel.getServerGuid() + " on port: " + serverProcessModel.getServerPort());
            return response;
        } catch (IOException e) {
            return null;
        }
    }

    private boolean waitForServer(int port, int timeoutSeconds) {

        long timeout = System.currentTimeMillis() + (timeoutSeconds * 1000L);

        while (System.currentTimeMillis() < timeout) {

            try (java.net.Socket socket = new java.net.Socket()) {

                socket.connect(
                        new java.net.InetSocketAddress("127.0.0.1", port),
                        500
                );

                log.info("Server is listening on port {}", port);
                return true;

            } catch (IOException e) {
                // Server isn't ready yet
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        log.error("Timed out waiting for server on port {}", port);
        return false;
    }

    private void sendDeathSignalToAPI(String guid){
        try {
            GameServerResponse managerResponse = restClient.post()
                    .uri("/freeSession/{sessionGuid}", guid)
                    .retrieve()
                    .body(GameServerResponse.class);
            System.out.println("the manager response is: " + managerResponse + " with guid: " + managerResponse.getSessionGuid());
            if (managerResponse != null && managerResponse.getSessionGuid() != null) {
                log.info("successfully sent the free server message to matchmaker!");
            }
        } catch (RestClientException e) {
            log.error("Failed to reach MatchMakingAPI service: " + e);
        }
    }
}