package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements PieceMoveCalculator {
    @Override
    public int hashCode() {
        return 31 * type.hashCode() * pieceColor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ChessPiece other = (ChessPiece) obj;
        return (other.type == this.type && other.pieceColor == this.pieceColor);
    }

    //    private
    private ChessGame.TeamColor pieceColor;
    private PieceType type;
    private PieceMoveCalculator moveCalculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMoveCalculator moves = switch(getPieceType()) {
            case KING -> new KingMoveCalculator();
            case QUEEN -> new QueenMoveCalculator();
            case BISHOP -> new BishopMoveCalculator();
            case KNIGHT -> new KnightMoveCalculator();
            case ROOK -> new RookMoveCalculator();
            case PAWN -> new PawnMoveCalculator();
        };
        return moves.pieceMoves(board,myPosition);
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
