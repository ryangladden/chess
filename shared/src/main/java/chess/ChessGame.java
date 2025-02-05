package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable{

    private ChessBoard board;
    private TeamColor turnColor;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.turnColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnColor = team;
    }

    @Override
    public ChessGame clone() {
        try {
            ChessGame clone = (ChessGame) super.clone();
            if (this.turnColor == TeamColor.BLACK) {
                clone.turnColor = TeamColor.BLACK;
            }
            else { clone.turnColor = TeamColor.WHITE; }
            clone.board = this.board.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) { return Collections.emptyList(); }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ChessGame clone = this.clone();
            ArrayList<ChessMove> valid = new ArrayList<>();
            for (ChessMove move : piece.pieceMoves(clone.getBoard(), startPosition)) {
                clone.board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.KING));
                clone.board.addPiece(move.getStartPosition(), null);
                if (clone.isInCheck(piece.getTeamColor())) {
                    valid.add(move);
                }
            }
            return valid;
        }
            return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null
                || !validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("You can't make that move, silly");
        }
        changeTeams();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = getKing(teamColor);
        Collection<ChessPosition> otherPositions = (teamColor == TeamColor.BLACK) ? getPiecePositions(TeamColor.WHITE) : getPiecePositions(TeamColor.BLACK);
        for (var pos : otherPositions) {
            for (var move : (board.getPiece(pos).pieceMoves(board, pos))) {
                if (move.getEndPosition() == king) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        return (isInCheck(teamColor) && board.getPiece(getKing(teamColor)).pieceMoves(board, getKing(teamColor)).isEmpty());
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
//        this.board = board;
                board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Collection<ChessPosition> getPiecePositions() {
        Collection<ChessPosition> pieces = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if (board.getPiece(i, j) != null) {
                    pieces.add(new ChessPosition(i, j));
                }
            }
        }
        return pieces;
    }

    private Collection<ChessPosition> getPiecePositions(TeamColor teamColor) {
        Collection<ChessPosition> pieces = getPiecePositions();
        for (ChessPosition pos : pieces) {
            if (board.getPiece(pos).getTeamColor() != teamColor) {
                pieces.remove(pos);
            }
        }
        return pieces;
    }


    private ChessPosition getKing(TeamColor teamColor) {
        Collection<ChessPosition> pieces = getPiecePositions();
        for (ChessPosition pos : pieces) {
            if (board.getPiece(pos).getPieceType() == ChessPiece.PieceType.KING
                && board.getPiece(pos).getTeamColor() == teamColor) {
                return pos;
            }
        }
        return null;
    }

    private void changeTeams() {
        if (turnColor == TeamColor.BLACK) { turnColor = TeamColor.WHITE; }
        else { turnColor = TeamColor.BLACK; }
    }
    private TeamColor getTurnColor() {
        return this.turnColor;
    }
}
