package chess;

import java.util.Collection;

public class RookMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return upDownMoves(board, myPosition);
//        return new ArrayList<>();
    }
}
