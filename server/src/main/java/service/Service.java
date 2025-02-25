package service;
import dataaccess.AuthData;

import java.util.UUID;

public abstract class Service {

    public boolean verifyAuth(String authToken) {
        return true;
    }
}
