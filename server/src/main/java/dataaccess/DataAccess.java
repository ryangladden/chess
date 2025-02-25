package dataaccess;

public interface DataAccess {

    String createAuth(String username);

    public void createUser(UserData user);
    public void createAuth(AuthData authData);

    public void verifyAuth(String authToken);
}
