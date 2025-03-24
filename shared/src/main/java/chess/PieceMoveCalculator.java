package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMoveCalculator {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean isValidMove(ChessBoard board, ChessPosition position, int row, int col) {
        if (row <= 8 && row >= 1 && col <= 8 && col >= 1) {
            return (board.getPiece(new ChessPosition(row, col)) == null
                    || board.getPiece(position).getTeamColor() != board.getPiece(new ChessPosition(row, col)).getTeamColor()
            );
        }
        return false;
    }

    default boolean isCapture(ChessBoard board, ChessMove move) {
        if (board.getPiece(move.getEndPosition()) != null) {
            return (board.getPiece(move.getEndPosition()).getTeamColor() != board.getPiece(move.getStartPosition()).getTeamColor());
        }
        return false;
    }

    default Collection<ChessMove> upDownMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn())) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn())) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow(), myPosition.getColumn() + i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow(), myPosition.getColumn() - i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        return possibilities;
    }

    default Collection<ChessMove> diagMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn() + i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn() + i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() + i));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn() - i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (isValidMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn() - i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
                possibilities.add(newMove);
                if (isCapture(board, newMove)) {
                    break;
                }
            } else {
                break;
            }
        }
        return possibilities;
    }
}
