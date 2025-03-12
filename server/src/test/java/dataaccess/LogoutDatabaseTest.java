package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.util.UUID;

public class LogoutDatabaseTest {

    DatabaseDataAccess dataAccess;
    UserData newUser = new UserData("joemama", "password", "joemama@email.com");
    AuthData auth = new AuthData(UUID.randomUUID().toString(), newUser.username());

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = new DatabaseDataAccess();
        dataAccess.createUser(newUser);
        dataAccess.createAuth(auth);
    }

    @AfterEach
    public void cleanUp() {
        TestUtilities.removeAuth(auth.authToken());
        TestUtilities.removeUser(newUser);
    }

    @Test
    @DisplayName("Remove auth token")
    public void removeAuthToken() throws Exception{
        dataAccess.removeAuthToken(auth.authToken());

        ResultSet query = TestUtilities.queryDatabase("SELECT * FROM auth WHERE token = '" + auth.authToken() + "';");
        Assertions.assertFalse(query.next());
    }

    @Test
    @DisplayName("Remove non-existent auth token")
    public void removeTokenDoesNotExist() throws Exception {
        Assertions.assertThrows(UnauthorizedException.class, () -> dataAccess.removeAuthToken(UUID.randomUUID().toString()));
    }
}
