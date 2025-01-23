package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.WHITE;

public class PawnMoveCalculator implements PieceMoveCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> possibilities = new ArrayList<>();
//        int limit = 1;
//        if (myPosition.getRow() == 2) { limit += 1; }
//        for ()
//        if (myPosition.getRow() == 2 && validateMove(board, myPosition, myPosition.getRow() + 2, myPosition.getColumn()) && board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == ) {
//            possibilities.add()
//        }
        return new ArrayList<>();
    }
}
