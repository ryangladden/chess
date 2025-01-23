package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMoveCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean validateMove(ChessBoard board, ChessPosition myPosition, int newRow, int newColumn) {
        return ((newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8)
                && (board.getPiece(new ChessPosition(newRow, newColumn)) == null || board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(newRow, newColumn)).getTeamColor())
                && !(myPosition.getRow() == newRow && myPosition.getColumn() == newColumn));
    }

    default Collection<ChessMove> upDownMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        boolean up = true;
        boolean down = true;
        boolean left = true;
        boolean right = true;
        int i = 1;
        while (up) {
            if (validateMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn())) {
                possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn())));
                i++;
            } else {
                up = false;
                i = 1;
            }
        }
        while (down) {
            if (validateMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn())) {
                possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn())));
                i++;
            } else {
                down = false;
                i = 1;
            }
        }
        while (left) {
            if (validateMove(board, myPosition, myPosition.getRow(), myPosition.getColumn() + i)) {
                possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i)));
                i++;
            } else {
                left = false;
                i = 1;
            }
        }
        while (right) {
            if (validateMove(board, myPosition, myPosition.getRow(), myPosition.getColumn() - i)) {
                possibilities.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i)));
                i++;
            } else {
                right = false;
                i = 1;
            }
        }
        return possibilities;
    }

    default Collection<ChessMove> diagMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibilities = new ArrayList<>();
        boolean upLeft = true;
        boolean downLeft = true;
        boolean upRight = true;
        boolean downRight = true;
        int i = 1;
        while (upLeft) {
            if (validateMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn() - i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i));
                possibilities.add(newMove);
                if (captureMove(board, newMove)) {
                    upLeft = false;
                }
                i++;
            } else {
                upLeft = false;
                i = 1;
            }
        }
        while (downLeft) {
            if (validateMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn() - i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
                possibilities.add(newMove);
                if (captureMove(board, newMove)) {
                    downLeft = false;
                }
                i++;
            } else {
                downLeft = false;
                i = 1;
            }
        }
        while (upRight) {
            if (validateMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn() + i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i));
                possibilities.add(newMove);
                if (captureMove(board, newMove)) {
                    upRight = false;
                }
                i++;
            } else {
                upRight = false;
                i = 1;
            }
        }
        while (downRight) {
            if (validateMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn() - i)) {
                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
                possibilities.add(newMove);
                if (captureMove(board, newMove)) {
                    downRight = false;
                }
                i++;
            } else {
                downRight = false;
                i = 1;
            }
        }
        return possibilities;
    }
    default boolean captureMove(ChessBoard board, ChessMove move) {
        return board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getStartPosition()).getTeamColor() != board.getPiece(move.getEndPosition()).getTeamColor();
        }
    }
