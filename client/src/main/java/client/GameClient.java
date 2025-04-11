package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import server.InvalidCommand;
import websocket.WebsocketFacade;

import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.*;

public class GameClient {

    private final WebsocketFacade websocket;
    private ChessGame game = null;
    private final String color;
    private final static Map<String, Integer> COORDINATES = generateCoordinateMap();

    public GameClient(WebsocketFacade websocket, String authToken, int gameID, String color) {
        this.websocket = websocket;
        this.color = color;
        System.out.println(color);
    }

    public String eval(String input) {
        String[] command = input.split(" ");
        return switch(command[0]) {
            case "leave" -> leave();
            case "resign" -> resign();
            case "move" -> move(command);
//            case "help" -> "help";
            case "show" -> show(command);
            case "board" -> printBoard();
            default -> help();
        };
    }

    private String leave() {
        websocket.leave();
        return "quit";
    }

    private String resign() {
        if (this.color.equals("observer")) {
            return helpObserver();
        }
        websocket.resign();
        return "Resigning from game";
    }

    private String move(String[] command) {
        if (this.color.equals("observer")) {
            return helpObserver();
        }
        try {
            if (command.length >= 3) {
                if (turnColor(game).equals(color)) {
                    ChessMove move = parseMoves(command[1], command[2]);
                    ChessGame copy = game.clone();
                    copy.makeMove(move);
                    websocket.makeMove(move);
                    return "made move";
                } else {
                    return "It's not your turn";
                }
            }
            return SET_TEXT_COLOR_RED + "Not enough arguments for a move command" + RESET_TEXT_COLOR;
        } catch (InvalidMoveException e) {
            return SET_TEXT_COLOR_RED + "That move is invalid" + RESET_TEXT_COLOR + "\n";
        } catch (InvalidCommand e) {
            return SET_TEXT_COLOR_RED + "Moves must be specified using two letter-number coordinates (e.g. d3 d4)" + RESET_TEXT_COLOR + "\n";
        }

    }

    private String show(String[] command) {
        try {
            if (command.length >= 2) {
                ChessPosition position = parsePosition(command[1]);
                return BoardPrinter.printValidMoves(game, color == "observer" ? "white" : color, position);
            }
            return SET_TEXT_COLOR_RED + "Not enough arguments for a show command" + RESET_TEXT_COLOR;
        } catch (InvalidCommand e) {
            return SET_TEXT_COLOR_RED + "Specify position using the letter and number coordinate found on the board (e.g. h2)"
                    + RESET_TEXT_COLOR + "\n";
        }
    }

    private String printBoard() {
        return getBoard();
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public String help() {
        return SET_TEXT_BOLD + "OPTIONS:\n" +
                "   move <start position> <end position>        " + RESET_TEXT_BOLD_FAINT
                + "Move piece at start position to end position (positions are specified in the following notation: a4, g7, etc.)\n" +
                SET_TEXT_BOLD +
                "   show <position>                             " + RESET_TEXT_BOLD_FAINT
                + "Show all possible moves for piece at specified position\n" +
                SET_TEXT_BOLD +
                "   board [<as color>]                          " + RESET_TEXT_BOLD_FAINT + "Print board as it is\n" +
                SET_TEXT_BOLD +
                "   resign                                      " + RESET_TEXT_BOLD_FAINT + "Forfeit the game\n" +
                SET_TEXT_BOLD +
                "   leave                                       " + RESET_TEXT_BOLD_FAINT + "Leave the game, opening the game for another person\n";
    }

    public String helpObserver() {
        return SET_TEXT_BOLD + "OPTIONS:\n" +
                "   show <position>                             " + RESET_TEXT_BOLD_FAINT
                + "Show all possible moves for piece at specified position\n" +
                SET_TEXT_BOLD +
                "   board [<as color>]                          " + RESET_TEXT_BOLD_FAINT + "Print board as it is\n" +
                SET_TEXT_BOLD +
                "   leave                                       " + RESET_TEXT_BOLD_FAINT + "Leave the game\n";
    }

    public ChessMove parseMoves(String start, String end) throws InvalidMoveException {
        return new ChessMove(parsePosition(start), parsePosition(end));
    }

    public String getBoard() {
        return BoardPrinter.printBoard(this.game, color == "observer" ? "white" : color);
    }

    private ChessPosition parsePosition(String position) {
        try {
            if (position.length() == 2) {
                int x = COORDINATES.get(position.charAt(0) + "");
                int y = Integer.parseInt(position.charAt(1) + "");
                return new ChessPosition(y, x);
            }
            throw new InvalidCommand ("invalid move format");
        } catch (NumberFormatException | NullPointerException e) {
            throw new InvalidCommand("invalid move format");
        }
    }

    private static HashMap<String, Integer> generateCoordinateMap() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("d", 4);
        map.put("e", 5);
        map.put("f", 6);
        map.put("g", 7);
        map.put("h", 8);
        return map;
    }

    private String turnColor(ChessGame game) {
        return switch (game.getTeamTurn()) {
            case WHITE -> "white";
            case BLACK -> "black";
        };
    }

}
