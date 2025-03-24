package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;


    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public AuthData register(UserData user) throws AlreadyTaken{
        return makeRequest("POST", "/user", user, AuthData.class);
    }

    public AuthData login(UserData user) throws Unauthorized{
        return makeRequest("POST", "/session", user, AuthData.class);
    }

    public void logout(String authToken) throws Unauthorized{
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    public void clear() {
        makeRequest("DELETE", "/db", null, null);
    }

    public int createGame(String gameName, String authToken) throws Unauthorized{
        record CreateGameRequest(String gameName){};
        record CreateGameResponse(int gameID){};
        CreateGameResponse response = makeRequest("POST", "/game", new CreateGameRequest(gameName), CreateGameResponse.class, authToken);
        return response.gameID();
    }

    public GameData[] listGames(String authToken) throws Unauthorized{
        record Games(GameData[] games) {};
        Games games = makeRequest("GET", "/game", null, Games.class, authToken);
        return games.games();
    }

    public void joinGame(int gameId, String color, String authToken) throws AlreadyTaken, Unauthorized, InvalidCommand {
        record JoinGame(int gameID, String playerColor){};
        makeRequest("PUT", "/game", new JoinGame(gameId, color), null, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ChessClientException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }
            writeBody(request, http);
            http.connect();
            catchErrors(http);
            return readBody(http, responseClass);
        } catch (ProtocolException | MalformedURLException | URISyntaxException | ConnectException e) {
            throw new ServerConnectionError("Error connecting to the server");
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> response) {
        return makeRequest(method, path, request, response, null);
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String json = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(json.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void catchErrors(HttpURLConnection http) throws IOException, ChessClientException {
        int code = http.getResponseCode();
        if (code == 500) {
            throw new ServerConnectionError("Error: internal server error");
        }
        else if (code == 400) {
            throw new InvalidCommand("Invalid command");
        }
        else if (code == 401) {
            throw new Unauthorized("Invalid credentials");
        }
        else if (code == 403) {
            throw new AlreadyTaken("Unique resource already taken");
        }
    }
}