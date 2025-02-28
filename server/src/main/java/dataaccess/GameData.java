package dataaccess;

import chess.ChessGame;

public record GameData(String gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
