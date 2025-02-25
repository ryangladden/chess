package service;
import dataaccess.AuthData;

import java.util.UUID;

public abstract class Service {

    public AuthData createAuth(String username) {
        return new AuthData(UUID.randomUUID().toString(), username);
    }

    public boolean verifyAuth(String authToken) {
        return true;
    }
}
