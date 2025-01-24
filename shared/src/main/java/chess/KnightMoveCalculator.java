package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements PieceMoveCalculator {


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibilities = new ArrayList<ChessMove>();
        if (isValidMove(board, myPosition, myPosition.getRow() + 2, myPosition.getColumn() + 1)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() + 2, myPosition.getColumn() - 1)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() - 2, myPosition.getColumn() + 1)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() - 2, myPosition.getColumn() - 1)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() + 1, myPosition.getColumn() + 2)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() + 1, myPosition.getColumn() - 2)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() - 1, myPosition.getColumn() + 2)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2)));
        }
        if (isValidMove(board, myPosition, myPosition.getRow() - 1, myPosition.getColumn() - 2)) {
            possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2)));
        }
        return possibilities;
    }
}
