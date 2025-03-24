package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;

public class ListGameDatabaseTest {

    DatabaseDataAccess dataAccess;
    String gameOne = "game 1";
    String gameTwo = "game 2";
    GameData gameOneData;
    GameData gameTwoData;
    UserData userOne = new UserData("usey-wusey", "pasSEWJasdga", "email@email.com");
    UserData userTwo = new UserData("other-usey", "Ljaw4ti", "emaiL@mfial.co");
    Collection<GameData> games;

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess = new DatabaseDataAccess();
        dataAccess.clear();
        dataAccess.createUser(userOne);
        dataAccess.createUser(userTwo);
        gameOneData = new GameData(1, userOne.username(), null, gameOne, null);
        gameTwoData = new GameData(2, userTwo.username(), userOne.username(), gameTwo, null);
        dataAccess.createNewGame(gameOne);
        dataAccess.createNewGame(gameTwo);
        dataAccess.joinGame(userOne, 1, "WHITE");
        dataAccess.joinGame(userOne, 2, "BLACK");
        dataAccess.joinGame(userTwo, 2, "WHITE");
        games = new ArrayList<GameData>();
        games.add(gameOneData);
        games.add(gameTwoData);
    }

    @AfterEach
    public void cleanUp() throws Exception {
        dataAccess.clear();
    }

    @Test
    @DisplayName("List all games")
    public void listAllGames() throws Exception {
        Collection<GameData> listGames = dataAccess.listGames();
        Assertions.assertEquals(games, listGames);
    }
}
