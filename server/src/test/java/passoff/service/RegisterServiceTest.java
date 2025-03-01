package passoff.service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import server.request.RegisterRequest;
import server.response.LoginResponse;
import service.UserService;
import java.util.UUID;

public class RegisterServiceTest {

    private MemoryDataAccess memoryData;
    private UserService service;

    @BeforeEach
    public void setUp() throws DataAccessException {
        memoryData = new MemoryDataAccess();
        service = new UserService(memoryData);
        memoryData.createUser(new UserData("joemama", "password", "joemama@gmail.com"));
    }

    @Test
    public void testRegisterUser() throws DataAccessException {
        LoginResponse expected = new LoginResponse(200, new AuthData(UUID.randomUUID().toString(), "joemamajunior"));
        RegisterRequest request = new RegisterRequest("joemamajunior", "password", "joe");

        LoginResponse actual = service.register(request);
        Assertions.assertEquals(expected.status(), actual.status());
        Assertions.assertEquals(expected.authData().username(), actual.authData().username());
        Assertions.assertNotEquals(expected.authData().authToken(), actual.authData().authToken());
    }

    @Test
    public void testUserAlreadyRegisteredThrowsUnauthorized() {

        RegisterRequest request = new RegisterRequest("joemama", "password", "joemama@gmail.com");

        DataAccessException exception = Assertions.assertThrows(DataAccessException.class, () -> service.register(request));
        Assertions.assertEquals("Error: username taken", exception.getMessage());
    }
}
