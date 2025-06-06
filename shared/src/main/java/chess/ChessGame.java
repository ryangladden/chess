package chess;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.KING;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (piece == null) {
            validMoves.add(null);
            return validMoves;
        }
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            var clone = this.clone();

            clone.board.addPiece(move.getEndPosition(), clone.board.getPiece(move.getStartPosition()));
            clone.board.addPiece(move.getStartPosition(), null);
            var color = piece.getTeamColor();
            if (!clone.isInCheck(color) && !clone.isInCheckmate(color) && !clone.isInStalemate(color)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var pos = move.getStartPosition();
        var piece = board.getPiece(pos);
        if (piece == null) {
            throw new InvalidMoveException("No piece at position " + move.getStartPosition());
        } else if (board.getPiece(pos).getTeamColor() != this.teamTurn) {
            throw new InvalidMoveException("Not your turn, fool! Your team: " + piece.getTeamColor() + " but it's " + this.teamTurn + "'s turn");
        } else if (!validMoves(pos).contains(move)) {
            throw new InvalidMoveException("Dawg this ain't gonna fly, you know that. Try a different move, son.");
        } else if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            board.addPiece(move.getStartPosition(), null);
        } else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            board.addPiece(move.getStartPosition(), null);
            teamTurn = (this.teamTurn == TeamColor.BLACK) ? (TeamColor.WHITE) : (TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> otherPositions =
                (teamColor == TeamColor.BLACK) ?
                        getPiecePositions(TeamColor.WHITE) :
                        getPiecePositions(TeamColor.BLACK);
        ChessPosition kingPos = getKing(teamColor);
        if (kingPos == null) {
            return false;
        }
        boolean check = false;
        for (ChessPosition pos : otherPositions) {
            for (ChessMove move : board.getPiece(pos).pieceMoves(board, pos)) {
                if (move.getEndPosition().hashCode() == kingPos.hashCode()) {
                    check = true;

                }
            }
        }

        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean checkmate = false;
        if (isInCheck(teamColor)) {
            checkmate = noMoves(teamColor);
        }
        return checkmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean stalemate = false;
        if (!isInCheck(teamColor)) {
            stalemate = noMoves(teamColor);
        }
        return stalemate;
    }

    private boolean noMoves(TeamColor teamColor) {
        boolean noMoves = true;
        for (var pos : getPiecePositions(teamColor)) {
            for (var move : board.getPiece(pos).pieceMoves(board, pos)) {
                ChessGame clone = this.clone();
                clone.board.addPiece(move.getEndPosition(), clone.board.getPiece(move.getStartPosition()));
                clone.board.addPiece(move.getStartPosition(), null);
                if (!clone.isInCheck(teamColor)) {
                    noMoves = false;
                    break;
                }
            }
            if (!noMoves) {
                break;
            }
        }
        return noMoves;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public Collection<ChessPosition> getPiecePositions() {
        Collection<ChessPosition> pieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (board.getPiece(i, j) != null) {
                    pieces.add(new ChessPosition(i, j));
                }
            }
        }
        return pieces;
    }

    public Collection<ChessPosition> getPiecePositions(TeamColor teamColor) {
        Collection<ChessPosition> pieces = getPiecePositions();
        Collection<ChessPosition> teamPieces = new ArrayList<>();
        for (ChessPosition pos : pieces) {
            if (board.getPiece(pos).getTeamColor() == teamColor) {
                teamPieces.add(pos);
            }
        }
        return teamPieces;
    }

    public ChessPosition getKing(TeamColor teamColor) {
        return getPiece(teamColor, KING).getFirst();
    }

    public ArrayList<ChessPosition> getPiece(TeamColor teamColor, ChessPiece.PieceType type) {
        Collection<ChessPosition> pieces = getPiecePositions();
        ArrayList<ChessPosition> piecesFound = new ArrayList<>();
        for (ChessPosition pos : pieces) {
            if (board.getPiece(pos).getPieceType() == type
                    && board.getPiece(pos).getTeamColor() == teamColor) {
                piecesFound.add(pos);
            }
        }
        if (piecesFound.isEmpty()) {
            piecesFound.add(null);
        }
        return piecesFound;
    }

    @Override
    public ChessGame clone() {
        try {
            ChessGame clone = (ChessGame) super.clone();
            if (this.teamTurn == TeamColor.BLACK) {
                clone.teamTurn = TeamColor.BLACK;
            } else {
                clone.teamTurn = TeamColor.WHITE;
            }
            clone.board = this.board.clone();
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

    public String toJson() {
        String json = new Gson().toJson(this);
        return json;
    }

    public static ChessGame fromJson(String json) {
        return new Gson().fromJson(json, ChessGame.class);
    }
}
