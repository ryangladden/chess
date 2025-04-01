package chess;

public class Main {
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        String json = game.toJson();
    }
}