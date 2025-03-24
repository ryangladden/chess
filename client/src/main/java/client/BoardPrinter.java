package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    private static final String[] COL_HEADERS = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
    private static final String[] ODD_ROWS = {SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK, SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE,
            SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK, SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE, SET_BG_COLOR_WHITE +
            SET_TEXT_COLOR_BLACK, SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE, SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK,
            SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE};
    private static final String[] EVEN_ROWS = {SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE, SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK,
            SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE, SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK, SET_BG_COLOR_BLACK +
            SET_TEXT_COLOR_WHITE, SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK, SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE,
            SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK};

    public static String printBoard(ChessGame game, String teamColor) {
        return teamColor.equals("black") ? printBlack(game) : printWhite(game);
    }

    private static String printWhite(ChessGame game) {
        StringBuilder string = new StringBuilder(printHeaders("black"));
        ChessBoard board = game.getBoard();
        for (int i = 7; i >= 0; i--) {
            string.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " ").append(i + 1).append(" ");
            for (int j = 7; j >= 0; j--) {
                string.append(printGrid(board, i, j));
            }
            string.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " ").append(i + 1).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        string.append(printHeaders("black"));
        return string.toString();
    }

    private static String printBlack(ChessGame game) {
        StringBuilder string = new StringBuilder(printHeaders("white"));
        ChessBoard board = game.getBoard();
        for (int i = 0; i <= 7; i++) {
            string.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " ").append(i + 1).append(" ");
            for (int j = 0; j <= 7; j++) {
                string.append(printGrid(board, i, j));
            }
            string.append(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " ").append(i + 1).append(" ").append(RESET_BG_COLOR).append("\n");
        }
        string.append(printHeaders("white"));
        return string.toString();
    }

    private static String printHeaders(String teamColor) {
        StringBuilder string = new StringBuilder(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + "   ");
        switch (teamColor) {
            case "white":
                for (int i = 7; i >= 0; i--) {
                    string.append(COL_HEADERS[i]);
                }
                break;
            case "black":
                for (int i = 0; i <= 7; i++) {
                    string.append(COL_HEADERS[i]);
                }
        }
        string.append("   " + RESET_BG_COLOR + "\n");
        return string.toString();
    }

    private static String printGrid(ChessBoard board, int i, int j) {
        StringBuilder string = new StringBuilder();
        ChessPiece piece = board.getPiece(i + 1, 7 - j + 1);
        if (i % 2 == 1) {
            string.append(ODD_ROWS[7 - j]).append(piece == null ? EMPTY : piece);
        } else {
            string.append(EVEN_ROWS[7 - j]).append(piece == null ? EMPTY : piece);
        }
        return string.toString();
    }
}
