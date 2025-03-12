package server;

import dataaccess.DataAccess;
import dataaccess.DatabaseDataAccess;
import dataaccess.MemoryDataAccess;
import server.handlers.*;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    DataAccess memoryData;
    UserService userService;
    GameService gameService;

    public Server() {
        try {
            memoryData = new MemoryDataAccess();
            userService = new UserService(memoryData);
            gameService = new GameService(memoryData);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String registerUser(Request req, Response res) {
        var handler = new RegisterHandler(this.userService);
        return handler.register(req, res);
    }

    private String login(Request req, Response res) {
        var handler = new LoginHandler(this.userService);
        return handler.login(req, res);
    }

    private String logout(Request req, Response res) {
        var handler = new LogoutHandler(this.userService);
        return handler.logout(req, res);
    }

    private String createGame(Request req, Response res) {
        var handler = new CreateGameHandler(gameService);
        return handler.createNewGame(req, res);
    }

    private String listGames(Request req, Response res) {
        var handler = new ListGamesHandler(gameService);
        return handler.listGames(req, res);
    }

    private String joinGame(Request req, Response res) {
        var handler = new JoinGameHandler(gameService);
        return handler.joinGame(req, res);
    }

    private String clear(Request req, Response res) {
        var handler = new ClearHandler(gameService);
        return handler.clear(req, res);
    }
}
