package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

import static server.websocket.Connection.Role.*;

public class ConnectionManager {

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID, String role, Session session) {
        Connection.Role teamColor = role == "white" ? WHITE : BLACK;
        Connection connection = new Connection(authToken, gameID, )
    }
}
