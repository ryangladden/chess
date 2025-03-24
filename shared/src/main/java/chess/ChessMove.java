package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    @Override
    public boolean equals(Object obj) {
        ChessMove other = (ChessMove) obj;
//        return (other.getStartPosition() == this.getStartPosition()
//                && other.getEndPosition() == this.getEndPosition()
//                && this.getPromotionPiece() == other.getPromotionPiece()
//        );
        if (other.getPromotionPiece() == null && this.getPromotionPiece() == null) {
            return other.getStartPosition().hashCode() == this.getStartPosition().hashCode()
                    && this.getEndPosition().hashCode() == other.getEndPosition().hashCode();
        } else if (other.getPromotionPiece() == null && this.getPromotionPiece() != null
                || other.getPromotionPiece() != null && this.getPromotionPiece() == null) {
            return false;
        }
        return (other.getStartPosition().hashCode() == this.getStartPosition().hashCode()
                && this.getEndPosition().hashCode() == other.getEndPosition().hashCode()
                && this.getPromotionPiece().hashCode() == other.getPromotionPiece().hashCode());
    }

    @Override
    public int hashCode() {
        if (promotionPiece != null) {
            return endPosition.hashCode() * 100 + startPosition.hashCode() + promotionPiece.hashCode() * 100000;
        }
        return endPosition.hashCode() * 100 + startPosition.hashCode();
    }

    @Override
    public String toString() {
        return getStartPosition() + "->" + getEndPosition() + "\n";
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }
}
