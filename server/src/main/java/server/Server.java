package server;

import com.google.gson.Gson;
import spark.*;
import spark.Request;
import spark.Response;

public class Server{

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session",  (req, res) -> "POST /session");
        Spark.delete("/session", (req, res) -> "DELETE /session");
        Spark.get("/game", (req, res) -> "GET /game");
        Spark.post("/game", (req, res) -> "POST /game");
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

    public Object registerUser(Request req, Response res) {
        System.out.println("Incoming POST /user");
        var obj = new RegisterHandler(req);
        return null;
    }
}
