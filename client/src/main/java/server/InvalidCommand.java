package server;

public class InvalidCommand extends ChessClientException {
    public InvalidCommand(String message) {
        super(message);
    }
}
