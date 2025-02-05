package chess;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public ChessPiece getPiece(int row, int col) {
        return squares[row][col];
    }

    @Override
    public int hashCode() {
//        int hash = 1;
//        for (int i = 0; i <=7; i++) {
//            for (int j = 0; j <=7; j++) {
//                if (squares[i][j] == null) {
//                    hash *= (i + 1) * (j + 1);
//                }
//                else {
//                    hash *= squares[i][j].hashCode() * ((i + 1) * (j + 1));
//                }
//                }
//            }
//        return hash;
        return Arrays.deepHashCode(squares);
    }

    @Override
    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        ChessBoard other = (ChessBoard) obj;
//        for (int i = 0; i <=7; i++) {
//            for (int j = 0; j <=7; j++) {
//                if (!(this.squares[i][j] == other.squares[i][j])) {
//                    return false;
//                }
//            }
//        }
//        return true;
        return this.hashCode() == obj.hashCode();
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 2; i <= 6; i++) {
            for (int j = 1; j <= 8; j++) {
                addPiece(new ChessPosition(i, j), null);
            }
        }
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));

        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
    }

    public String toString() {
        StringBuilder rep = new StringBuilder();
        for (int i = 0; i <= 7; i++) {
            rep.append(" ");
            for (int j = 0; j <= 7; j++) {
                rep.append(squares[i][j] + " ");
            }
            rep.append("\n");
        }
        return rep.toString();
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessPiece[][] squares = new ChessPiece[8][8];
            ChessBoard clone = (ChessBoard) super.clone();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    squares[i][j] = clone.getPiece(i, j);
                }
            }
            clone.squares = squares;
            // TODO: copy mutable state here, so the clone can't change the internals of the original
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return null;
    }
}
