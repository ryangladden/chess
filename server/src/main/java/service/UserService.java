package service;


import dataaccess.*;
import server.response.*;
import com.google.gson.Gson;

import java.util.UUID;

public class UserService extends Service {

    public UserService(MemoryDataAccess memoryData) {
        super(memoryData);
    }

    public LoginResponse login(String username, String password) throws UnauthorizedException {
        UserData user = memoryData.getUser(username);
        System.out.println(user);
        if (user == null || !password.equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
       else {
            AuthData authData = createAuth(user);
            return new LoginResponse(200, authData);
        }
    }

    public LogoutResponse logout(String authToken) throws UnauthorizedException {
        authenticate(authToken);
        memoryData.removeAuthToken(authToken);
        return new LogoutResponse(200);
    }

    public LoginResponse register(String username, String password, String email) throws DataAccessException {
        var user = new UserData(username, password, email);
        memoryData.createUser(user);
        return login(username, password);
    }

    private AuthData createAuth(UserData userData) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        memoryData.createAuth(authData);
        return authData;
    }
}
