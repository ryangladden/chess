package service;
import dataaccess.AuthData;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import dataaccess.UserData;

import java.util.UUID;

public abstract class Service {

    MemoryDataAccess memoryData;

    public Service(MemoryDataAccess memoryData) {
        this.memoryData = memoryData;
    }

    protected UserData authenticate(String authToken) throws UnauthorizedException {
        UserData user = memoryData.authenticate(authToken);
        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return user;
    }
}
