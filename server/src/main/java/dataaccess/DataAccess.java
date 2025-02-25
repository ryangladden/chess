package dataaccess;

public interface DataAccess {

    public void createUser(UserData user) throws DataAccessException;
    public void createAuth(AuthData authData);
    public void verifyAuth(String authToken);
    public UserData getUser(String username);
}
