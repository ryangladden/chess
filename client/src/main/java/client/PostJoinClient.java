package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import model.AuthData;
import model.GameData;
import server.AlreadyTaken;
import server.ServerFacade;
import server.Unauthorized;

import static ui.EscapeSequences.*;

public class PostJoinClient {

    private final ServerFacade server;
    private final AuthData auth;

    public PostJoinClient(ServerFacade server, AuthData auth) {
        this.server = server;
        this.auth = auth;
    }

    public String eval(String input) {
        String[] command = input.toLowerCase().split(" ");
        return switch(command[0]) {
            case "create" -> createGame(command);
            case "list" -> listGames();
            case "join" -> joinGame(command);
            case "quit" -> quit();
            default -> help();
        };
    }

    private String createGame(String[] command) {
        try {
            if (command.length >= 2) {
                int id = server.createGame(command[1], auth.authToken());
                return SET_TEXT_COLOR_BLUE + "Game \"" + command[1] + "\" created with ID " + id + RESET_TEXT_COLOR;
            }
            return help();
        } catch (Unauthorized e) {
            return printUnauthenticated();
        }
    }

    private String listGames() {
        try {
            GameData[] games = server.listGames(auth.authToken());
            StringBuilder result = new StringBuilder();
            printListHeaders();
            for (GameData game : games) {
                result.append(parseGameData(game));
            }
            return result.toString();
        } catch (Unauthorized e) {
            return printUnauthenticated();
        }
    }

    private String joinGame(String[] command) {
        try {
            if (command.length >= 3) {
                int id = Integer.parseInt(command[1]);
                if (!command[2].equalsIgnoreCase("black") && !command[2].equalsIgnoreCase("white")) {
                    return help();
                }
                server.joinGame(id, command[2].toUpperCase(), auth.authToken());
                System.out.println(printGame(new ChessGame(), command[2].toLowerCase()));
                return SET_TEXT_COLOR_BLUE + "joining game " + id + "..." + RESET_TEXT_COLOR;
            }
            return help();
        } catch (Unauthorized e) {
           return printUnauthenticated();
        } catch (AlreadyTaken e) {
            return SET_TEXT_COLOR_RED + "That team is already taken in that game. Join as a different color or join a different game" + RESET_TEXT_COLOR;
        }
    }

    private String quit() {
        server.logout(auth.authToken());
        return "quit";
    }

    public String help() {
        return SET_TEXT_BOLD + "OPTIONS:\n" +
                "   list                                        " + RESET_TEXT_BOLD_FAINT + "List all games\n" +
                SET_TEXT_BOLD +
                "   join <game id> <black/white>                " + RESET_TEXT_BOLD_FAINT + "Join game by id as color\n" +
                SET_TEXT_BOLD +
                "   watch <game id>                             " + RESET_TEXT_BOLD_FAINT + "Watch game\n" +
                SET_TEXT_BOLD +
                "   create <game name>                          " + RESET_TEXT_BOLD_FAINT + "Create a new game with specified name\n" +
                SET_TEXT_BOLD +
                "   help                                        " + RESET_TEXT_BOLD_FAINT + "Show these options\n" +
                SET_TEXT_BOLD +
                "   quit                                        " + RESET_TEXT_BOLD_FAINT + "Quit";
    }

    private String parseGameData(GameData game) {
        String black = game.blackUsername() == null ? SET_TEXT_FAINT + SET_TEXT_BLINKING + "available" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_BLINKING : game.blackUsername();
        String white = game.whiteUsername() == null ? SET_TEXT_FAINT + SET_TEXT_BLINKING + "available\n" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_BLINKING : game.whiteUsername() + "\n";
        return "\t" + game.gameID() + "\t\t" + game.gameName() + "\t\t" + black + "\t\t\t\t" + white;
    }

    private void printListHeaders() {
        System.out.println("\tID\t\tGAME NAME\t\tBLACK USERNAME\t\t\tWHITE USERNAME");
    }

    private String printUnauthenticated() {
        System.out.println(SET_TEXT_COLOR_RED + "There was an error authenticating you with the server\n Please log in.\nThe app will close now.");
        return "quit";
    }

    private String printGame(chess.ChessGame game, String team) {
        return BoardPrinter.printBoard(game, team);
    }
}
