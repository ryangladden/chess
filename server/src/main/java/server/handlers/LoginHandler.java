package server.handlers;

import com.google.gson.Gson;
import server.request.LoginRequest;
import service.UserService;
import spark.Request;

public class LoginHandler{

    private UserService service;

    public LoginHandler(UserService service) {
        this.service = service;
    }

    private LoginRequest parseRequest(Request req) {
        Gson serializer = new Gson();
        LoginRequest logReq = serializer.fromJson(req.body(), LoginRequest.class);
        return logReq;
    }
}
