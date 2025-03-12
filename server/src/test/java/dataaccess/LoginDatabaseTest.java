package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.UUID;

public class LoginDatabaseTest {

    DatabaseDataAccess dataAccess;
    UserData newUser = new UserData("joemama", "password", "joemama@email.com");
    AuthData auth = new AuthData(UUID.randomUUID().toString(), newUser.username());

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = new DatabaseDataAccess();
        dataAccess.createUser(newUser);
    }

    @AfterEach
    public void cleanUp() {
        TestUtilities.removeAuth(auth.authToken());
        TestUtilities.removeUser(newUser);
    }

    @Test
    public void passwordValidation() throws Exception {
        Assertions.assertTrue(dataAccess.isValidPassword(newUser.username(), newUser.password()));
    }

    @Test
    public void createAuth() throws Exception {
        dataAccess.createAuth(auth);
        ResultSet authQuery = TestUtilities.queryDatabase("SELECT * FROM auth WHERE token = '" + auth.authToken() + "';");

        Assertions.assertTrue(authQuery.next());

        int id = authQuery.getInt("user_id");
        ResultSet userQuery = TestUtilities.queryDatabase("SELECT username FROM users WHERE id = " + id);

        Assertions.assertTrue(userQuery.next());

        Assertions.assertEquals(userQuery.getString("username"), newUser.username());
    }

    @Test
    public void incorrectPassword() throws Exception {
        Assertions.assertFalse(dataAccess.isValidPassword(newUser.username(), "newUser.password()"));
    }
}
