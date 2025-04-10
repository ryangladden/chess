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
        System.out.println("Handler passed move to service");
        if (role == Connection.Role.OBSERVER) {
            throw new UnauthorizedException("Observers cannot make moves");
        }
        GameData gameState = memoryData.getGame(gameID);
        GameData gameee = memoryData.getGame(gameID);
        System.out.println("retreived this: " + gameee + "\n\nAnd this: " + gameee.game().getBoard());
        ChessGame game = gameState.game();
        System.out.println(game.getBoard());
        String userTeam = userColor(role);
        String teamTurn = turnColor(game);
        System.out.println(userTeam);
        System.out.println(role);
        System.out.println(game.getTeamTurn());
        System.out.println(teamTurn);
        System.out.println("Successfully retreived game");
        if (teamTurn.equals(userTeam)) {
            System.out.println("User is the correct team");
            System.out.println("Making move...");
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
}
