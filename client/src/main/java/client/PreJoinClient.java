package client;

import model.AuthData;
import model.UserData;
import server.*;
import static ui.EscapeSequences.*;

public class PreJoinClient {


    private final ServerFacade server;
    public AuthData auth;

    public PreJoinClient(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        String[] command = input.toLowerCase().split(" ");
        return switch(command[0]) {
            case "login" -> login(command);
            case "register" -> register(command);
            case "help" -> help();
            case "quit" -> quit();
            default -> help();
        };
    }

    private String login(String[] command) {
        try {
            if (command.length >= 3) {
                auth = server.login(new UserData(command[1], command[2], null));
                return "Successfully logged in";
            } else {
                return help();
            }
        } catch (Unauthorized e) {
            return SET_TEXT_COLOR_RED + "Invalid username or password" + RESET_TEXT_COLOR;
        }
    }

    private String register(String[] command) {
        try {
            if (command.length >= 4) {
                auth = server.register(new UserData(command[1], command[2], command[3]));
                return "Successfully registered";
            } else {
                return help();
            }
        } catch (AlreadyTaken e) {
            return SET_TEXT_COLOR_RED + "Looks like that username is already taken; pick a new one and try again" + RESET_TEXT_COLOR;
        }
    }

    public String help() {
        return SET_TEXT_BOLD + "OPTIONS:\n" +
                "   login <username> <password>                 " + RESET_TEXT_BOLD_FAINT + "Log in as an existing user\n" +
                SET_TEXT_BOLD +
                "   register <username> <password> <email>      " + RESET_TEXT_BOLD_FAINT + "Register as a new user\n" +
                SET_TEXT_BOLD +
                "   help                                        " + RESET_TEXT_BOLD_FAINT + "Show these options\n" +
                SET_TEXT_BOLD +
                "   quit                                        " + RESET_TEXT_BOLD_FAINT + "Quit";
    }

    private String quit() {
        return "quit";
    }
}
