package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import server.AlreadyTaken;
import server.InvalidCommand;
import server.ServerFacade;
import server.Unauthorized;

import java.util.HashMap;
import java.util.Set;

import static ui.EscapeSequences.*;

public class PostJoinClient {

    private final ServerFacade server;
    private final AuthData auth;
    private HashMap<Integer, Integer> idConversion = new HashMap<>();
    private int currentId = 0;

    public PostJoinClient(ServerFacade server, AuthData auth) {
        this.server = server;
        this.auth = auth;
        GameData[] games = server.listGames(auth.authToken());
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
                idConversion.put(currentId++, id);
                return SET_TEXT_COLOR_BLUE + "Game \"" + command[1] + "\" created with ID " + (currentId - 1) + RESET_TEXT_COLOR;
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
            idConversion = new HashMap<>();
            int count = 1;
            for (GameData game : games) {
                idConversion.put(count, game.gameID());
                result.append(printTableRow(String.valueOf(count), game.gameName(), game.whiteUsername(), game.blackUsername())).append("\n");
                count++;
            }
            currentId = count;
            return result.toString();
        } catch (Unauthorized e) {
            return printUnauthenticated();
        }
    }

    private String joinGame(String[] command) {
        System.out.println("join command entered");
        try {
            if (command.length >= 3) {
                int id = Integer.parseInt(command[1]);
                if (!command[2].equalsIgnoreCase("black") && !command[2].equalsIgnoreCase("white")) {
                    return help();
                }
                server.joinGame(idConversion.get(id), command[2].toUpperCase(), auth.authToken());
                System.out.println(SET_TEXT_COLOR_BLUE + "joining game " + id + "..." + RESET_TEXT_COLOR);
                System.out.println(command[0] + " " + idConversion.get(Integer.parseInt(command[1])) + " " + command[2]);
                return command[0] + " " + idConversion.get(Integer.parseInt(command[1])) + " " + command[2];
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
                "   logout                                      " + RESET_TEXT_BOLD_FAINT + "Log out and return to login cli\n" +
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

    private String logout() {
        return "logout";
    }

    private String watch(String[] command) {
        return "join " + command[1] + " observer";
    }

}
