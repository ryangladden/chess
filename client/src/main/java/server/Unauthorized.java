package server;

public class Unauthorized extends ChessClientException {
    public Unauthorized(String message) {
        super(message);
    }
}
