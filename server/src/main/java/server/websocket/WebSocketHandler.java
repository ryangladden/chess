package server.websocket;

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

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onCommand(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println(command);
        switch (command.getCommandType()) {
            case LEAVE -> leaveGame(command);
            case RESIGN -> resign(command);
            case CONNECT -> connect(session, command);
            case MAKE_MOVE -> makeMove(command, message);
        }
    }

    private void leaveGame(UserGameCommand command) throws IOException {
        System.out.println("leave game called");
        String username = connections.getConnection(command.getGameID(), command.getAuthToken()).username;
        String message = username + " has left the game";
        ServerMessage notification = new ServerMessage(NOTIFICATION, message);
        connections.broadcast(command.getGameID(), command.getAuthToken(), notification);
    }

    private void resign(UserGameCommand command) {
        System.out.println("resign called");
    }

    private void connect(Session session, UserGameCommand command) throws DataAccessException, IOException {
        UserData user = dataAccess.authenticate(command.getAuthToken());
        GameData game = dataAccess.getGame(command.getGameID());

        Connection.Role role = getRole(game, user.username());
        connections.add(command.getAuthToken(), command.getGameID(), role, session, dataAccess);

        ServerMessage message = new ServerMessage(NOTIFICATION, gameToJson(game));
        connections.send(game.gameID(), command.getAuthToken(), message);

        message = new ServerMessage(NOTIFICATION, user.username() + " joined the game");
        connections.broadcast(game.gameID(), command.getAuthToken(), message);
    }

    private void makeMove(UserGameCommand command, String message) {
        System.out.println(command.getMove());
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
