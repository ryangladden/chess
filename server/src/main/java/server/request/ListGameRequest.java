package server.request;

public record ListGameRequest(String authToken) implements RecordRequest {
    public ListGameRequest addAuth(String authToken) {
        return new ListGameRequest(authToken);
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
