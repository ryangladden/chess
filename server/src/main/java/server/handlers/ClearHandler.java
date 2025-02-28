package server.handlers;

import service.GameService;
import spark.Request;
import spark.Response;

public class ClearHandler implements Handler{

    GameService service;

    public ClearHandler(GameService service) {
        this.service = service;
    }

    public String clear(Request req, Response res) {
        try {
            service.clear();
            res.status(200);
            return "";
        } catch(Exception e) {
            res.status(500);
            return errorToJson(e.getMessage());
        }
    }
}
