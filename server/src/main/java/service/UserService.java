package service;


import dataaccess.*;
import server.request.LoginRequest;
import server.request.LogoutRequest;
import server.request.RegisterRequest;
import server.response.*;
import com.google.gson.Gson;

import java.util.UUID;

public class UserService extends Service {

    public UserService(MemoryDataAccess memoryData) {
        super(memoryData);
    }

    public LoginResponse login(LoginRequest req) throws UnauthorizedException {
        UserData user = memoryData.getUser(req.username());
        System.out.println(user);
        if (user == null || !req.password().equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
       else {
            AuthData authData = createAuth(user);
            return new LoginResponse(200, authData);
        }
    }

    public LogoutResponse logout(LogoutRequest req) throws UnauthorizedException {
        String authToken = req.authToken();
        authenticate(req.authToken());
        memoryData.removeAuthToken(authToken);
        return new LogoutResponse(200);
    }

    public LoginResponse register(RegisterRequest req) throws DataAccessException {
        var user = new UserData(req.username(), req.password(), req.email());
        memoryData.createUser(user);

        return login(new LoginRequest(req.username(), req.password()));
    }

    private AuthData createAuth(UserData userData) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        memoryData.createAuth(authData);
        return authData;
    }
}
