package chess;

import java.util.Collection;

public abstract class PieceMoveCalculator {

    public abstract Collection<ChessMove> pieceMove(ChessBoard board,ChessPosition position);
}
