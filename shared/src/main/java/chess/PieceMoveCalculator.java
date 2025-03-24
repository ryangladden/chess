package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMoveCalculator {

    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    default boolean isValidMove(ChessBoard board, ChessPosition position, int row, int col) {
//        if ((newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8)) {
//            return false;
//        }
//        return ((board.getPiece(new ChessPosition(newRow, newColumn)) != null
//                && board.getPiece(myPosition).getTeamColor() != board.getPiece(new ChessPosition(newRow, newColumn)).getTeamColor())
//                && !(myPosition.getRow() == newRow && myPosition.getColumn() == newColumn));
//    }
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
//    default Collection<ChessMove> upDownMoves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> possibilities = new ArrayList<>();
//        boolean up = true;
//        boolean down = true;
//        boolean left = true;
//        boolean right = true;
//        int i = 1;
//        while (up) {
//            if (validateMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn())) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()));
//                possibilities.add(newMove);
//                i++;
//                if (isCapture(board, newMove)) {
//                    up = false;
//                }
//            } else {
//                up = false;
//                i = 1;
//            }
//        }
//        while (down) {
//            if (validateMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn())) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn()));
//                possibilities.add(newMove);
//                if (isCapture(board, newMove)) {
//                    down = false;
//                }
//                i++;
//            } else {
//                down = false;
//                i = 1;
//            }
//        }
//        while (left) {
//            if (validateMove(board, myPosition, myPosition.getRow(), myPosition.getColumn() + i)) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() + i));
//                possibilities.add(newMove);
//                i++;
//                if (isCapture(board, newMove)) {
//                    left = false;
//                }
//            } else {
//                left = false;
//                i = 1;
//            }
//        }
//        while (right) {
//            if (validateMove(board, myPosition, myPosition.getRow(), myPosition.getColumn() - i)) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow(), myPosition.getColumn() - i));
//                possibilities.add(newMove);
//                if (isCapture(board, newMove)) {
//                    right = false;
//                }
//                i++;
//            } else {
//                right = false;
//                i = 1;
//            }
//        }
//        return possibilities;
//    }
//
//    default Collection<ChessMove> diagMoves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> possibilities = new ArrayList<>();
//        boolean upLeft = true;
//        boolean downLeft = true;
//        boolean upRight = true;
//        boolean downRight = true;
//        int i = 1;
//        while (upLeft) {
//            if (validateMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn() - i)) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - i));
//                possibilities.add(newMove);
//                if (isCapture(board, newMove)) {
//                    upLeft = false;
//                }
//                i++;
//            } else {
//                upLeft = false;
//                i = 1;
//            }
//        }
//        while (downLeft) {
//            if (validateMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn() - i)) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
//                possibilities.add(newMove);
//                if (isCapture(board, newMove)) {
//                    downLeft = false;
//                }
//                i++;
//            } else {
//                downLeft = false;
//                i = 1;
//            }
//        }
//        while (upRight) {
//            if (validateMove(board, myPosition, myPosition.getRow() + i, myPosition.getColumn() + i)) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + i));
//                possibilities.add(newMove);
//                if (isCapture(board, newMove)) {
//                    upRight = false;
//                }
//                i++;
//            } else {
//                upRight = false;
//                i = 1;
//            }
//        }
//        while (downRight) {
//            if (validateMove(board, myPosition, myPosition.getRow() - i, myPosition.getColumn() - i)) {
//                ChessMove newMove = new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - i, myPosition.getColumn() - i));
//                possibilities.add(newMove);
//                if (isCapture(board, newMove)) {
//                    downRight = false;
//                }
//                i++;
//            } else {
//                downRight = false;
//                i = 1;
//            }
//        }
//        return possibilities;
//    }
//    default boolean isCapture(ChessBoard board, ChessMove move) {
//        return board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getStartPosition()).getTeamColor() != board.getPiece(move.getEndPosition()).getTeamColor();
//        }
}
