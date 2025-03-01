package passoff.service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import service.GameService;

public class CreateGameTest {

    MemoryDataAccess memoryData;
    GameService service;

    @BeforeEach
    public void setUp() {
        memoryData = new MemoryDataAccess();
        service = new GameService(memoryData);
    }

    public void createGameSuccess() {

    }
}
