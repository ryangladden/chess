package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.LoginRequest;
import server.response.LoginResponse;

import java.util.UUID;

public class LoginServiceTest {

    private MemoryDataAccess memoryData;
    private UserService service;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryData = new MemoryDataAccess();
        service = new UserService(memoryData);

        memoryData.createUser(new UserData("joemama", "password", "joemama@gmail.com"));
    }

    @Test
    public void loginExistingUser() throws DataAccessException {
        LoginRequest request = new LoginRequest("joemama", "password");
        LoginResponse expected = new LoginResponse(200, new AuthData(UUID.randomUUID().toString(), "joemama"));

        LoginResponse actual = service.login(request);

        Assertions.assertEquals(expected.status(), actual.status());
        Assertions.assertEquals(expected.authData().username(), actual.authData().username());
        Assertions.assertNotEquals(expected.authData().authToken(), actual.authData().authToken());
    }

    @Test
    public void deniedWrongPassword() {
        UnauthorizedException exception = new UnauthorizedException("Error: unauthorized");
        LoginRequest request = new LoginRequest("joemama", "Password");

        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(request));
    }

    @Test
    public void userDoesNotExist() {
        LoginRequest request = new LoginRequest("joemam", "password");

        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> service.login(request));
        Assertions.assertEquals("Error: unauthorized", exception.getMessage());
    }

}
