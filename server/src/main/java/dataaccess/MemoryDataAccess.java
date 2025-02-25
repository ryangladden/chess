package dataaccess;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> authTokens = new HashMap<>();
    private HashMap<String, GameData> games = new HashMap<>();
    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), users.get(authData.username()));
    }

    @Override
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void verifyAuth(String authToken) {

    }
}
