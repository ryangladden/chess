package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.UnauthorizedException;
import model.GameData;
import service.Service;

public class WebsocketService extends Service {

    DataAccess memoryData;

    public WebsocketService(DataAccess memoryData) {
        super(memoryData);
        this.memoryData = memoryData;
        memoryData.getGame(5);
    }

    public GameData makeMove(int gameID, ChessMove move, Connection.Role role) throws InvalidMoveException, UnauthorizedException {
        if (role == Connection.Role.OBSERVER) {
            throw new UnauthorizedException("Observers cannot make moves");
        }
        GameData gameState = memoryData.getGame(gameID);
        ChessGame game = gameState.game();
        String userTeam = userColor(role);
        String teamTurn = turnColor(game);
        if (teamTurn.equals(userTeam)) {
            game.makeMove(move);
            System.out.println(game);
            GameData gameData = new GameData(gameState.gameID(), gameState.whiteUsername(), gameState.blackUsername(), gameState.gameName(), game);
            memoryData.setGame(gameData);
            return gameData;
        } else {
            throw new InvalidMoveException("It's not your turn");
        }
    }

    public void leave(int gameID, String role) {
        this.memoryData.removeUser(gameID, role);
    }

    private String userColor(Connection.Role role) {
        return switch(role) {
            case WHITE -> "white";
            case BLACK -> "black";
            case OBSERVER -> "observer";
        };
    }

    private String turnColor(ChessGame game) {
        return switch(game.getTeamTurn()) {
            case WHITE -> "white";
            case BLACK -> "black";
        };
    }

    public void deleteGame(int gameID) {
        memoryData.deleteGame(gameID);
    }
}
