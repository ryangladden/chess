package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import server.InvalidRequest;
import server.request.RegisterRequest;
import server.response.RegisterResponse;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private UserService service;

    public RegisterHandler(UserService service) {
        this.service = service;
    }

    private RegisterRequest parseRequest(Request req) throws InvalidRequest{
        try {
            Gson serializer = new Gson();
            return serializer.fromJson(req.body(), RegisterRequest.class);
        }
        catch (JsonSyntaxException e) {
            throw new InvalidRequest("{\"message\": \"Error: bad request\"}");
        }
    }

    public Object register(Request req, Response res) {
        RegisterResponse regRes;
        try {
            var reqParsed = parseRequest(req);
            regRes = service.register(reqParsed.username(), reqParsed.password(), reqParsed.email());
            res.status(regRes.status());
            return regRes.body();
        } catch (InvalidRequest e) {
            res.status(400);
            return e.getMessage();
        }
    }

}
