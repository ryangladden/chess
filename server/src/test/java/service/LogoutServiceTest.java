package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.request.LoginRequest;
import server.request.LogoutRequest;
import server.request.RegisterRequest;
import server.response.LoginResponse;
import server.response.LogoutResponse;

import java.util.UUID;

public class LogoutServiceTest {

    MemoryDataAccess memoryData;
    UserService service;
    String username;
    String password;
    String email;
    String authToken;
    LogoutResponse expected;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryData = new MemoryDataAccess();
        service = new UserService(memoryData);
        expected = new LogoutResponse(200);
        username = "joemama";
        password = "password";
        email = "joemama@gmail.com";
        RegisterRequest regReq = new RegisterRequest(username, password, email);
        LoginResponse logRes = service.register(regReq);

        authToken = logRes.authData().authToken();
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        LogoutRequest logout = new LogoutRequest(authToken);

        LogoutResponse actual = service.logout(logout);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void loginAndLogout() throws UnauthorizedException, DataAccessException {
        LoginRequest login = new LoginRequest(username, password);
        String authToken = service.login(login).authData().authToken();

        LogoutRequest logout = new LogoutRequest(authToken);
        LogoutResponse actual = service.logout(logout);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void loginFailed() {
        LogoutRequest wrongToken = new LogoutRequest(UUID.randomUUID().toString());

        UnauthorizedException actual = Assertions.assertThrows(UnauthorizedException.class, () -> service.logout(wrongToken));
    }
}
