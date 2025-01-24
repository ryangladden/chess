package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMoveCalculator implements PieceMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = upDownMoves(board, myPosition);
        diagMoves(board, myPosition).forEach(moves::add);
        return moves;
    }
}
