package service;

import dataaccess.GameData;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import server.request.CreateGameRequest;
import server.request.ListGameRequest;
import server.response.CreateGameResponse;
import server.response.ListGameResponse;

import java.util.ArrayList;
import java.util.Collection;

public class GameService extends Service {

    public GameService(MemoryDataAccess memoryData) {
        super(memoryData);
    }

    public CreateGameResponse createNewGame(CreateGameRequest req) throws UnauthorizedException {
        authenticate(req.authToken());
        int gameID = memoryData.createNewGame(req.gameName());
        return new CreateGameResponse(200, gameID);
    }

    public ListGameResponse listGames(ListGameRequest req) throws UnauthorizedException {
        authenticate(req.authToken());
        Collection<GameData> games = memoryData.listGames();
        return new ListGameResponse(games);
    }
}
