package server.request;

public record LoginRequest(String username, String password) implements RecordRequest {
    @Override
    public RecordRequest addAuth(String authToken) {
        return null;
    }

    @Override
    public boolean requiresAuth() {
        return false;
    }

}
