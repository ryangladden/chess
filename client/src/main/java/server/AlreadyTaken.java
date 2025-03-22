package server;

public class AlreadyTaken extends ChessClientException {
    public AlreadyTaken(String message) {
        super(message);
    }
}
