package server.handlers;

import com.google.gson.Gson;
import dataaccess.AuthData;
import dataaccess.DataAccessException;
import server.request.RegisterRequest;
import service.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler {

    private final RegisterService service;

    RegisterHandler(RegisterService service) {
        this.service = service;
        System.out.println("Request serialized into Record");

    }

    private RegisterRequest parseRequest(Request req) {
        Gson serializer = new Gson();
        return serializer.fromJson(req.body(), RegisterRequest.class);
    }

    public AuthData register(Request req, Response res) throws DataAccessException {
        RegisterRequest regReq = parseRequest(req);
        if (!this.service.userExists(regReq.username())) {
            this.service.createUser(regReq.username(), regReq.password(), regReq.email());
            return this.service.createAuth(regReq.username());
        }
        else {
            throw new DataAccessException("error: username already exists");
        }
    }


}
