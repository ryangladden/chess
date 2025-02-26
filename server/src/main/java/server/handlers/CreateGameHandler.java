package server.handlers;

import service.GameService;

public class CreateGameHandler implements Handler{

    private GameService service;

    public CreateGameHandler(GameService service) {
        this.service = service;
    }
}
