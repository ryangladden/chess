package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.InvalidRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseDataAccess extends DataAccess {


    private static final String[] CREATE_TABLES = {
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
            `game` JSON DEFAULT ('""" + new ChessGame().toJson() + "'),\n" +
            """
            PRIMARY KEY(`id`),
            FOREIGN KEY (`white`) REFERENCES users(`id`) ON DELETE SET NULL,
            FOREIGN KEY (`black`) REFERENCES users(`id`) ON DELETE SET NULL
            );
            """
    };

    public DatabaseDataAccess() throws DataAccessException {
        createDatabase();
    }

    private static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : CREATE_TABLES) {
                try (var stmt = conn.prepareStatement(statement)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: database could not be initialized");
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        if (checkUsername(user.username())) {
            throw new UserExistsException("Error: username taken");
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.username());
                stmt.setString(2, encryptPassword(user.password()));
                stmt.setString(3, user.email());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: registration failed");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failure to connect to database");
        }
    }

    String encryptPassword(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt());
    }

    public boolean isValidPassword(String username, String password) throws DataAccessException {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return BCrypt.checkpw(password, rs.getString("password"));
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal server error");
        }
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
            throw new DataAccessException("Error: internal server error");
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO auth (token, user_id) VALUES (?, ?)";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authData.authToken());
                stmt.setInt(2, getIDfromUsername(authData.username()));
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: auth could not be created");
        }
    }

    @Override
    public UserData authenticate(String authToken) throws DataAccessException {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: internal server error");
        }
    }

    @Override
    public UserData getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), null, rs.getString("email"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void removeAuthToken(String authToken) throws UnauthorizedException {
        String sql = "DELETE FROM auth WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (authenticate(authToken) == null) {
                    throw new UnauthorizedException("Error: unauthorized");
                }
                stmt.setString(1, authToken);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    @Override
    public int createNewGame(String gameName) {
        String sql = "INSERT INTO games (name) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)) {
                if (gameName.isEmpty()) {
                    throw new DataAccessException("Error: invalid game name");
                }
                stmt.setString(1, gameName);
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<GameData> listGames() {
        String sql = "SELECT * FROM games";
        Collection<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    GameData game = getGame(rs);
                    GameData gameNull = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null);
                    games.add(gameNull);
                }
                return games;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinGame(UserData user, int gameID, String playerColor) throws InvalidRequest, ColorTakenException, InvalidGameID {
        if (validJoinGame(gameID, playerColor)) {
            String join = "UPDATE games SET " + playerColor.toLowerCase() + " = ? WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(join)) {
                    stmt.setInt(1, getIDfromUsername(user.username()));
                    stmt.setInt(2, gameID);
                    stmt.executeUpdate();
                }
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ColorTakenException("Error: game taken");
        }
    }

    @Override
    public void clear() {
        String[] statements = {
                """
                DELETE FROM games;
                """,
                """
                ALTER TABLE games AUTO_INCREMENT = 1;
                """,
                """
                DELETE FROM auth
                """,
                """
                DELETE FROM users;
                """,
                """
                ALTER TABLE users AUTO_INCREMENT = 1;
                """
        };
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String sql : statements) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) {
        String sql = """
        SELECT games.*,
        white.username AS white_player,
        black.username AS black_player
        FROM games
        LEFT JOIN users AS white ON games.white = white.id
        LEFT JOIN users AS black on games.black = black.id
        WHERE games.id = ?""";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println(parseGameResult(rs));
                    return parseGameResult(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private GameData parseGameResult(ResultSet rs) throws SQLException, DataAccessException {
        String json = rs.getString("game");
        int id = rs.getInt("id");
        String white = rs.getString("white_player");
        String black = rs.getString("black_player");
        String name = rs.getString("name");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(id, white, black, name, game);
    }

    private UserData getUserFromID(int id) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer getIDfromUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public GameData getGame(ResultSet rs) throws SQLException, DataAccessException {
        System.out.println("getting that game");
        int id = rs.getInt("id");
        int whiteResult = rs.getInt("white");
        int blackResult = rs.getInt("black");
        String gameName = rs.getString("name");
        String json = rs.getString("game");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);
        System.out.println(game.getBoard());
        return new GameData(
                id,
                whiteResult == 0 ? null : getUserFromID(whiteResult).username(),
                blackResult == 0 ? null : getUserFromID(blackResult).username(),
                gameName,
                game
        );
    }

    public void setGame(GameData game) {
        String sql = "UPDATE games SET game = ? WHERE id = ?";
        String queryGame = "SELECT * FROM games WHERE id = ?";
        String json = new Gson().toJson(game.game());
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, json);
                stmt.setInt(2, game.gameID());
                System.out.println(sql);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("You got an sql exception, something went wrong");
        } catch (DataAccessException e) {
            System.out.println("e");
        }
    }

    private boolean validJoinGame(int gameID, String playerColor) throws InvalidGameID {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int colorId = rs.getInt(playerColor.toLowerCase());
                    return (colorId == 0);
                } else {
                    throw new InvalidGameID("Error: game does not exist");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUser(int gameID, String role) {
        String sql = "UPDATE games SET " + role + " = NULL WHERE id = ?;";
        System.out.println(sql);
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1,gameID);
                stmt.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteGame(int gameID) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                stmt.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
        throw new RuntimeException(e);
        }
    }
}
