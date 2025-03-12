package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDatabaseTest {

    DatabaseDataAccess dataAccess;
    UserData newUser = new UserData("joemama", "password", "joemama@email.com");
    UserData sameUsername = new UserData("joemama", "pw", "joe@email.com");
    UserData differentUser = new UserData("joemamajr", "pAsSwOrD", "junior.joe.mama@gmail.com");

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = new DatabaseDataAccess();
    }

    @AfterEach
    public void cleanUp() {
        TestUtilities.removeUser(newUser);
        TestUtilities.removeUser(sameUsername);
        TestUtilities.removeUser(differentUser);
    }

    @Test
    public void registerUser() throws Exception {
        dataAccess.createUser(newUser);
        ResultSet query = TestUtilities.queryDatabase("SELECT * FROM users WHERE username = '" + newUser.username() + "';");
        Assertions.assertTrue(query.next(), "no change to database");
        Assertions.assertEquals(newUser.username(), query.getString("username"), "user not created");
        Assertions.assertTrue(BCrypt.checkpw(newUser.password(), query.getString("password")));
    }

    @Test
    public void attemptRegisterUserExists() throws Exception {
        dataAccess.createUser(newUser);

        UserExistsException e = Assertions.assertThrows(UserExistsException.class, () -> dataAccess.createUser(sameUsername));
    }
}
