package server.websocket;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDataAccess;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

    public Session session;
    public int gameID;
    public String authToken;
    public String username;
    public Role role;

    public Connection(String authToken, int gameID, Role role, Session session, DataAccess dataAccess) throws DataAccessException {
        this.authToken = authToken;
        this.gameID = gameID;
        this.role = role;
        this.session = session;
        this.username = dataAccess.authenticate(authToken).username();
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public enum Role {
        WHITE,
        BLACK,
        OBSERVER
    }
}
