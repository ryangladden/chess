package service;

import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import dataaccess.UserData;

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
