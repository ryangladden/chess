package dataaccess;

import chess.ChessGame;

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
        String strGameID = String.valueOf(gameID);
        GameData game = new GameData(gameID, "", "", gameName, new ChessGame());
        games.put(strGameID, game);
        return gameID;
    }

    public Collection<GameData> listGames() {
        System.out.println("Listing games");
        ArrayList<GameData> gameList = new ArrayList<GameData>();
        for ( String key : games.keySet() ) {
            gameList.add(removeBoard(games.get(key)));
        }
        return gameList;
    }

    private GameData removeBoard(GameData game) {
        return new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null);
    }
}
