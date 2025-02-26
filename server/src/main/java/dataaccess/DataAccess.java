package dataaccess;

public interface DataAccess {

    public void createUser(UserData user) throws DataAccessException;
    public void createAuth(AuthData authData);
    public UserData authenticate(String authToken);
    public UserData getUser(String username);
    public void removeAuthToken(String authToken);
}
