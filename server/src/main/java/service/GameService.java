package service;

import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import server.request.CreateGameRequest;
import server.response.CreateGameResponse;

public class GameService extends Service {

    public GameService(MemoryDataAccess memoryData) {
        super(memoryData);
    }

    public CreateGameResponse createNewGame(CreateGameRequest req) throws UnauthorizedException {
        authenticate(req.authToken());
        int gameID = memoryData.createNewGame(req.gameName());
        return new CreateGameResponse(200, gameID);
    }

}
