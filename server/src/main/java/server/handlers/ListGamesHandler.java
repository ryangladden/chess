package server.handlers;

import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import server.request.ListGameRequest;
import server.response.ListGameResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class ListGamesHandler implements Handler {

    private GameService service;

    public ListGamesHandler(GameService service) {
        this.service = service;
    }

    public String listGames(Request req, Response res) {
        try {
            ListGameRequest listRequest = (ListGameRequest) parseRequest(req, ListGameRequest.class);
            ListGameResponse listResponse = this.service.listGames(listRequest);
            res.status(200);
            return new Gson().toJson(listResponse);
        } catch (UnauthorizedException e) {
            res.status(401);
            return errorToJson(e.getMessage());
        } catch (Exception e) {
            res.status(400);
            return errorToJson("Error: bad request in listing games");
        }
    }
}
