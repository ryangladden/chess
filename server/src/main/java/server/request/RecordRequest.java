package server.request;

public interface RecordRequest {

    RecordRequest addAuth(String authToken);

    boolean requiresAuth();
}
