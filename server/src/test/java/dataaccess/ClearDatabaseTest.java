package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.UUID;

public class ClearDatabaseTest {

    DatabaseDataAccess dataAccess;
    UserData newUser = new UserData("joemama", "password", "joemama@email.com");
    AuthData auth = new AuthData(UUID.randomUUID().toString(), newUser.username());

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = new DatabaseDataAccess();
        dataAccess.createUser(newUser);
        dataAccess.createAuth(auth);
        dataAccess.createNewGame("new game ere guvna");
    }

    @Test
    public void clear() throws Exception {
        dataAccess.clear();

        ResultSet games = TestUtilities.queryDatabase("SELECT * FROM games");
        ResultSet auth = TestUtilities.queryDatabase("SELECT * FROM auth");
        ResultSet users = TestUtilities.queryDatabase("SELECT * FROM users");

        Assertions.assertFalse(games.next());
        Assertions.assertFalse(auth.next());
        Assertions.assertFalse(users.next());
    }
}
