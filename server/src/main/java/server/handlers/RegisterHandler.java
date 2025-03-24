package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import dataaccess.UserExistsException;
import server.InvalidRequest;
import server.request.RegisterRequest;
import server.response.LoginResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler implements Handler {

    private final UserService service;

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
        } catch (UserExistsException e) {
            res.status(403);
            return errorToJson(e.getMessage());
        } catch (DataAccessException e) {
            res.status(400);
            return errorToJson(e.getMessage());
        }
    }

    private String toJson(LoginResponse regRes) {
        Gson serializer = new Gson();
        return serializer.toJson(regRes.authData());
    }
}
