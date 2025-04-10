package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import model.GameData;
import service.Service;

public class WebsocketService extends Service {

    DataAccess memoryData;

    public WebsocketService(DataAccess memoryData) {
        super(memoryData);
        this.memoryData = memoryData;
        memoryData.getGame(5);
    }

    public GameData makeMove(int gameID, ChessMove move) throws InvalidMoveException {
        GameData gameState = memoryData.getGame(gameID);
        ChessGame game = gameState.game();
        game.makeMove(move);
        System.out.println(game);
        GameData gameData = new GameData(gameState.gameID(), gameState.whiteUsername(), gameState.blackUsername(), gameState.gameName(), game);
        memoryData.setGame(gameData);
        return gameData;
    }

    public void leave(int gameID, String role) {
        this.memoryData.removeUser(gameID, role);
    }
}
