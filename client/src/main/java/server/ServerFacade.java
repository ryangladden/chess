package server;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.net.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;


    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData user) {

    }

    public AuthData login(UserData user) {

    }

    public void logout(String authToken) {

    }

    public int createGame(String gameName, String authToken) {

    }

    public GameData[] listGames(String authToken) {

    }

    public void joinGame(int gameId, String color, String authToken) {

    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> response) {
        makeRequest(method, path, request, response, null);
    }
}
