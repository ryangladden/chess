package websocket;

import websocket.messages.ServerMessage;

public interface NotificationHandler {

    public abstract void notify(ServerMessage message);
}
