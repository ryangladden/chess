package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator extends PieceMoveCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        for (int i = position.getRow() - 1; i <= position.getRow() + 1; i++) {
            for (int j = position.getColumn() - 1; j <= position.getColumn() + 1; j++ ) {
                if (i >= 0 && i < 8 && j >= 0 && j< 8) {
                    possibilities.add(new ChessMove(position, new ChessPosition(i, j)));
                }
            }
        }
        return possibilities;
    }
}

//    @Override
//    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
//        Collection<ChessMove> possibilities = new ArrayList<>();
//        for (int i = position.getRow() - 1; i <= position.getRow() + 1; i++) {
//            for (int j = position.getColumn() - 1; j <= position.getColumn() + 1; j++ ) {
//                if (i >= 0 && i < 8 && j >= 0 && j< 8) {
//                    possibilities.add(new ChessMove(position, new ChessPosition(i, j)));
//                }
//            }
//        }
//        return possibilities;
//    }
//}
