package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import server.request.LoginRequest;
import server.request.LogoutRequest;
import server.request.RegisterRequest;
import server.response.LoginResponse;
import server.response.LogoutResponse;

import java.util.UUID;

public class UserService extends Service {

    public UserService(DataAccess memoryData) {
        super(memoryData);
    }

    public LoginResponse login(LoginRequest req) throws DataAccessException {
        if (memoryData.isValidPassword(req.username(), req.password())) {
            AuthData authData = createAuth(memoryData.getUser(req.username()));
            return new LoginResponse(200, authData);
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public LogoutResponse logout(LogoutRequest req) throws DataAccessException {
        String authToken = req.authToken();
        authenticate(req.authToken());
        memoryData.removeAuthToken(authToken);
        return new LogoutResponse(200);
    }

    public LoginResponse register(RegisterRequest req) throws DataAccessException {
        var user = new UserData(req.username(), req.password(), req.email());
        if (user.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        memoryData.createUser(user);
        return login(new LoginRequest(req.username(), req.password()));
    }

    private AuthData createAuth(UserData userData) throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        memoryData.createAuth(authData);
        return authData;
    }
}
