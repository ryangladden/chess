package server.request;

public record CreateGameRequest(String authToken, String gameName) {
}
