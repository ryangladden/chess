package dataaccess;

import chess.ChessGame;
import server.InvalidRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class MemoryDataAccess extends DataAccess {

    private HashMap<String, UserData> authTokens = new HashMap<>();
    private HashMap<String, GameData> games = new HashMap<>();
    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if (users.getOrDefault(user.username(), null) != null) {
            throw new DataAccessException("Error: username taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public UserData authenticate(String authToken) {
        return authTokens.getOrDefault(authToken, null);
    }

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), users.get(authData.username()));
    }

    @Override
    public void removeAuthToken(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public int createNewGame(String gameName) {
        int gameID = getNextID();
        String strGameID = valueOf(gameID);
        GameData game = new GameData(gameID, "", "", gameName, new ChessGame());
        games.put(strGameID, game);
        return gameID;
    }

    @Override
    public Collection<GameData> listGames() {
        System.out.println("Listing games");
        ArrayList<GameData> gameList = new ArrayList<GameData>();
        for ( String key : games.keySet() ) {
            gameList.add(removeBoard(games.get(key)));
        }
        return gameList;
    }

    @Override
    public void joinGame(UserData user, int gameID, String playerColor) throws InvalidRequest, ColorTakenException {
        String gameIdStr = valueOf(gameID);
        GameData game = games.get(gameIdStr);
        switch(playerColor) {
            case "WHITE":
                if (game.whiteUsername().isEmpty()) {
                    games.replace(gameIdStr, new GameData(gameID, user.username(), game.blackUsername(), game.gameName(), game.game()));
                    break;
                }
                else {
                    throw new ColorTakenException("Error: already taken");
                }
            case "BLACK":
                if (game.blackUsername().isEmpty()) {
                    games.replace(gameIdStr, new GameData(gameID, game.whiteUsername(), user.username(), game.gameName(), game.game()));
                    break;
                }
                else {
                    throw new ColorTakenException("Error: already taken");
                }
            case null, default:
                throw new InvalidRequest("Error: bad request");
        }
    }

    private GameData removeBoard(GameData game) {
        return new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null);
    }

    private boolean colorNotTaken(GameData game, String color) throws InvalidRequest {
        return switch (color) {
            case "WHITE" -> game.whiteUsername().isEmpty();
            case "BLACK" -> game.blackUsername().isEmpty();
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
    }
}
