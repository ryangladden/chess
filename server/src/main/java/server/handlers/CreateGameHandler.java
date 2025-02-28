package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.UnauthorizedException;
import server.InvalidRequest;
import server.request.CreateGameRequest;
import server.response.CreateGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler implements Handler{

    private GameService service;

    public CreateGameHandler(GameService service) {
        this.service = service;
    }

    public String createNewGame(Request req, Response res) {
        try {
            CreateGameRequest gameName = (parseRequest(req, CreateGameRequest.class));
            CreateGameRequest gameRequest = new CreateGameRequest(getAuthToken(req), gameName.gameName());
            CreateGameResponse gameResponse = service.createNewGame(gameRequest);
            res.status(gameResponse.status());
            return gameIdToJson(gameResponse);
        } catch (UnauthorizedException e) {
            res.status(401);
            return errorToJson(e.getMessage());
        }
    }

    private String getGameName(Request req) {
        return "";
    }

    private String gameIdToJson(CreateGameResponse res) {
        return "{\"gameID\": " + res.gameID() + "}";
    }

}
