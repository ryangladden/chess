package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;

public class JoinGameDatabaseTest {

    DatabaseDataAccess dataAccess;
    UserData user;
    int userId;
    String gameName;
    int gameId;
    String team;

    @BeforeEach
    public void setUp() throws Exception{
        dataAccess = new DatabaseDataAccess();
        user = new UserData("brand", "PaSSworD", "email@email.com");
        dataAccess.createUser(user);
        gameName = "wowie a new game";
        team = "BLACK";
        gameId = dataAccess.createNewGame(gameName);
    }

    @AfterEach
    public void cleanUp() throws Exception {
        dataAccess.clear();
    }

    @Test
    @DisplayName("Join new game as Black")
    public void joinGame() throws Exception{
        dataAccess.joinGame(user, gameId, team);

        ResultSet gameQuery = TestUtilities.queryDatabase("SELECT * FROM games WHERE id = " + gameId + ";");
        Assertions.assertTrue(gameQuery.next());

        ResultSet userQuery = TestUtilities.queryDatabase("SELECT * FROM users WHERE username = '" + user.username() + "';");
        userQuery.next();
        userId = userQuery.getInt("id");

        Assertions.assertEquals(gameQuery.getInt("black"), userId);
    }

    @Test
    @DisplayName("Try to Steal Team")
    public void stealBlack() throws Exception {
        dataAccess.joinGame(user, gameId, team);
        Assertions.assertThrows(ColorTakenException.class, () -> dataAccess.joinGame(user, gameId, team));
    }
}
