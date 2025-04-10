package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.InvalidRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.lang.String.valueOf;

public class MemoryDataAccess extends DataAccess {

    private HashMap<String, UserData> authTokens = new HashMap<>();
    private HashMap<String, GameData> games = new HashMap<>();
    private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.getOrDefault(user.username(), null) != null) {
            throw new UserExistsException("Error: username taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public boolean isValidPassword(String username, String password) throws DataAccessException {
        try {
            UserData user = getUser(username);
            if (user == null) {
                return false;
            }
            return password.equals(user.password());
        } catch (Exception e) {
            throw new DataAccessException("Error: internal server error");
        }
    }

    @Override
    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public UserData authenticate(String authToken) {
        return authTokens.getOrDefault(authToken, null);
    }

    @Override
    public void createAuth(AuthData authData) {
        authTokens.put(authData.authToken(), users.get(authData.username()));
    }

    @Override
    public void removeAuthToken(String authToken) throws UnauthorizedException {
        if (!authTokens.containsKey(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        authTokens.remove(authToken);
    }

    @Override
    public int createNewGame(String gameName) {
        int gameID = getNextID();
        String strGameID = valueOf(gameID);
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(strGameID, game);
        return gameID;
    }

    @Override
    public Collection<GameData> listGames() {
        ArrayList<GameData> gameList = new ArrayList<GameData>();
        for (String key : games.keySet()) {
            gameList.add(removeBoard(games.get(key)));
        }
        return gameList;
    }

    @Override
    public void joinGame(UserData user, int gameID, String playerColor) throws InvalidRequest, ColorTakenException, InvalidGameID {
        String gameIdStr = valueOf(gameID);
        if (!games.containsKey(gameIdStr)) {
            throw new InvalidGameID("Error: invalid game ID");
        }
        GameData game = games.get(gameIdStr);
        switch (playerColor) {
            case "WHITE":
                if (game.whiteUsername() == null) {
                    games.replace(gameIdStr, new GameData(gameID, user.username(), game.blackUsername(), game.gameName(), game.game()));
                    break;
                } else {
                    throw new ColorTakenException("Error: already taken");
                }
            case "BLACK":
                if (game.blackUsername() == null) {
                    games.replace(gameIdStr, new GameData(gameID, game.whiteUsername(), user.username(), game.gameName(), game.game()));
                    break;
                } else {
                    throw new ColorTakenException("Error: already taken");
                }
            case null, default:
                throw new InvalidRequest("Error: bad request");
        }
    }

    @Override
    public void clear() {
        this.authTokens = new HashMap<>();
        this.users = new HashMap<>();
        this.games = new HashMap<>();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(valueOf(gameID));
    }

    private GameData removeBoard(GameData game) {
        return new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null);
    }

    @Override
    public void setGame(GameData game) {
        games.put(valueOf(game.gameID()), game);
    }

    @Override
    public void removeUser(int gameID, String role) {

    }

    @Override
    public void deleteGame(int gameID) {

    }
}
