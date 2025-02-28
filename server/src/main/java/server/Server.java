package server;

import dataaccess.MemoryDataAccess;
import server.handlers.*;
import service.GameService;
import service.UserService;
import spark.*;
import spark.Request;
import spark.Response;

public class Server{

    MemoryDataAccess memoryData = new MemoryDataAccess();
    UserService userService = new UserService(memoryData);
    GameService gameService = new GameService(memoryData);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session",  this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", (req, res) -> "PUT /game");
        Spark.delete("/db", (req,res) -> "{}");
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();



        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public String registerUser(Request req, Response res) {
        var handler = new RegisterHandler(this.userService);
        return handler.register(req, res);
    }

    public String login(Request req, Response res) {
        var handler = new LoginHandler(this.userService);
        return handler.login(req, res);
    }

    public String logout(Request req, Response res) {
        var handler = new LogoutHandler(this.userService);
        return handler.logout(req, res);
    }

    public String createGame(Request req, Response res) {
        var handler = new CreateGameHandler(gameService);
        return handler.createNewGame(req, res);
    }

    public String listGames(Request req, Response res) {
        var handler = new ListGamesHandler(gameService);
        return handler.listGames(req, res);
    }
}
