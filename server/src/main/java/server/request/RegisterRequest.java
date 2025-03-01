package server.request;

public record RegisterRequest(String username, String password, String email) implements RecordRequest {
    @Override
    public RecordRequest addAuth(String authToken) {
        return null;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public boolean requiresAuth() {
        return false;
    }
}
