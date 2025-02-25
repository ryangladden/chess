package dataaccess;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> authTokens = new HashMap<>();
    private HashMap<String, GameData> games = new HashMap<>();
    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if (users.getOrDefault(user.username(), null) != null) {
            throw new DataAccessException("Error: username taken");
        }
        users.put(user.username(), user);
        System.out.println(this.users);
    }

    @Override
    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), users.get(authData.username()));
        System.out.println(this.authTokens);
    }

    @Override
    public void verifyAuth(String authToken) {

    }
}
