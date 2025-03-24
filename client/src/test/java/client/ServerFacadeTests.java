package client;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.*;


public class ServerFacadeTests {

    static ServerFacade facade;
    private static Server server;
    private UserData user;
    private AuthData auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    public void cleanUp() {
        facade.clear();
    }

    @BeforeEach
    public void setUp() {
        user = new UserData("josephmama", "password", "joseph@gmail.com");
        auth = facade.register(user);
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Register new user")
    public void register() {
        UserData user = new UserData("bigdawgjoe", "password", "email@email.com");
        AuthData authentication = facade.register(user);

        Assertions.assertEquals("bigdawgjoe", authentication.username());
    }

    @Test
    @DisplayName("Register existing user")
    public void registerExisting() {
        UserData user = new UserData("josephmama", "password", "joseph@gmail.com");
        AlreadyTaken ex = Assertions.assertThrows(AlreadyTaken.class, () -> facade.register(user));
        Assertions.assertEquals("Unique resource already taken", ex.getMessage());
    }

    @Test
    @DisplayName("Log in existing user")
    public void login() {
        AuthData authentication = facade.login(this.user);
        Assertions.assertEquals("josephmama", authentication.username());
    }

    @Test
    @DisplayName("Log in non-existing user")
    public void loginNonExisting() {
        UserData fakeUser = new UserData("joemamajunior", "password", "email@email.com");
        Unauthorized ex = Assertions.assertThrows(Unauthorized.class, () -> facade.login(fakeUser));
        Assertions.assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    @DisplayName("Log in with wrong password")
    public void loginWrongPassword() {
        UserData user = new UserData("josephmama", "wrong password", null);
        Unauthorized ex = Assertions.assertThrows(Unauthorized.class, () -> facade.login(user));
        Assertions.assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    @DisplayName("Log out")
    public void logout() {
        Assertions.assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    @DisplayName("Log out unauthorized")
    public void logoutUnauthorized() {
        facade.logout(auth.authToken());
        Assertions.assertThrows(Unauthorized.class, () -> facade.logout(auth.authToken()));
    }

    @Test
    @DisplayName("List games")
    public void listGames() {
        String gameName = "gamey-wamey";
        int id = facade.createGame(gameName, auth.authToken());
        GameData[] games = facade.listGames(auth.authToken());

        Assertions.assertEquals(games[0].gameName(), gameName);
        Assertions.assertEquals(games[0].gameID(), id);
    }

    @Test
    @DisplayName("List games unauthorized")
    public void listGamesUnauthorized() {
        facade.logout(auth.authToken());

        Unauthorized ex = Assertions.assertThrows(Unauthorized.class, () -> facade.listGames(auth.authToken()));
        Assertions.assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    @DisplayName("Create new game")
    public void createGame() {
        String gameName1 = "gamey-wamey";
        int id1 = facade.createGame(gameName1, auth.authToken());

        String gameName2 = "other-game";
        int id2 = facade.createGame(gameName2, auth.authToken());

        GameData[] games = facade.listGames(auth.authToken());

        Assertions.assertEquals(games[0].gameName(), gameName1);
        Assertions.assertEquals(games[0].gameID(), id1);

        Assertions.assertEquals(games[1].gameName(), gameName2);
        Assertions.assertEquals(games[1].gameID(), id2);
    }

    @Test
    @DisplayName("Create new game unauthorized")
    public void createGameUnauthorized() {
        facade.logout(auth.authToken());

        Unauthorized ex = Assertions.assertThrows(Unauthorized.class, () -> facade.createGame("brother", auth.authToken()));
        Assertions.assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    @DisplayName("Join game as white")
    public void joinWhite() {
        String gameName = "gamey-wamey";
        int id = facade.createGame(gameName, auth.authToken());

        facade.joinGame(id, "white", auth.authToken());
        GameData[] games = facade.listGames(auth.authToken());
        GameData game = games[0];

        Assertions.assertEquals(id, game.gameID());
        Assertions.assertEquals(user.username(), game.whiteUsername());
        Assertions.assertEquals(gameName, game.gameName());
    }

    @Test
    @DisplayName("Join game as black")
    public void joinBlack() {
        String gameName = "gamey-wamey";
        int id = facade.createGame(gameName, auth.authToken());

        facade.joinGame(id, "black", auth.authToken());
        GameData[] games = facade.listGames(auth.authToken());
        GameData game = games[0];

        Assertions.assertEquals(id, game.gameID());
        Assertions.assertEquals(user.username(), game.blackUsername());
        Assertions.assertEquals(gameName, game.gameName());
    }

    @Test
    @DisplayName("Attempt join game where color taken")
    public void joinColorTaken() {
        String gameName = "gamey-wamey";
        int id = facade.createGame(gameName, auth.authToken());
        facade.joinGame(id, "black", auth.authToken());

        AlreadyTaken ex = Assertions.assertThrows(AlreadyTaken.class, () -> facade.joinGame(id, "black", auth.authToken()));
        Assertions.assertEquals("Unique resource already taken", ex.getMessage());
    }

    @Test
    @DisplayName("Attempt join non-existing game")
    public void joinNonExisting() {
        int id = 900;

        InvalidCommand ex = Assertions.assertThrows(InvalidCommand.class, () -> facade.joinGame(id, "black", auth.authToken()));
        Assertions.assertEquals("Invalid command", ex.getMessage());
    }

}
