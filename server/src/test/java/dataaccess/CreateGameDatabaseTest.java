package dataaccess;

import org.junit.jupiter.api.*;

import java.sql.ResultSet;

public class CreateGameDatabaseTest {

    DatabaseDataAccess dataAccess;
    String gameName;

    @BeforeEach
    public void setUp() throws Exception{
        dataAccess = new DatabaseDataAccess();
        gameName = "wowie cool new chess game here";
    }

    @AfterEach
    public void cleanUp() {
        TestUtilities.removeGame(gameName);
    }

    @Test
    @DisplayName("Create New Game")
    public void createNewGame() throws Exception {
        dataAccess.createNewGame(gameName);
        ResultSet query = TestUtilities.queryDatabase("SELECT * FROM games WHERE name = '" + gameName + "';");

        Assertions.assertTrue(query.next());
    }

    @Test
    public void createGameEmptyName() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> dataAccess.createNewGame(""));
    }
}
