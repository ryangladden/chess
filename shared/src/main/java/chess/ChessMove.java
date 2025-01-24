package chess;

import java.util.ArrayList;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    @Override
    public int hashCode() {
        if (this.promotionPiece == null) {
            return startPosition.hashCode() * 31 + endPosition.hashCode() * 29;
        }
        return startPosition.hashCode()*31 + endPosition.hashCode() * 29 + promotionPiece.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
//        ChessMove other = (ChessMove) obj;
//        return other.getStartPosition() == this.getStartPosition() && other.getEndPosition() == this.getEndPosition();
        return this.hashCode() == obj.hashCode();
    }

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this(startPosition, endPosition, null);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString() {
        if (promotionPiece != null) {
            return new String(("{" + startPosition.getRow() + "," + startPosition.getColumn() + "} -> {" + endPosition.getRow() + "," + endPosition.getColumn() + "} " + promotionPiece + "\n"));
        }
        return new String(("{" + startPosition.getRow() + "," + startPosition.getColumn() + "} -> {" + endPosition.getRow() + "," + endPosition.getColumn() + "}\n"));
    }
}
