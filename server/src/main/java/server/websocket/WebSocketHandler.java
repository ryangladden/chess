package server.websocket;

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
import java.util.HashMap;

import static server.websocket.Connection.Role.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {

    private final HashMap<Integer, String> COORDINATES = generateCoordinateMap();
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
//        String message = connection.username + " has left the game";
//        service.leave(command.getGameID(), role);
//        connections.remove(command.getGameID(), command.getAuthToken());
//        ServerMessage notification = new ServerMessage(NOTIFICATION, message);
//        connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
    }

    private void resign(UserGameCommand command) {
        System.out.println("resign called");
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

            message = new ServerMessage(NOTIFICATION, user.username() + " joined the game");
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
                System.out.println("connection is good");
                GameData game = service.makeMove(command.getGameID(), command.getMove(), conn.role);
                ServerMessage board = new ServerMessage(game.game());
                ServerMessage notification = new ServerMessage(NOTIFICATION, conn.username + describeMove(command.getMove()));
                connections.broadcast(command.getGameID(), "", board);
                connections.broadcast(command.getGameID(), command.getAuthToken(), notification);

            } catch (InvalidMoveException e) {
                ServerMessage error = new ServerMessage(ERROR, "Invalid move");
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
}
