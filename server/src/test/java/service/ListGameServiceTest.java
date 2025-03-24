package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.CreateGameRequest;
import server.request.ListGameRequest;
import server.request.RegisterRequest;
import server.response.CreateGameResponse;
import server.response.ListGameResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ListGameServiceTest {

    ListGameResponse expected;
    MemoryDataAccess memoryData;
    GameService gameService;
    UserService userService;
    String authToken;
    ArrayList<Integer> gameIDs;
    String gameName;
    ArrayList<GameData> gameData;

    @BeforeEach
    public void setUp() throws DataAccessException {

        memoryData = new MemoryDataAccess();
        gameService = new GameService(memoryData);
        userService = new UserService(memoryData);
        authToken = userService.register(new RegisterRequest("joemama", "password", "joemama@gmail.com")).authData().authToken();
        gameName = "kewlGame";
        gameIDs = new ArrayList<Integer>();
        gameIDs.add(gameService.createNewGame(new CreateGameRequest(authToken, gameName)).gameID());
        gameData = new ArrayList<GameData>();
        gameData.add(new GameData(1, null, null, gameName, null));
        expected = new ListGameResponse(gameData);
    }

    @Test
    public void listGame() throws DataAccessException {
        ListGameRequest listReq = new ListGameRequest(authToken);

        ListGameResponse actual = gameService.listGames(listReq);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void createAndListGames() throws DataAccessException {
        String newGameName = "other kewl game";
        CreateGameRequest createReq = new CreateGameRequest(authToken, newGameName);
        Collection<GameData> expectedList = new ArrayList<GameData>();
        expectedList.add(new GameData(1, null, null, gameName, null));
        expectedList.add(new GameData(2, null, null, newGameName, null));
        ListGameResponse expected = new ListGameResponse(expectedList);

        CreateGameResponse createRes = gameService.createNewGame(createReq);
        int newGameID = createRes.gameID();

        ListGameRequest listReq = new ListGameRequest(authToken);
        ListGameResponse listRes = gameService.listGames(listReq);
        GameData actual = (GameData) listRes.games().toArray()[1];

        Assertions.assertEquals(expected, listRes);
        Assertions.assertTrue(listRes.games().containsAll(expectedList));
        Assertions.assertEquals(newGameID, actual.gameID());
        Assertions.assertEquals(newGameName, actual.gameName());
    }

    @Test
    public void unauthorizedThrowsError() {
        String randomAuth = UUID.randomUUID().toString();
        ListGameRequest listReq = new ListGameRequest(randomAuth);
        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames(listReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
