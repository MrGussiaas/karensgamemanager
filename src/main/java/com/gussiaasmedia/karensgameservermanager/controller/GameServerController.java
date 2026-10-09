package com.gussiaasmedia.karensgameservermanager.controller;

import com.gussiaasmedia.karensgameservermanager.model.GameServerResponse;
import com.gussiaasmedia.karensgameservermanager.service.GameServerProcessManager;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/server")
public class GameServerController {

    private final GameServerProcessManager processManager;

    public GameServerController(GameServerProcessManager processManager) {
        this.processManager = processManager;
    }

    @PostMapping("/start/{port}")
    public GameServerResponse startServer(@PathVariable int port) throws IOException {

        return processManager.startServer(port);
    }

    @GetMapping("/reservePort")
    public Integer getNextPort(){
        return processManager.reservePort();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from 2 Karens Server Manager!";
    }
}