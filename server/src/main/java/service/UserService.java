package service;


import dataaccess.AuthData;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UserData;
import server.response.*;
import com.google.gson.Gson;

import java.util.UUID;

public class UserService extends Service {

    MemoryDataAccess memoryData;

    public UserService(MemoryDataAccess memoryData) {
        this.memoryData = memoryData;
    }

    public LoginResponse login(String username, String password) {
        UserData user = this.memoryData.getUser(username);
        if (user == null || password != user.password()) {
            return new LoginResponse(401, "{ \"message\": \"Error: unauthorized\" }");
        }
       else {
            AuthData authData = createAuth(user);
            return new LoginResponse(200, serializeAuthResponse(authData));
        }
    }

    public RegisterResponse register(String username, String password, String email) {
        try {
            var user = new UserData(username, password, email);
            memoryData.createUser(user);
            var auth = createAuth(user);
            return registerSuccess(auth);
        }
        catch(DataAccessException e) {
            return registerFailure(e.getMessage());
        }
    }

    private RegisterResponse registerSuccess(AuthData authData) {
        var auth = serializeAuthResponse(authData);
        var res = new RegisterResponse(200, auth);
        System.out.println(res);
        return res;
    }

    private RegisterResponse registerFailure(String message) {
        return new RegisterResponse(403, "{\"message\": \""+ message + "\"}");
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
