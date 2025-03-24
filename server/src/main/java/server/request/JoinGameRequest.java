package server.request;

public record JoinGameRequest(String playerColor, int gameID, String authToken) implements RecordRequest {

    @Override
    public JoinGameRequest addAuth(String authToken) {
        return new JoinGameRequest(this.playerColor, this.gameID, authToken);
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
