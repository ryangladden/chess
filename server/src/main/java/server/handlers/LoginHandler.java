package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.UnauthorizedException;
import server.InvalidRequest;
import server.request.LoginRequest;
import server.response.LoginResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler implements Handler{

    private UserService service;

    public LoginHandler(UserService service) {
        this.service = service;
    }

    public String login(Request req, Response res) {
        try {
            LoginRequest reqParsed = parseRequest(req, LoginRequest.class);
            LoginResponse logRes = service.login(reqParsed.username(), reqParsed.password());
            res.status(logRes.status());
            return toJson(logRes);
        }
        catch(InvalidRequest e) {
            res.status(400);
            return errorToJson(e.getMessage());
        }
        catch(UnauthorizedException e) {
            res.status(401);
            return errorToJson(e.getMessage());
        }
    }

    private String toJson(LoginResponse regRes) {
        return new Gson().toJson(regRes.authData());
    }

}
