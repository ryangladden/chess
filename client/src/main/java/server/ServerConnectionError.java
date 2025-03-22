package server;

public class ServerConnectionError extends ChessClientException {
    public ServerConnectionError(String message) {
        super(message);
    }
}
