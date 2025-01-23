package chess;

import java.util.Collection;

public interface PieceMoveCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean validateMove(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if (board.getPiece(myPosition) == null) { return true; }
        return (board.getPiece(myPosition).getPieceType() != board.getPiece(newPosition).getPieceType()
                || !(newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() >= 1 && newPosition.getColumn() <= 8)
                || !(myPosition.getRow() == newPosition.getRow() && myPosition.getColumn() == newPosition.getColumn()));
    }
}
