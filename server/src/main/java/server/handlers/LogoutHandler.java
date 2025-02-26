package server.handlers;

import dataaccess.UnauthorizedException;
import server.request.LogoutRequest;
import server.response.LogoutResponse;
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
            LogoutRequest reqParsed = new LogoutRequest(getAuthToken(req));
            LogoutResponse logoutRes = service.logout(reqParsed.authentication());
            res.status(logoutRes.status());
            return "";
        } catch (UnauthorizedException e) {
            res.status(401);
            return errorToJson(e.getMessage());
        }
    }


}
