package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.InvalidRequest;

import java.util.Collection;

public abstract class DataAccess {

    int idCount = 1;

    public abstract void createUser(UserData user) throws DataAccessException;

    public abstract void createAuth(AuthData authData);

    public abstract UserData authenticate(String authToken);

    public abstract UserData getUser(String username);

    public abstract void removeAuthToken(String authToken);

    public abstract int createNewGame(String gameName);

    protected int getNextID() {
        return idCount++;
    }

    public abstract Collection<GameData> listGames();

    public abstract void joinGame(UserData user, int gameID, String playerColor) throws InvalidRequest, ColorTakenException, InvalidGameID;

    public abstract void clear();
}
