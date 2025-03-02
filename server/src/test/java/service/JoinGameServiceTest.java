package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.CreateGameRequest;
import server.request.JoinGameRequest;
import server.request.ListGameRequest;
import server.request.RegisterRequest;
import server.response.ListGameResponse;

public class JoinGameServiceTest {

    private String authToken;
    private String gameName;
    private MemoryDataAccess memoryData;
    private GameService gameService;
    private UserService userService;


    @BeforeEach
    public void setup() throws DataAccessException {
        memoryData = new MemoryDataAccess();
        gameService = new GameService(memoryData);
        userService = new UserService(memoryData);
        RegisterRequest regReq = new RegisterRequest("joemama", "pAsSwOrD", "joemama@gmail.com");
        authToken = userService.register(regReq).authData().authToken();
        gameName = "super kewl new chess game";
        CreateGameRequest gameReq = new CreateGameRequest(authToken, gameName);
        gameService.createNewGame(gameReq);
    }

    @Test
    public void joinGame() throws ColorTakenException, UnauthorizedException, InvalidGameID {
        JoinGameRequest joinReq = new JoinGameRequest("BLACK", 1, authToken);
        gameService.joinGame(joinReq);
        ListGameRequest listReq = new ListGameRequest(authToken);
        ListGameResponse listRes = gameService.listGames(listReq);
        GameData game = (GameData) listRes.games().toArray()[0];
        Assertions.assertEquals("joemama", game.blackUsername());
    }

    @Test
    public void joinTeamTaken() throws ColorTakenException, UnauthorizedException, InvalidGameID {
        JoinGameRequest joinReq = new JoinGameRequest("BLACK", 1, authToken);
        gameService.joinGame(joinReq);
        ColorTakenException exception = Assertions.assertThrows(ColorTakenException.class, () -> gameService.joinGame(joinReq));
        Assertions.assertEquals("Error: already taken", exception.getMessage());
    }
}
