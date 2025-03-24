package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.InvalidRequest;
import server.request.CreateGameRequest;
import server.request.RegisterRequest;
import server.response.CreateGameResponse;

public class CreateGameServiceTest {

    MemoryDataAccess memoryData;
    GameService service;
    UserService userService;
    CreateGameResponse expected;
    String authToken;
    String gameName;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryData = new MemoryDataAccess();
        service = new GameService(memoryData);
        userService = new UserService(memoryData);
        expected = new CreateGameResponse(200, 1);
        authToken = userService.register(new RegisterRequest("joemama", "password", "joemama@gmail.com")).authData().authToken();
        gameName = "kewlGame";
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        CreateGameRequest newGameReq = new CreateGameRequest(authToken, gameName);
        CreateGameResponse actual = service.createNewGame(newGameReq);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void gameNameNull() {
        CreateGameRequest newGameReq = new CreateGameRequest(authToken, null);

        InvalidRequest exception = Assertions.assertThrows(InvalidRequest.class, () -> service.createNewGame(newGameReq));
        Assertions.assertEquals("Error: bad request", exception.getMessage());
    }
}
