package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.InvalidRequest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class DatabaseDataAccess extends DataAccess{


    public DatabaseDataAccess() throws Exception{
        createDatabase();
    }

    @Override
    void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.username());
                stmt.setString(2, encryptPassword(user.password()));
                stmt.setString(3, user.email());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String encryptPassword(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt());
    }

    boolean checkUsername(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT username FROM users";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    void createAuth(AuthData authData) {

    }

    @Override
    UserData authenticate(String authToken) {
        return null;
    }

    @Override
    UserData getUser(String username) {
        return null;
    }

    @Override
    void removeAuthToken(String authToken) {

    }

    @Override
    int createNewGame(String gameName) {
        return 0;
    }

    @Override
    Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    void joinGame(UserData user, int gameID, String playerColor) throws InvalidRequest, ColorTakenException, InvalidGameID {

    }

    @Override
    void clear() {

    }

    private static String[] initialStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `id` int NOT NULL AUTO_INCREMENT,
            `username` VARCHAR(256) NOT NULL,
            `password` VARCHAR(256) NOT NULL,
            `email` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`id`),
            INDEX (`username`)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
            `user_id` int NOT NULL,
            `token` CHAR(36) NOT NULL,
            PRIMARY KEY (`user_id`),
            FOREIGN KEY (`user_id`) REFERENCES users(`id`)
            );
            """,
            """
            
            CREATE TABLE IF NOT EXISTS games (
            `id` int NOT NULL AUTO_INCREMENT,
            `name` VARCHAR(256) NOT NULL,
            `white` int DEFAULT NULL,
            `black` int DEFAULT NULL,
            `game` TEXT DEFAULT NULL,
            PRIMARY KEY(`id`),
            FOREIGN KEY (`white`) REFERENCES users(`id`) ON DELETE SET NULL,
            FOREIGN KEY (`black`) REFERENCES users(`id`) ON DELETE SET NULL
            );
            """
    };

    private static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : initialStatements)
                try (var stmt = conn.prepareStatement(statement)) {
                    stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
