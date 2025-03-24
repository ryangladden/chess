package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.UserData;

public abstract class Service {

    DataAccess memoryData;

    public Service(DataAccess memoryData) {
        this.memoryData = memoryData;
    }

    protected UserData authenticate(String authToken) throws DataAccessException {
        UserData user = memoryData.authenticate(authToken);
        if (user == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return user;
    }
}
