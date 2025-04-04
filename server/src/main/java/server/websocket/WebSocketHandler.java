package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

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
        connections.broadcast(command.getGameID(), command.getAuthToken(), new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION));
    }

    private void resign(UserGameCommand command) {
        System.out.println("resign called");
    }

    private void connect(Session session, UserGameCommand command) throws DataAccessException {
        connections.add(command.getAuthToken(), command.getGameID(), "observer", session);
    }

    private void makeMove(UserGameCommand command, String message) {
        System.out.println("make move called");
    }
}
