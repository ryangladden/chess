package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.InvalidRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        if (checkUsername(user.username())) {
            throw new UserExistsException("Error: already exists");
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement(sql)) {
                System.out.println("Arrived");
                stmt.setString(1, user.username());
                stmt.setString(2, encryptPassword(user.password()));
                stmt.setString(3, user.email());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(("Error: " + e.getMessage()));
        }
    }

    String encryptPassword(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt());
    }

    boolean checkUsername(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    void createAuth(AuthData authData) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO auth (token, user_id) VALUES (?, ?)";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authData.authToken());
                stmt.setInt(2, getIDfromUsername(authData.username()));
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    UserData authenticate(String authToken) {
        String sql = "SELECT user_id FROM auth WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return getUserFromID(rs.getInt("user_id"));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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

    private static final String[] initialStatements = {
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
            PRIMARY KEY (`token`),
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

    private UserData getUserFromID(int id) throws DataAccessException {
        String sql = "SELECT FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getIDfromUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
