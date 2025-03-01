package server.request;

public record CreateGameRequest(String authToken, String gameName) implements RecordRequest {

    @Override
    public CreateGameRequest addAuth(String authToken) {
        return new CreateGameRequest(authToken, this.gameName);
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }
}
