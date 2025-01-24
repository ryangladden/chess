package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class PawnMoveCalculator implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        int direction = invert(board.getPiece(myPosition));
        if (isValidMove(board, myPosition, myPosition.getRow() + 1 * direction, myPosition.getColumn())) {
            ChessMove forward = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn()));
            if (!isCapture(board, forward)) {
                possibilities.add(forward);
                if (isValidMove(board, myPosition, myPosition.getRow() + 2 * direction, myPosition.getColumn())) {
                    ChessMove forward2 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn()));
                    if (isStarting(board.getPiece(myPosition), myPosition) && !isCapture(board, forward2)) {
                        possibilities.add(forward2);
                }
                }
            }
        }
//        ChessMove forward = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn()));
        if (isValidMove(board, myPosition, myPosition.getRow() + 1 * direction, myPosition.getColumn() + 1)) {
            ChessMove right = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn() + 1));
            if (isCapture(board, right)) {
                possibilities.add(right);
            }
        }
        if (isValidMove(board, myPosition, myPosition.getRow() + 1 * direction, myPosition.getColumn() - 1)) {
            ChessMove left = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn() - 1));
            if (isCapture(board, left)) {
                possibilities.add(left);
            }
        }

        ChessMove right2 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn() + 1));
        ChessMove left = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn() - 1));
        ChessMove left2 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn() - 1));
//        if (!isCapture(board, forward)) {
//            possibilities.add(forward);
//            if (isStarting(board.getPiece(myPosition), myPosition) && !isCapture(board, forward2)) {
//                possibilities.add(forward2);
//            }
//        }
        if (isCapture(board, left)) {
            possibilities.add(left);
        }
        if (isStarting(board.getPiece(myPosition), myPosition)) {

        }
        return possibilities;
    }
//    private boolean isBlocked(ChessBoard board, ChessPosition myPosition) {
//        ChessPiece piece = board.getPiece(myPosition);
//        int direction = invert(piece);
//        return (board.getPiece(new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn())) != null);
//    }

    private int invert(ChessPiece piece) {
        if (piece.getTeamColor() == BLACK) {
            return -1;
        }
        return 1;
    }

    private boolean isStarting(ChessPiece piece, ChessPosition myPosition) {
        if (piece.getTeamColor() == BLACK && myPosition.getRow() == 7) {return true;}
        else if (piece.getTeamColor() == WHITE && myPosition.getRow() == 2) {return true;}
        return false;
    }
}
