package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.CreateGameRequest;
import server.request.LoginRequest;
import server.request.LogoutRequest;
import server.request.RegisterRequest;

public class ClearServiceTest {

    private MemoryDataAccess memoryData;
    private UserService userService;
    private GameService gameService;

    private String username1;
    private String username2;
    private String password1;
    private String password2;
    private String email1;
    private String email2;
    private String authToken1;
    private String authToken2;

    private String gameName1;
    private String gameName2;
    private String gameName3;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryData = new MemoryDataAccess();
        userService = new UserService(memoryData);
        gameService = new GameService(memoryData);

        username1 = "joemama";
        password1 = "pAsSwOrD";
        email1 = "joemama@gmail.com";
        RegisterRequest regReq1 = new RegisterRequest(username1, password1, email1);

        username2 = "joemamajr";
        password2 = "1qaz2wsx";
        email2 = "joemamama@gmail.com";
        RegisterRequest regReq2 = new RegisterRequest(username2, password2, email2);

        authToken1 = userService.register(regReq1).authData().authToken();
        authToken2 = userService.register(regReq2).authData().authToken();

        gameName1 = "kewl game bro";
        gameName2 = "checkmate mate";
        gameName3 = "oh shoot it's chess time bröther";

        CreateGameRequest gameReq1 = new CreateGameRequest(authToken1, gameName1);
        CreateGameRequest gameReq2 = new CreateGameRequest(authToken2, gameName2);
        CreateGameRequest gameReq3 = new CreateGameRequest(authToken2, gameName3);

        gameService.createNewGame(gameReq1);
        gameService.createNewGame(gameReq2);
        gameService.createNewGame(gameReq3);
    }

    @Test
    public void clearAndAttemptLogin() {
        gameService.clear();

        LoginRequest loginReq = new LoginRequest(username1, password1);

        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> userService.login(loginReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

    @Test
    public void clearAndAttemptLogout() {
        gameService.clear();

        LogoutRequest logoutReq = new LogoutRequest(authToken2);
        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout(logoutReq));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }
}
