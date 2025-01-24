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
                if (isPromoted(forward)) {
                    possibilities.add(new ChessMove(forward.getStartPosition(), forward.getEndPosition(), ChessPiece.PieceType.BISHOP));
                    possibilities.add(new ChessMove(forward.getStartPosition(), forward.getEndPosition(), ChessPiece.PieceType.QUEEN));
                    possibilities.add(new ChessMove(forward.getStartPosition(), forward.getEndPosition(), ChessPiece.PieceType.ROOK));
                    possibilities.add(new ChessMove(forward.getStartPosition(), forward.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                } else {
                    possibilities.add(forward);
                }
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
                if (isPromoted(right)) {
                    possibilities.add(new ChessMove(right.getStartPosition(), right.getEndPosition(), ChessPiece.PieceType.BISHOP));
                    possibilities.add(new ChessMove(right.getStartPosition(), right.getEndPosition(), ChessPiece.PieceType.QUEEN));
                    possibilities.add(new ChessMove(right.getStartPosition(), right.getEndPosition(), ChessPiece.PieceType.ROOK));
                    possibilities.add(new ChessMove(right.getStartPosition(), right.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                } else {
                    possibilities.add(right);
                }
                if (isValidMove(board, myPosition, myPosition.getRow() + 2 * direction, myPosition.getColumn() + 1)) {
                    ChessMove right2 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn() + 1));
                    if (isCapture(board, right2)) {
                        possibilities.add(right2);
                    }
                }
            }
        }
        if (isValidMove(board, myPosition, myPosition.getRow() + 1 * direction, myPosition.getColumn() - 1)) {
            ChessMove left = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1 * direction, myPosition.getColumn() - 1));
            if (isCapture(board, left)) {
                if (isPromoted(left)) {
                    possibilities.add(new ChessMove(left.getStartPosition(), left.getEndPosition(), ChessPiece.PieceType.BISHOP));
                    possibilities.add(new ChessMove(left.getStartPosition(), left.getEndPosition(), ChessPiece.PieceType.QUEEN));
                    possibilities.add(new ChessMove(left.getStartPosition(), left.getEndPosition(), ChessPiece.PieceType.ROOK));
                    possibilities.add(new ChessMove(left.getStartPosition(), left.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                } else {
                    possibilities.add(left);
                }
                if (isValidMove(board, myPosition,myPosition.getRow() + 2 * direction, myPosition.getColumn() - 1)) {
                    ChessMove left2 = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn() - 1));
                    if (isCapture(board, left2)) {
                        possibilities.add(left2);
                    }
                }
            }
        }
        return possibilities;
    }

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

    private boolean isPromoted(ChessMove move) {
        return (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8);
    }
}
