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
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.*;

import static server.websocket.Connection.Role.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {

    private static HashMap<Integer, String> COORDINATES = generateCoordinateMap();
    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;
    private final WebsocketService service;

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
            System.out.println("JSON ERROR");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("IOException IN MAIN HANDLER THINGY");
            throw new RuntimeException(e);
        } catch (UnauthorizedException e) {
            System.out.println("UNAUTHORIZED EXCEPTION in MAIN HANDLER THingY");
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
        System.out.println("resign called");
        try {
            Connection connection = connections.getConnection(command.getGameID(), command.getAuthToken());
            String role = connection.getRoleString();
            if (connection.gameOver) {
                connections.send(command.getGameID(), command.getAuthToken(), new ServerMessage(ERROR, "This game has already ended"));
            } else if (role.equals("observer")) {
                connections.send(command.getGameID(), command.getAuthToken(), new ServerMessage(ERROR, "observers cannot resign"));
            } else {
                connections.setGameOver(command.getGameID());
                System.out.println("canMove switched due to resign");
                ServerMessage message = new ServerMessage(NOTIFICATION, connection.username + " resigned from the game.");
                connections.broadcast(command.getGameID(), "", message);
            }
        } catch (IOException e) {
            System.out.println("HIDDEN ERROR WAS SKIPPED BROTHER");
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
        try {
            try {
                Connection conn = connections.getConnection(command.getGameID(), command.getAuthToken());
                if (conn == null) {
                    throw new UnauthorizedException("Unauthorized");
                }
                if (conn.gameOver) {
                    throw new InvalidMoveException("The game is over. No more moves can be made");
                }
                GameData game = service.makeMove(command.getGameID(), command.getMove(), conn.role);
                String announcement = announcement(game);
                if (announcement == null) {
                    ServerMessage board = new ServerMessage(game.game());
                    ServerMessage notification = new ServerMessage(NOTIFICATION, conn.username + describeMove(command.getMove()));
                    connections.broadcast(command.getGameID(), "", board);
                    connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
                } else {
                    ServerMessage board = new ServerMessage(game.game());
                    ServerMessage notification = new ServerMessage(NOTIFICATION, conn.username
                            + describeMove(command.getMove()) + "\n" + announcement);
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

    private String convertPositionToString(ChessPosition position) {
        return COORDINATES.get(position.getColumn()) + position.getRow();
    }

    private static HashMap<Integer, String> generateCoordinateMap() {
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

    private boolean isCheck(GameData gameData, ChessGame.TeamColor color) {
        ChessGame game = gameData.game();
        return game.isInCheck(color);
    }

    private boolean isCheckmate(GameData gameData, ChessGame.TeamColor color) {
        ChessGame game = gameData.game();
        return game.isInCheckmate(color);
    }

    private boolean isStalemate(GameData gameData) {
        ChessGame game = gameData.game();
        return game.isInStalemate(ChessGame.TeamColor.BLACK);
    }

    private String announcement(GameData gameData) {
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (isCheckmate(gameData, ChessGame.TeamColor.WHITE)) {
            System.out.println("canMove switched due to white checkmate");
            connections.setGameOver(gameData.gameID());
            return white + "(white) in checkmate. " + black + " wins!";
        } else if (isCheckmate(gameData, ChessGame.TeamColor.BLACK)) {
            System.out.println("canMove switched due to black checkmate");
            connections.setGameOver(gameData.gameID());
            return black + "(white) in checkmate. " + white + " wins!";
        } else if (isStalemate(gameData)) {
            System.out.println("canMove switched due to stalemate");
            connections.setGameOver(gameData.gameID());
            return "The game is in stalemate";
        } else if (isCheck(gameData, ChessGame.TeamColor.WHITE)) {
            return white + " (white) in check";
        } else if (isCheck(gameData, ChessGame.TeamColor.BLACK)) {
            return black + "(black) in check";
        }
        return null;
    }
}
