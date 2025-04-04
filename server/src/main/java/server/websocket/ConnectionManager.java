package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
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

    public void remove(int gameID, String authToken) {
        connections.get(gameID).remove(authToken);
    }

    public Connection getConnection(int gameID, String authToken) {
        return connections.get(gameID).get(authToken);
    }

    public void broadcast(int gameID, String excludeAuth, ServerMessage message) throws IOException {
        var removeList = new ArrayList<String>();
        var game = connections.get(gameID);
        String msg = new Gson().toJson(message);
        System.out.println(msg);
        for (Map.Entry<String, Connection> entry : game.entrySet()) {
            String auth = entry.getKey();
            Connection connection = entry.getValue();
            if (connection.session.isOpen()) {
                if (!excludeAuth.equals(auth)) {
                    connection.send(msg);
                }
            } else {
                removeList.add(connection.authToken);
            }
        }
        for (String auth : removeList) {
            game.remove(auth);
        }
    }

    private Connection.Role getRole(String role) {
        if (Objects.equals(role, "observer")) {
            return OBSERVER;
        } else {
            return Objects.equals(role, "white") ? WHITE : BLACK;
        }
    }
}
