package service;

import dataaccess.ColorTakenException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.InvalidGameID;
import model.GameData;
import model.UserData;
import server.InvalidRequest;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGameRequest;
import server.response.CreateGameResponse;
import server.response.JoinGameResponse;
import server.response.ListGameResponse;

import java.util.Collection;

public class GameService extends Service {

    public GameService(DataAccess memoryData) {
        super(memoryData);
    }

    public CreateGameResponse createNewGame(CreateGameRequest req) throws DataAccessException {
        authenticate(req.authToken());
        if (req.gameName() == null) {
            throw new InvalidRequest("Error: bad request");
        }
        int gameID = memoryData.createNewGame(req.gameName());
        return new CreateGameResponse(200, gameID);
    }

    public ListGameResponse listGames(ListGameRequest req) throws DataAccessException {
        authenticate(req.authToken());
        Collection<GameData> games = memoryData.listGames();
        return new ListGameResponse(games);
    }

    public JoinGameResponse joinGame(JoinGameRequest req) throws DataAccessException, ColorTakenException, InvalidGameID {
        UserData user = authenticate(req.authToken());
        memoryData.joinGame(user, req.gameID(), req.playerColor());
        return new JoinGameResponse(200);
    }

    public void clear() {
        memoryData.clear();
    }
}
