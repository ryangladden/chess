package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import server.request.*;
import server.response.*;
import server.*;
import service.*;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler implements Handler{

    private UserService service;

    public RegisterHandler(UserService service) {
        this.service = service;
    }

    public String register(Request req, Response res) {
        try {
            var reqParsed = (RegisterRequest) parseRequest(req, RegisterRequest.class);
            LoginResponse regRes = service.register(reqParsed);
            res.status(regRes.status());
            return toJson(regRes);
        } catch (InvalidRequest e) {
            res.status(400);
            return errorToJson(e.getMessage());
        } catch (DataAccessException e) {
            res.status(403);
            return errorToJson(e.getMessage());
        }
    }

    private String toJson(LoginResponse regRes) {
        Gson serializer = new Gson();
        return serializer.toJson(regRes.authData());
    }
}
