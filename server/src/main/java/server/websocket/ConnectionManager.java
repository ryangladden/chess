package server.websocket;

import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static server.websocket.Connection.Role.*;

public class ConnectionManager {

    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID, String role, Session session) throws DataAccessException {
        Connection.Role teamColor = getRole(role);
        Connection connection = new Connection(authToken, gameID, teamColor, session);
        if (connections.containsKey(gameID)) {
            connections.get(gameID).put(authToken, connection);
        } else {
            ConcurrentHashMap<String, Connection> connData = new ConcurrentHashMap<>();
            connData.put(authToken, connection);
            connections.put(gameID, connData);
        }
    }

//    public void remove(String authToken) {
//        connections.remove(authToken);
//    }

    public Connection getConnection(int gameID, String authToken) {
        return connections.get(gameID).get(authToken);
    }

    private Connection.Role getRole(String role) {
        if (Objects.equals(role, "observer")) {
            return OBSERVER;
        } else {
            return Objects.equals(role, "white") ? WHITE : BLACK;
        }
    }
}
