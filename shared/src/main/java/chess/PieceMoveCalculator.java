package chess;

import java.util.Collection;

public interface PieceMoveCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean validateMove(ChessBoard board, ChessPosition myPosition, int newRow, int newColumn) {
        return ((newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8)
                && (board.getPiece(new ChessPosition(newRow, newColumn)) == null || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(newRow, newColumn)).getTeamColor())
                && !(myPosition.getRow() == newRow && myPosition.getColumn() == newColumn));
    }
}
