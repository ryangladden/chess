package dataaccess;

abstract class DataAccess {

    int idCount = 1;

    abstract void createUser(UserData user) throws DataAccessException;
    abstract void createAuth(AuthData authData);
    abstract UserData authenticate(String authToken);
    abstract UserData getUser(String username);
    abstract void removeAuthToken(String authToken);
    abstract int createNewGame(String gamename);
    protected int getNextID() {
        return idCount++;
    }
}
