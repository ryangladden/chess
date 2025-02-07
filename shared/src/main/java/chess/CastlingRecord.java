package chess;

public record CastlingRecord(ChessGame.TeamColor teamColor, boolean kingMoved, boolean rook1Moved) {
}
