package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import server.AlreadyTaken;
import server.InvalidCommand;
import server.ServerFacade;
import server.Unauthorized;

import static ui.EscapeSequences.*;

public class PostJoinClient {

    private final ServerFacade server;
    private final AuthData auth;
    private final boolean gamesExist;
    private int idStart;

    public PostJoinClient(ServerFacade server, AuthData auth) {
        this.server = server;
        this.auth = auth;
        GameData[] games = server.listGames(auth.authToken());
        if (games.length == 0) {
            gamesExist = false;
            idStart = 0;
        } else {
            this.idStart = server.listGames(auth.authToken())[0].gameID();
            gamesExist = true;
        }
    }

    public String eval(String input) {
        String[] command = input.toLowerCase().split(" ");
        return switch (command[0]) {
            case "create" -> createGame(command);
            case "list" -> listGames();
            case "join" -> joinGame(command);
            case "logout" -> logout();
            case "quit" -> quit();
            case "watch" -> watch(command);
            default -> help();
        };
    }

    private String createGame(String[] command) {
        try {
            if (command.length >= 2) {
                int id = server.createGame(command[1], auth.authToken());
                if (!gamesExist) {
                    this.idStart = id;
                }
                return SET_TEXT_COLOR_BLUE + "Game \"" + command[1] + "\" created with ID " + (id - idStart + 1) + RESET_TEXT_COLOR;
            }
            return help();
        } catch (Unauthorized e) {
            return printUnauthenticated();
        }
    }

    public String listGames() {
        try {
            GameData[] games = server.listGames(auth.authToken());
            if (games.length == 0) {
                return SET_TEXT_COLOR_BLUE + "No games available. Create a new game using the \"create\" command." + RESET_TEXT_COLOR;
            }
            StringBuilder result = new StringBuilder(SET_TEXT_COLOR_BLUE).append("LIST OF AVAILABLE GAMES:")
                    .append(RESET_TEXT_COLOR).append("\n");
            result.append(SET_TEXT_BOLD).append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK)
                    .append(printTableRow("ID", "GAME NAME", "WHITE   ", "BLACK   "))
                    .append(RESET_TEXT_BOLD_FAINT).append(RESET_TEXT_COLOR).append(RESET_BG_COLOR).append("\n");
            for (GameData game : games) {
                result.append(printTableRow(String.valueOf(game.gameID() - idStart + 1),
                        game.gameName(), game.whiteUsername(), game.blackUsername())).append("\n");
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
                server.joinGame(id + idStart - 1, command[2].toUpperCase(), auth.authToken());
                System.out.println(SET_TEXT_COLOR_BLUE + "joining game " + id + "..." + RESET_TEXT_COLOR);
                System.out.println(printGame(new ChessGame(), command[2].toLowerCase()));
                return "okay";
            }
            return help();
        } catch (Unauthorized e) {
            return printUnauthenticated();
        } catch (AlreadyTaken e) {
            return SET_TEXT_COLOR_RED +
                    "That team is already taken in that game. Join as a different color or join a different game." + RESET_TEXT_COLOR;
        } catch (InvalidCommand e) {
            return SET_TEXT_COLOR_RED + "Game with ID " + command[1] +
                    " does not exist. Use \"list\" command to see available games." + RESET_TEXT_COLOR;
        } catch (Exception e) {
            return help();
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


    private String printTableRow(String id, String name, String white, String black) {
        String whiteUsername = white == null ? "AVAILABLE" : white;
        String blackUsername = black == null ? "AVAILABLE" : black;
        return String.format("%4s%16s%16s%16s", id, name, whiteUsername, blackUsername);
    }

    private String printUnauthenticated() {
        System.out.println(SET_TEXT_COLOR_RED + "There was an error authenticating you with the server\nPlease log in again.\n");
        return "logout";
    }

    private String printGame(chess.ChessGame game, String team) {
        return BoardPrinter.printBoard(game, team);
    }

    private String logout() {
        return "logout";
    }

    private String watch(String[] command) {
        return printGame(new ChessGame(), "white");
    }
}
