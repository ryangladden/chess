package chess;

public class Main {
    public static void main(String[] args) {
        chess.ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.print(board);

        ChessPiece piece = board.getPiece(0, 4);
        System.out.print(piece);

    }
}