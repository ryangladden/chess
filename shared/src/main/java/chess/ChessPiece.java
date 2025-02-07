package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable{

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
        switch(type) {
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
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
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
        PieceMoveCalculator calc = switch(type) {
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
                rep = "p";
                break;
            case ROOK:
                rep = "r";
                break;
            case BISHOP:
                rep = "b";
                break;
            case KNIGHT:
                rep = "n";
                break;
            case KING:
                rep = "k";
                break;
            case QUEEN:
                rep = "q";
                break;
            case null:
                rep = ".";
                break;
        }
        if (this.pieceColor == ChessGame.TeamColor.BLACK) {
            rep = rep.toUpperCase();
        }
        return rep;
    }


}
