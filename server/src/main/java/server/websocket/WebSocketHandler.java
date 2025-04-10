package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.HashMap;

import static server.websocket.Connection.Role.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {

    private final HashMap<Integer, String> COORDINATES = generateCoordinateMap();
    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;
    private final WebsocketService service;
    private boolean canMove = true;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        System.out.println(dataAccess);
        System.out.println(dataAccess.getGame(5));
        this.service = new WebsocketService(dataAccess);
    }

    @OnWebSocketMessage
    public void onCommand(Session session, String message) throws IOException, DataAccessException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            System.out.println(command);
            switch (command.getCommandType()) {
                case LEAVE -> leaveGame(command);
                case RESIGN -> resign(command);
                case CONNECT -> connect(session, command);
                case MAKE_MOVE -> makeMove(command);
            }
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UnauthorizedException e) {
            session.getRemote().sendString(new Gson().toJson(new ServerMessage(ERROR, e.getMessage())));
        }

    }

    private void leaveGame(UserGameCommand command) throws IOException {
        System.out.println("leave game called");
        Connection connection = connections.getConnection(command.getGameID(), command.getAuthToken());
        String role = connection.getRoleString();
        if (role.equals("observer")) {
            String message = connection.username + " stopped watching the game";
            connections.remove(command.getGameID(), command.getAuthToken());
            ServerMessage notification = new ServerMessage(NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
        } else {
            String message = connection.username + " has left the game";
            service.leave(command.getGameID(), role);
            connections.remove(command.getGameID(), command.getAuthToken());
            ServerMessage notification = new ServerMessage(NOTIFICATION, message);
            connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
        }
    }

    private void resign(UserGameCommand command) {
        try {

            Connection connection = connections.getConnection(command.getGameID(), command.getAuthToken());
            String role = connection.getRoleString();
            if (role.equals("observer")) {
                connections.send(command.getGameID(), command.getAuthToken(), new ServerMessage(ERROR, "observers cannot resign"));
            } else if (canMove) {
                ServerMessage message = new ServerMessage(NOTIFICATION, connection.username + " resigned from the game.");
                connections.broadcast(command.getGameID(), "", message);
                canMove = false;
            } else {
                connections.send(command.getGameID(), command.getAuthToken(), new ServerMessage(ERROR, "This game has ended"));
            }
            System.out.println("resign called");
        } catch (IOException e) {
            connections.remove(command.getGameID(), command.getAuthToken());
        }
    }

    private void connect(Session session, UserGameCommand command) {
        try {
            UserData user = dataAccess.authenticate(command.getAuthToken());
            if (user == null) {
                throw new UnauthorizedException("Unauthorized user");
            }
            GameData game = dataAccess.getGame(command.getGameID());

            if (game == null) {
                throw new DataAccessException("Game does not exist");
            }

            Connection.Role role = getRole(game, user.username());
            connections.add(command.getAuthToken(), command.getGameID(), role, session, dataAccess);

            ServerMessage message = new ServerMessage(game.game());
            connections.send(game.gameID(), command.getAuthToken(), message);

            message = new ServerMessage(NOTIFICATION, user.username() + " joined the game as " + role);
            connections.broadcast(game.gameID(), command.getAuthToken(), message);
        } catch (UnauthorizedException e) {
            try {
                String message = new Gson().toJson(new ServerMessage(ERROR, "invalid authorization"));
                session.getRemote().sendString(message);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (DataAccessException e) {
            try {
                System.out.println("Data access exception");
                String message = new Gson().toJson(new ServerMessage(ERROR, "Game does not exist"));
                session.getRemote().sendString(message);
            } catch (IOException ex) {
                System.out.println("IO EXCEPTION inner");
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            System.out.println("IO EXCEPTION outer");
            throw new RuntimeException(e);
        }
    }

    private void makeMove(UserGameCommand command)  throws UnauthorizedException{
        System.out.println("Handler is making move");
        try {
            try {
                Connection conn = connections.getConnection(command.getGameID(), command.getAuthToken());
                if (conn == null) {
                    throw new UnauthorizedException("Unauthorized");
                }
                if (!canMove) {
                    throw new InvalidMoveException("The game is over. No more moves can be made");
                }
                System.out.println("connection is good");
                GameData game = service.makeMove(command.getGameID(), command.getMove(), conn.role);
                String announcement = announcement(game);
                if (announcement == null) {
                    ServerMessage board = new ServerMessage(game.game());
                    ServerMessage notification = new ServerMessage(NOTIFICATION, conn.username + describeMove(command.getMove()));
                    connections.broadcast(command.getGameID(), "", board);
                    connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
                } else {
                    ServerMessage board = new ServerMessage(game.game());
                    ServerMessage notification = new ServerMessage(NOTIFICATION, conn.username + describeMove(command.getMove()) + "\n" + announcement);
                    connections.broadcast(command.getGameID(), "", board);
                    connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
                }
            } catch (InvalidMoveException e) {
                ServerMessage error = new ServerMessage(ERROR, e.getMessage());
                connections.send(command.getGameID(), command.getAuthToken(), error);
            }
        } catch (IOException ignored) {
            System.out.println("cray cray bro");
        }
    }

    private Connection.Role getRole(GameData game, String username) {
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (!username.equals(white) && !username.equals(black)) {
            return OBSERVER;
        }
        return username.equals(white) ? WHITE : BLACK;
    }

    private String describeMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        return " moved piece at " + convertPositionToString(start) +  " to " + convertPositionToString(end);

    }

    private String gameToJson(GameData game) {
        return new Gson().toJson(game);
    }

    private String convertPositionToString(ChessPosition position) {
        return COORDINATES.get(position.getColumn()) + position.getRow();
    }

    private HashMap<Integer, String> generateCoordinateMap() {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(4, "d");
        map.put(5, "e");
        map.put(6, "f");
        map.put(7, "g");
        map.put(8, "h");
        return map;
    }

    private boolean isWhiteCheck(GameData gameData) {
        System.out.println("Is white in check?");
        ChessGame game = gameData.game();
        System.out.println(game.isInCheck(ChessGame.TeamColor.WHITE));
        return game.isInCheck(ChessGame.TeamColor.WHITE);
    }

    private boolean isBlackCheck(GameData gameData) {
        System.out.println("Is white in check?");
        ChessGame game = gameData.game();
        System.out.println(game.isInCheck(ChessGame.TeamColor.BLACK));
        return game.isInCheck(ChessGame.TeamColor.BLACK);
    }

    private boolean isWhiteCheckmate(GameData gameData) {
        System.out.println("Is white in check?");
        ChessGame game = gameData.game();
        return game.isInCheckmate(ChessGame.TeamColor.WHITE);
    }

    private boolean isBlackCheckmate(GameData gameData) {
        System.out.println("Is white in check?");
        ChessGame game = gameData.game();
        return game.isInCheckmate(ChessGame.TeamColor.BLACK);
    }

    private boolean isWhiteStalemate(GameData gameData) {
        System.out.println("Is white in check?");
        ChessGame game = gameData.game();
        return game.isInCheckmate(ChessGame.TeamColor.WHITE);
    }

    private boolean isBlackStalemate(GameData gameData) {
        System.out.println("Is white in check?");
        ChessGame game = gameData.game();
        return game.isInStalemate(ChessGame.TeamColor.BLACK);
    }

    private String announcement(GameData gameData) {
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (isWhiteCheckmate(gameData)) {
            canMove = false;
            return white + "(white) in checkmate. " + black + " wins!";
        } else if (isBlackCheckmate(gameData)) {
            canMove = false;
            return black + "(white) in checkmate. " + white + " wins!";
        } else if (isWhiteCheck(gameData)) {
            return white + " (white) in check";
        } else if (isBlackCheck(gameData)) {
            return black + "(black) in check";
        } else if (isWhiteStalemate(gameData)) {
            canMove = false;
            return "The game is in stalemate";
        } else if (isBlackStalemate(gameData)) {
            canMove = false;
            return "The game is in stalemate";
        }
        return null;
    }
}
