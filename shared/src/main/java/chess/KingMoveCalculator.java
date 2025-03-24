package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements PieceMoveCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        for (int i = position.getRow() - 1; i <= position.getRow() + 1; i++) {
            for (int j = position.getColumn() - 1; j <= position.getColumn() + 1; j++) {
                if (isValidMove(board, position, i, j)) {
                    ChessMove newMove = new ChessMove(position, new ChessPosition(i, j));
                    if (isCapture(board, newMove)) {
                        System.out.printf("%s, %s\n", newMove.getStartPosition(), newMove.getEndPosition());
                    }
                    possibilities.add(newMove);
                }
            }
        }
        return possibilities;
    }
}
