package server.request;

public record LogoutRequest(String authToken) implements RecordRequest{
    @Override
    public LogoutRequest addAuth(String authToken) {
        return new LogoutRequest(authToken);
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
