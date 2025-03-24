package chess;

import java.util.Collection;

public class BishopMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return diagMoves(board, myPosition);
//        return new ArrayList<>();
    }
}
