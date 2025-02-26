package server.handlers;

import server.request.LogoutRequest;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler implements Handler{

    UserService service;

    public LogoutHandler(UserService service) {
        this.service = service;
    }

    public String logout(Request req, Response res) {
        try {
            var reqParsed = parseRequest(req, LogoutRequest.class);

        }
        catch()
    }


}
