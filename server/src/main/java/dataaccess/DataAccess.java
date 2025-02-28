package dataaccess;

import server.InvalidRequest;

import java.util.Collection;

abstract class DataAccess {

    int idCount = 1;

    abstract void createUser(UserData user) throws DataAccessException;

    abstract void createAuth(AuthData authData);

    abstract UserData authenticate(String authToken);

    abstract UserData getUser(String username);

    abstract void removeAuthToken(String authToken);

    abstract int createNewGame(String gameName);

    protected int getNextID() {
        return idCount++;
    }

    abstract Collection<GameData> listGames();

    abstract void joinGame(UserData user, int gameID, String playerColor) throws InvalidRequest, ColorTakenException;
}
