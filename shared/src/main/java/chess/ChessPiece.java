package chess;

import java.util.Collection;

import static chess.ChessGame.TeamColor.WHITE;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public int hashCode() {
        int piece;
        int color = 0;
        if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            color = 10;
        }
        switch (type) {
            case KING -> piece = 1;
            case QUEEN -> piece = 2;
            case PAWN -> piece = 3;
            case ROOK -> piece = 4;
            case BISHOP -> piece = 5;
            case KNIGHT -> piece = 6;
            case null -> piece = 0;
        }
        return piece + color;
    }

    @Override
    public boolean equals(Object obj) {
        ChessPiece other = (ChessPiece) obj;
        return (this.getTeamColor() == ((ChessPiece) obj).getTeamColor()
                && this.getPieceType() == other.getPieceType());
    }

    @Override
    public ChessPiece clone() {
        try {
            ChessPiece clone = (ChessPiece) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMoveCalculator calc = switch (type) {
            case KING -> new KingMoveCalculator();
            case KNIGHT -> new KnightMoveCalculator();
            case QUEEN -> new QueenMoveCalculator();
            case PAWN -> new PawnMoveCalculator();
            case BISHOP -> new BishopMoveCalculator();
            case ROOK -> new RookMoveCalculator();
        };
        return calc.pieceMoves(board, myPosition);
    }

    @Override
    public String toString() {
        String rep;
        switch (type) {
            case PAWN:
                return pieceColor == WHITE ? " ♙ " : " ♟ ";
            case ROOK:
                return pieceColor == WHITE ? " ♖ " : " ♜ ";
            case BISHOP:
                return pieceColor == WHITE ? " ♗ " : " ♝ ";
            case KNIGHT:
                return pieceColor == WHITE ? " ♘ " : " ♞ ";
            case KING:
                return pieceColor == WHITE ? " ♔ " : " ♚ ";
            case QUEEN:
                return pieceColor == WHITE ? " ♕ " : " ♛ ";
            case null:
                rep = ".";
                break;
        }
        if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            rep = rep.toUpperCase();
        }
        return rep;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


}
