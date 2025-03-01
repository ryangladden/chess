package server.request;

public record ClearRequest() implements RecordRequest {
    @Override
    public RecordRequest addAuth(String authToken) {
        return this;
    }

    @Override
    public boolean requiresAuth() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return false;
    }
}
