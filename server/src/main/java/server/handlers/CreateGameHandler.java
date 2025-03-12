package server.handlers;

import dataaccess.UnauthorizedException;
import server.InvalidRequest;
import server.request.CreateGameRequest;
import server.response.CreateGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler implements Handler {

    private GameService service;

    public CreateGameHandler(GameService service) {
        this.service = service;
    }

    public String createNewGame(Request req, Response res) {
        try {
            CreateGameRequest gameRequest = (CreateGameRequest) parseRequest(req, CreateGameRequest.class);
            CreateGameResponse gameResponse = service.createNewGame(gameRequest);
            res.status(gameResponse.status());
            return gameIdToJson(gameResponse);
        } catch (UnauthorizedException e) {
            res.status(401);
            return errorToJson(e.getMessage());
        } catch (InvalidRequest e) {
            res.status(400);
            return errorToJson(e.getMessage());
        } catch (Exception e) {
            res.status(400);
            return errorToJson("Error: bad request in creating game");
        }
    }


    private String gameIdToJson(CreateGameResponse res) {
        return "{\"gameID\": " + res.gameID() + "}";
    }
}
