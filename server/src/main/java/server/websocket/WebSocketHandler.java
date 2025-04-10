package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDataAccess;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.io.IOException;

import static server.websocket.Connection.Role.*;
import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
public class WebSocketHandler {

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
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println(command);
        switch (command.getCommandType()) {
            case LEAVE -> leaveGame(command);
            case RESIGN -> resign(command);
            case CONNECT -> connect(session, command);
            case MAKE_MOVE -> makeMove(command);
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
            System.out.println(command.getGameID());
            UserData user = dataAccess.authenticate(command.getAuthToken());
            System.out.println(user);
            GameData game = dataAccess.getGame(command.getGameID());

            Connection.Role role = getRole(game, user.username());
            connections.add(command.getAuthToken(), command.getGameID(), role, session, dataAccess);

            ServerMessage message = new ServerMessage(game.game());
            connections.send(game.gameID(), command.getAuthToken(), message);

            message = new ServerMessage(NOTIFICATION, user.username() + " joined the game");
            connections.broadcast(game.gameID(), command.getAuthToken(), message);
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

    private void makeMove(UserGameCommand command) {
        System.out.println(command.getMove());
        try {
            try {
                GameData game = service.makeMove(command.getGameID(), command.getMove());
                ServerMessage message = new ServerMessage(game.game());
                connections.broadcast(command.getGameID(), "", message);

            } catch (InvalidMoveException e) {
                ServerMessage error = new ServerMessage(ERROR, "Invalid move");
                connections.send(command.getGameID(), command.getAuthToken(), error);
            }
        } catch (IOException ignored) {
        }
        System.out.println("make move called");

    }

    private Connection.Role getRole(GameData game, String username) {
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (!username.equals(white) && !username.equals(black)) {
            return OBSERVER;
        }
        return username.equals(white) ? WHITE : BLACK;
    }

    private String gameToJson(GameData game) {
        return new Gson().toJson(game);
    }
}
