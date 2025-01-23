package chess;

import java.util.Collection;
import java.util.List;

public class RookMoveCalculator implements PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return upDownMoves(board, myPosition);
    }
}