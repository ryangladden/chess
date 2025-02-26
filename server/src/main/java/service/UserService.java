package service;


import dataaccess.*;
import server.response.*;
import com.google.gson.Gson;

import java.util.UUID;

public class UserService extends Service {

    MemoryDataAccess memoryData;

    public UserService(MemoryDataAccess memoryData) {
        this.memoryData = memoryData;
    }

    public LoginResponse login(String username, String password) throws UnauthorizedException {
        UserData user = this.memoryData.getUser(username);
        if (user == null || password != user.password()) {
            throw new UnauthorizedException("Error: unauthorized");
        }
       else {
            System.out.println("success baby");
            AuthData authData = createAuth(user);
            return new LoginResponse(200, authData);
        }
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

    private String serializeAuthResponse(AuthData authData) {
        Gson serializer = new Gson();
        String json = serializer.toJson(authData);
        return json;
    }
}
