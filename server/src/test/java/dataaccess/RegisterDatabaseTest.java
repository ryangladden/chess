package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterDatabaseTest {

    DatabaseDataAccess dataAccess;

    @BeforeEach
    public void setUP() throws Exception {
        dataAccess = new DatabaseDataAccess();
    }

    @Test
    public void registerUser() throws DataAccessException {
        UserData newUser = new UserData("joemama", "password", "joemama@email.com");
        dataAccess.createUser(newUser);


    }

    @Test
    public void attemptRegisterUserExists() {

    }
}
