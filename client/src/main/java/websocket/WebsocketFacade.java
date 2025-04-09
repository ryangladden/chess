package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import static websocket.commands.UserGameCommand.CommandType.*;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebsocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    String authToken;
    int gameID;


    public WebsocketFacade(String url, NotificationHandler notificationHandler, String authToken, int gameID) throws RuntimeException {
        this.authToken = authToken;
        this.gameID = gameID;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });

        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void leave() {
        sendMessage(LEAVE, authToken, gameID);
    }

    public void resign() {
        sendMessage(RESIGN, authToken, gameID);
    }

    public void connect() {
        sendMessage(CONNECT, authToken, gameID);
    }

    public void makeMove(ChessMove move) {
        sendMessage(authToken, gameID, move);
    }

    private void sendMessage(UserGameCommand.CommandType commandType, String authToken, int gameID) {
        try {
            UserGameCommand command = new UserGameCommand(commandType, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(String authToken, int gameID, ChessMove move) {
        try {
            UserGameCommand command = new UserGameCommand(MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
